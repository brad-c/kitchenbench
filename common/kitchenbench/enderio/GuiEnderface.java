package kitchenbench.enderio;

import kitchenbench.GuiHandler;
import kitchenbench.KitchenBench;
import kitchenbench.PacketHandler;
import cpw.mods.fml.client.GuiSlotModList;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;

public class GuiEnderface extends GuiScreen {

  private final EntityPlayer player;
  private final World world;
  private final int x;
  private final int y;
  private final int z;

  public GuiEnderface(EntityPlayer player, World world, int x, int y, int z) {
    this.player = player;
    this.world = world;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Adds the buttons (and other controls) to the screen in question.
   */
  public void initGui() {
    int size = 32;
    int hsize = size /2;
    int spacing = 16;
    this.buttonList.add(new GuiButton(0, this.width / 2 - hsize, this.height/2 - spacing - size, size, size, "S"));
    this.buttonList.add(new GuiButton(1, this.width / 2 - hsize, this.height/2 + spacing, size, size, "N"));
    this.buttonList.add(new GuiButton(2, this.width / 2 + spacing, this.height / 2 - hsize, size, size, "E"));
    this.buttonList.add(new GuiButton(3, this.width / 2 - spacing - size, this.height / 2 - hsize, size, size, "W"));
  }
  

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  /**
   * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
   */
  protected void actionPerformed(GuiButton button) {
    int targetX = x;
    int targetZ = z;
    if (button.enabled) {
      switch (button.id) {
      case 0:
        player.sendChatToPlayer("South");
        targetZ++;
        break;
      case 1:
        player.sendChatToPlayer("North");
        targetZ--;
        break;
      case 2:
        player.sendChatToPlayer("East");
        targetX++;
        break;
      case 3:
        player.sendChatToPlayer("West");
        targetX--;
        break;
      }
    }
    openInterface(targetX, y, targetZ);
    
  }
  
  protected void openInterface(int x, int y, int z) {                
      Block block = null;
      int blockId = world.getBlockId(x, y, z);
      if (blockId >= 0 && blockId < Block.blocksList.length) {
        block = Block.blocksList[blockId];
      }

      if (block != null) {
        player.sendChatToPlayer("Got block at location: " + block.getLocalizedName());       
          
        Packet250CustomPayload pkt = PacketHandler.getPacketEnderface(x,y,z);
        PacketDispatcher.sendPacketToServer(pkt);
        
        player.sendChatToPlayer("Sent packet to server. ");
        
        
      } else {
        player.sendChatToPlayer("No block found");
      }

    
  }

}
