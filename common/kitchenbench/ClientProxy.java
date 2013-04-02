package kitchenbench;

import kitchenbench.oven.GuiOven;
import kitchenbench.oven.TileEntityOven;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

  public static int[][] sideAndFacingToSpriteOffset;

  @Override
  public void registerRenderers() {
    //@formatter:off
    sideAndFacingToSpriteOffset = new int[][] {

        { 3, 2, 0, 0, 0, 0 }, 
        { 2, 3, 1, 1, 1, 1 }, 
        { 1, 1, 3, 2, 5, 4 }, 
        { 0, 0, 2, 3, 4, 5 }, 
        { 4, 5, 4, 5, 3, 2 },
        { 5, 4, 5, 4, 2, 3 }       
    };
    //@formatter:on
  }

  public int addArmor(String armor) {
    return RenderingRegistry.addNewArmourRendererPrefix(armor);
  }

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }
  
//  @Override
//  public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
//    return null;
//  }
//  
//  @Override
//  public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {    
//    player.sendChatToPlayer("From server GUI id is: " + id);
//    IGuiHandler handler = guiHandlers.get(id);
//    if(handler != null) {
//      return handler.getServerGuiElement(id, player, world, x, y, z);
//    }
//    return null;
//  }

}
