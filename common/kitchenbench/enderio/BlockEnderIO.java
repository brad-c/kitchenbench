package kitchenbench.enderio;

import kitchenbench.KitchenBench;
import kitchenbench.oven.TileEntityOven;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEnderIO extends Block {

  public static BlockEnderIO create(int id) {
    BlockEnderIO result = new BlockEnderIO(id);
    result.init();
    return result;
  }

  private Icon icon;

  private BlockEnderIO(int id) {
    super(id, Material.rock);
    setHardness(0.5F);
    setStepSound(Block.soundStoneFootstep);
    setUnlocalizedName("enderIO");
    setCreativeTab(CreativeTabs.tabMisc);
  }

  private void init() {
    LanguageRegistry.addName(this, "Ender IO");
    GameRegistry.registerBlock(this, "enderIO");
    GameRegistry.addRecipe(new ItemStack(this), " x ", "xyx", " x ", 'x', new ItemStack(Item.diamond), 'y', new ItemStack(Block.enderChest));
  }    

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {    
    if (entityPlayer.isSneaking()) {
      return false;
    }    
    if (entityPlayer.getCurrentEquippedItem().itemID == KitchenBench.items.enderfaceItem.itemID) {
      ItemStack enderFaceStack = entityPlayer.getCurrentEquippedItem();
      NBTTagCompound nbttagcompound = enderFaceStack.getTagCompound();
      if (nbttagcompound == null) {
        nbttagcompound = new NBTTagCompound();
      }
      nbttagcompound.setBoolean(ItemEnderface.KEY_IO_SET, true);
      nbttagcompound.setInteger(ItemEnderface.KEY_IO_X, x);
      nbttagcompound.setInteger(ItemEnderface.KEY_IO_Y, y);
      nbttagcompound.setInteger(ItemEnderface.KEY_IO_Z, z);
      enderFaceStack.setTagCompound(nbttagcompound);   
      
      entityPlayer.setCurrentItemOrArmor(0, enderFaceStack);
            
      if(world.isRemote) {
        entityPlayer.sendChatToPlayer("EnderIO Interface Selected");
      }

      // TODO: Need to register this enderFace so we can unlink it if the block
      // is destroyed
    }
    return true;

  }

  @Override
  public void onBlockDestroyedByPlayer(World par1World, int par2, int par3, int par4, int par5) {
    super.onBlockDestroyedByPlayer(par1World, par2, par3, par4, par5);
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    icon = iconRegister.registerIcon("kitchenbench:enderIO");
  }

  @Override
  public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int blockSide) {
    return icon;
  }

  @Override
  public Icon getBlockTextureFromSideAndMetadata(int blockSide, int blockMeta) {
    // This is used to render the block as an item
    return icon;
  }

}
