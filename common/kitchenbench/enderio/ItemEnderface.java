package kitchenbench.enderio;

import java.lang.reflect.Method;

import kitchenbench.GuiHandler;
import kitchenbench.KitchenBench;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.Item;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ItemEnderface extends Item implements IGuiHandler {

  public static final String KEY_IO_SET = "enderFaceIoSet";
  public static final String KEY_IO_X = "enderFaceIoX";
  public static final String KEY_IO_Y = "enderFaceIoY";
  public static final String KEY_IO_Z = "enderFaceIoZ";

  public static ItemEnderface create(int id) {
    ItemEnderface result = new ItemEnderface(id);
    result.init();
    return result;
  }

  protected ItemEnderface(int id) {
    super(id);
    setCreativeTab(CreativeTabs.tabMisc);
    setUnlocalizedName("enderFace");
    setMaxStackSize(1);
  }

  protected void init() {
    LanguageRegistry.addName(this, "Enderface");
    GameRegistry.addRecipe(new ItemStack(this), " x ", "xyx", " x ", 'x', new ItemStack(Item.enderPearl), 'y', new ItemStack(Item.diamond));
    KitchenBench.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ENDERFACE, this);
  }

  @Override
  public void updateIcons(IconRegister iconRegister) {
    iconIndex = iconRegister.registerIcon("kitchenbench:enderface");
  }

  @Override
  public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
    super.onCreated(itemStack, world, entityPlayer);
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    nbttagcompound.setBoolean(KEY_IO_SET, false);
    nbttagcompound.setInteger(KEY_IO_X, -1);
    nbttagcompound.setInteger(KEY_IO_Y, -1);
    nbttagcompound.setInteger(KEY_IO_Z, -1);
    itemStack.setTagCompound(nbttagcompound);

  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiEnderface(player, world, x, y, z);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStack, World world, final EntityPlayer entityPlayer) {

    if (!world.isRemote) {
      return itemStack;
    }

    NBTTagCompound tag = itemStack.getTagCompound();
    if (tag != null && tag.getBoolean(KEY_IO_SET)) {

      int x = tag.getInteger(KEY_IO_X);
      int y = tag.getInteger(KEY_IO_Y);
      int z = tag.getInteger(KEY_IO_Z);

      entityPlayer.openGui(KitchenBench.instance, GuiHandler.GUI_ID_ENDERFACE, world, x, y, z);
      return itemStack;

      // x++;
      //
      // //entityPlayer.openGui(KitchenBench.instance, 0, world, x, y, z);
      //
      // entityPlayer.sendChatToPlayer("EnderIO Interface Selected : Checking coords: "
      // + x + "," + y + "," + z);
      //
      // Block block = null;
      // int blockId = world.getBlockId(x, y, z);
      // if (blockId >= 0 && blockId < Block.blocksList.length) {
      // block = Block.blocksList[blockId];
      // }
      //
      // if (block != null) {
      // entityPlayer.sendChatToPlayer("Got block at location: " +
      // block.getLocalizedName() + " Player calss is: " +
      // entityPlayer.getClass());
      //
      // EntityPlayerMP proxy = createProxy((EntityPlayerMP)entityPlayer);
      // proxy.playerNetServerHandler =
      // ((EntityPlayerMP)entityPlayer).playerNetServerHandler;
      // System.out.println("Created proxy");
      //
      // ItemInWorldManager itemInWorldManager = new ItemInWorldManager(world);
      // boolean result = itemInWorldManager.activateBlockOrUseItem(proxy,
      // world, null, x, y, z, 0, 0, 0, 0);
      // //boolean result =
      // itemInWorldManager.activateBlockOrUseItem(entityPlayer, world, null, x,
      // y, z, 0, 0, 0, 0);
      // entityPlayer.sendChatToPlayer("Called activate. Result was: " +
      // result);
      //

    }
    entityPlayer.sendChatToPlayer("No block found");

    // }

    return itemStack;
  }

  // @Override
  public ItemStack onItemRightClick2(ItemStack itemStack, World world, final EntityPlayer entityPlayer) {

    NBTTagCompound tag = itemStack.getTagCompound();
    if (tag != null && tag.getBoolean(KEY_IO_SET)) {

      int x = tag.getInteger(KEY_IO_X) + 1;
      int y = tag.getInteger(KEY_IO_Y);
      int z = tag.getInteger(KEY_IO_Z);

      entityPlayer.sendChatToPlayer("EnderIO Interface Selected : Checking coords: " + x + "," + y + "," + z);

      Block block = null;
      int blockId = world.getBlockId(x, y, z);
      if (blockId >= 0 && blockId < Block.blocksList.length) {
        block = Block.blocksList[blockId];
      }

      if (block != null) {
        entityPlayer.sendChatToPlayer("Got block at location: " + block.getLocalizedName() + " Player calss is: " + entityPlayer.getClass());

        ItemInWorldManager itemInWorldManager = new ItemInWorldManager(world);

        if (!world.isRemote) {
          final EntityPlayerMP real = ((EntityPlayerMP) entityPlayer);

          EntityPlayerMP faker = new EntityPlayerMP(real.mcServer, world, real.username, itemInWorldManager) {

            @Override
            public double getDistanceSq(double par1, double par3, double par5) {
              return 1;
            }

            @Override
            public double getDistance(double par1, double par3, double par5) {
              return 1;
            }

            @Override
            public void displayGUIFurnace(TileEntityFurnace par1TileEntityFurnace) {
              super.displayGUIFurnace(par1TileEntityFurnace);
              // real.openContainer = openContainer;
            }

          };
          faker.inventory = real.inventory;
          faker.playerNetServerHandler = real.playerNetServerHandler;

          boolean result = itemInWorldManager.activateBlockOrUseItem(faker, world, null, x, y, z, 0, 0, 0, 0);
          entityPlayer.sendChatToPlayer("Called activate. Result was: " + result);

        }

      } else {
        entityPlayer.sendChatToPlayer("No block found");
      }

    }

    return itemStack;
  }

  public static <T> T createProxy(EntityPlayerMP player) {
    Enhancer e = new Enhancer();
    e.setCallback(new MyInterceptor(player));

    e.setSuperclass(player.getClass());
    // MinecraftServer par1MinecraftServer, World par2World, String par3Str,
    // ItemInWorldManager par4ItemInWorldManager
    Class[] argTypes = new Class[] { MinecraftServer.class, World.class, String.class, ItemInWorldManager.class };
    Object[] args = new Object[] { player.mcServer, player.worldObj, player.username, player.theItemInWorldManager };

    e.setInterceptDuringConstruction(false);
    T proxifiedObj = (T) e.create(argTypes, args);

    return proxifiedObj;
  }

  public static class MyInterceptor implements MethodInterceptor {

    private EntityPlayer realObj;

    public MyInterceptor(EntityPlayer obj) {

      this.realObj = obj;

    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
      System.out.println("Method name is: " + method.getName());
//      if (method.getName().equals("displayGUIFurnace")) {
//        Object res = method.invoke(realObj, objects);
//        realObj.openContainer = new ContainerFurnace(realObj.inventory, (TileEntityFurnace) objects[0]) {
//
//          @Override
//          public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
//            return true;
//          }
//
//        };
//        return res;
//      }
      method.setAccessible(true);
      return method.invoke(realObj, objects);

     
    }

  }

}
