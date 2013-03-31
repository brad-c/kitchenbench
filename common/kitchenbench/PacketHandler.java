package kitchenbench;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

  @Override
  public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
    
    
    ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    
    TileEntityOven oven = null;
    World world = KitchenBench.proxy.getClientWorld();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileEntityOven) {      
      oven = (TileEntityOven) te;
    } else {
      return;
    }
    
    short facing = data.readByte();
    short temperature = data.readShort();
    short progress = data.readShort();
    
    ItemStack[] inv = oven.getInventory();
    for(int i=0; i < inv.length; i++) {
      int itemId = data.readInt();
      int size = data.readInt();
      if(itemId < 0 || size <= 0) {
        inv[i] = null;
      } else {
        inv[i] = new ItemStack(itemId, size, 0);
      }
    }
    
    oven.setFacing(facing);
    oven.setTemperature(temperature);
    oven.setProgress(progress);
    
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
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
      dos.writeByte(facing);     
      dos.writeShort(temperature);
      dos.writeShort(progress);
      
      ItemStack[] inv = oven.getInventory();      
      for(int i=0; i < inv.length; i++) {
        ItemStack st = inv[i];
        dos.writeInt(st == null ? -1 : st.itemID);
        dos.writeInt(st == null ? -1 : st.stackSize);
      }
      
    } catch (IOException e) {
      //never thrown
    }
    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = "KitchenBench";
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }

}
