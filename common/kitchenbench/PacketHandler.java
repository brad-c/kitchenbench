package kitchenbench;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import kitchenbench.oven.TileEntityOven;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.OpenGuiPacket;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

  public static int ID_OVEN = 1;
  public static int ID_ENDERFACE = 2;

  @Override
  public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

    if (packet.data != null && packet.data.length <= 0) {
      return;
    }

    ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
    int id = data.readInt();
    if (id == ID_OVEN) {
      handleOvenPacket(data);
    } else if (id == ID_ENDERFACE) {
      handleEnderfacePacket(data, manager, player);
    }

  }

  public static Packet getPacket(TileEntityOven oven) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    int x = oven.xCoord;
    int y = oven.yCoord;
    int z = oven.zCoord;
    short facing = oven.getFacing();
    short temperature = oven.getTemperature();
    short progress = oven.getProgress();
    try {
      dos.writeInt(ID_OVEN);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
      dos.writeByte(facing);
      dos.writeShort(temperature);
      dos.writeShort(progress);

      ItemStack[] inv = oven.getInventory();
      for (int i = 0; i < inv.length; i++) {
        ItemStack st = inv[i];
        dos.writeInt(st == null ? -1 : st.itemID);
        dos.writeInt(st == null ? -1 : st.stackSize);
      }

    } catch (IOException e) {
      // never thrown
    }
    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = "KitchenBench";
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }

  private void handleOvenPacket(ByteArrayDataInput data) {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();

    World world = KitchenBench.proxy.getClientWorld();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    TileEntityOven oven = null;
    if (te instanceof TileEntityOven) {
      oven = (TileEntityOven) te;
    } else {
      return;
    }

    short facing = data.readByte();
    short temperature = data.readShort();
    short progress = data.readShort();

    ItemStack[] inv = oven.getInventory();
    for (int i = 0; i < inv.length; i++) {
      int itemId = data.readInt();
      int size = data.readInt();
      if (itemId < 0 || size <= 0) {
        inv[i] = null;
      } else {
        inv[i] = new ItemStack(itemId, size, 0);
      }
    }

    oven.setFacing(facing);
    oven.setTemperature(temperature);
    oven.setProgress(progress);
  }

  public static Packet250CustomPayload getPacketEnderface(int x, int y, int z) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(ID_ENDERFACE);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
    } catch (IOException e) {
      // never thrown
    }
    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = "KitchenBench";
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }

  private void handleEnderfacePacket(ByteArrayDataInput data, INetworkManager manager, Player p) {
    if (!(p instanceof EntityPlayerMP)) {
      System.out.println("PacketHandler:handleEnderfacePacket: ______________________________ Not an EntityPlayerMP ______________________________");
      return;
    }
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();

    EntityPlayerMP player = (EntityPlayerMP) p;
    EntityPlayerMP proxy = createProxy(player);

    player.theItemInWorldManager.activateBlockOrUseItem(proxy, player.worldObj, null, x, y, z, 0, 0, 0, 0);

    System.out.println("PacketHandler:handleEnderfacePacket: ______________________________ Activated the block ______________________________");

  }

  public static <T> T createProxy(EntityPlayerMP player) {
    Enhancer e = new Enhancer();
    e.setCallback(new PlayerProxy(player));

    e.setSuperclass(player.getClass());
    Class[] argTypes = new Class[] { MinecraftServer.class, World.class, String.class, ItemInWorldManager.class };
    Object[] args = new Object[] { player.mcServer, player.worldObj, player.username, player.theItemInWorldManager };

    e.setInterceptDuringConstruction(false);
    T proxifiedObj = (T) e.create(argTypes, args);

    return proxifiedObj;
  }

  public static class PlayerProxy implements MethodInterceptor {

    private EntityPlayerMP realObj;

    public PlayerProxy(EntityPlayerMP obj) {

      this.realObj = obj;

    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
      System.out.println("Method name is: " + method.getName());
      if (method.getName().equals("displayGUIFurnace")) {
        Object res = method.invoke(realObj, objects);
        realObj.openContainer = new ContainerFurnace(realObj.inventory, (TileEntityFurnace) objects[0]) {
          @Override
          public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
            return true;
          }

        };
        realObj.openContainer.windowId = realObj.currentWindowId;
        realObj.openContainer.addCraftingToCrafters(realObj);
        return res;
      } else if (method.getName().equals("openGui")) {

        ModContainer mc = FMLCommonHandler.instance().findContainerFor(objects[0]);
        if (mc == null) {
          NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(objects[0]);
          if (nmh != null) {
            mc = nmh.getContainer();
          } else {
            FMLLog.warning("A mod tried to open a gui on the server without being a NetworkMod");
            method.setAccessible(true);
            return method.invoke(realObj, objects);
          }
        }
        // NetworkRegistry.instance().openRemoteGui(mc, (EntityPlayerMP)
        // realObj, ((Integer)objects[1]).intValue(),realObj.worldObj,
        // ((Integer)objects[1]).intValue(), ((Integer)objects[2]).intValue(),
        // ((Integer)objects[3]).intValue());
        openRemoteGui(mc, (EntityPlayerMP) realObj, ((Integer) objects[1]).intValue(), realObj.worldObj, ((Integer) objects[1]).intValue(),
            ((Integer) objects[2]).intValue(), ((Integer) objects[3]).intValue());

      }
      method.setAccessible(true);
      return method.invoke(realObj, objects);

    }

    void openRemoteGui(ModContainer mc, EntityPlayerMP player, int modGuiId, World world, int x, int y, int z) {

      IGuiHandler handler = null;
      
      try {        
        Field f = NetworkRegistry.class.getDeclaredField("serverGuiHandlers");
        f.setAccessible(true);        
        handler = (IGuiHandler) f.get(NetworkRegistry.instance());
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
      

      // IGuiHandler handler =
      // NetworkRegistry.instance().serverGuiHandlers.get(mc);
      
      NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
      if (handler != null && nmh != null) {
        Container container = (Container) handler.getServerGuiElement(modGuiId, player, world, x, y, z);
        if (container != null) {
          player.incrementWindowID();
          player.closeInventory();
          int windowId = player.currentWindowId;
          // Packet250CustomPayload pkt = new Packet250CustomPayload();
          // pkt.channel = "FML";
          // pkt.data = FMLPacket.makePacket(FMLPacket.Type.GUIOPEN, windowId,
          // nmh.getNetworkId(), modGuiId, x, y, z);
          // pkt.length = pkt.data.length;

          OpenGuiPacket gp = new OpenGuiPacket();
          byte[] pakdata = gp.generatePacket(windowId, nmh.getNetworkId(), modGuiId, x, y, z);
          Bytes.concat(new byte[] { UnsignedBytes.checkedCast(4) }, pakdata);

          Packet250CustomPayload pkt = new Packet250CustomPayload();
          pkt.channel = "FML";
          pkt.data = pakdata;
          pkt.length = pkt.data.length;

          player.playerNetServerHandler.sendPacketToPlayer(pkt);
          player.openContainer = container;
          player.openContainer.windowId = windowId;
          player.openContainer.addCraftingToCrafters(player);
        }
      }

    }
  }

}
