package kitchenbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

  public void registerRenderers() {
  }
  
  public int addArmor(String armor){
    return 0;
  }
  
//  public void registerServerTickHandler() {
//    TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
//  }

  
  public Object getGuiElementForClient(int iD, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }
  
  public World getClientWorld() {
    return null;
  }
  
  
}
