package kitchenbench;

import java.util.Iterator;
import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOven extends BlockContainer {

  public static boolean canSmelt(ItemStack item) {
    return item.itemID != Item.redstone.itemID;
  }

  public static boolean isFuel(ItemStack item) {
    return item.itemID == Item.redstone.itemID;
  }

  public static BlockOven create(int blockId, CommonProxy proxy) {
    BlockOven oven = new BlockOven(blockId, proxy);
    oven.init();
    return oven;
  }

  @SideOnly(Side.CLIENT)
  private Icon[][] iconBuffer;

  private final Random random;

  private BlockOven(int blockId, CommonProxy proxy) {
    super(blockId, Material.iron);
    setHardness(2.0F);
    setStepSound(soundMetalFootstep);
    setUnlocalizedName("kitchenOven");
    setCreativeTab(CreativeTabs.tabDecorations);
    random = new Random();
  }

  private void init() {
    LanguageRegistry.addName(this, "Kitchen Oven");
    GameRegistry.registerBlock(this, "kitchenOven");
    GameRegistry.registerTileEntity(TileEntityOven.class, "kitchenOvenEntity");
    GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx", 'x', new ItemStack(Block.cobblestone), 'y', new ItemStack(Block.glass));
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileEntityOven();
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
    if (entityPlayer.isSneaking()) {
      return false;
    }
    entityPlayer.openGui(KitchenBench.instance, 0, world, x, y, z);
    return true;
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {

    iconBuffer = new Icon[1][12];
    // first the 6 sides in OFF state
    iconBuffer[0][0] = iconRegister.registerIcon("kitchenbench:ovenBottom");
    iconBuffer[0][1] = iconRegister.registerIcon("kitchenbench:ovenTop");
    iconBuffer[0][2] = iconRegister.registerIcon("kitchenbench:ovenSide");
    iconBuffer[0][3] = iconRegister.registerIcon("kitchenbench:ovenFrontOff");
    iconBuffer[0][4] = iconRegister.registerIcon("kitchenbench:ovenSide");
    iconBuffer[0][5] = iconRegister.registerIcon("kitchenbench:ovenSide");

    iconBuffer[0][6] = iconRegister.registerIcon("kitchenbench:ovenBottom");
    iconBuffer[0][7] = iconRegister.registerIcon("kitchenbench:ovenTop");
    iconBuffer[0][8] = iconRegister.registerIcon("kitchenbench:ovenSide");
    iconBuffer[0][9] = iconRegister.registerIcon("kitchenbench:ovenFrontOn");
    iconBuffer[0][10] = iconRegister.registerIcon("kitchenbench:ovenSide");
    iconBuffer[0][11] = iconRegister.registerIcon("kitchenbench:ovenSide");
  }

  @Override
  public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int blockSide) {
    // used to render the block in the world
    TileEntity te = world.getBlockTileEntity(x, y, z);
    int facing = 0;
    if (te instanceof TileEntityOven) {
      TileEntityOven ote = (TileEntityOven) te;
      facing = ote.facing;
    }
    if (isActive(world, x, y, z)) {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing] + 6];
    } else {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing]];
    }
  }

  @Override
  public Icon getBlockTextureFromSideAndMetadata(int blockSide, int blockMeta) {
    // This is used to render the block as an item  
    return iconBuffer[0][blockSide];
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, int par5, int par6) {    
    TileEntityOven tileentitychest = (TileEntityOven) world.getBlockTileEntity(x, y, z);
    if (tileentitychest != null) {
      dropContent(0, tileentitychest, world, tileentitychest.xCoord, tileentitychest.yCoord, tileentitychest.zCoord);
    }
    super.breakBlock(world, x, y, z, par5, par6);
  }

  public void dropContent(int newSize, TileEntityOven inventory, World world, int xCoord, int yCoord, int zCoord) {
    for (int i = newSize; i < inventory.getSizeInventory(); i++) {
      ItemStack itemstack = inventory.getStackInSlot(i);
      if (itemstack == null) {
        continue;
      }
      float f = random.nextFloat() * 0.8F + 0.1F;
      float f1 = random.nextFloat() * 0.8F + 0.1F;
      float f2 = random.nextFloat() * 0.8F + 0.1F;

      EntityItem entityitem = new EntityItem(world, (float) xCoord + f, (float) yCoord + (newSize > 0 ? 1 : 0) + f1, (float) zCoord + f2, new ItemStack(
          itemstack.itemID, itemstack.stackSize, itemstack.getItemDamage()));
      float f3 = 0.05F;
      entityitem.motionX = (float) random.nextGaussian() * f3;
      entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
      entityitem.motionZ = (float) random.nextGaussian() * f3;
      if (itemstack.hasTagCompound()) {
        entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
      }
      world.spawnEntityInWorld(entityitem);
    }
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
    super.onBlockPlacedBy(world, x, y, z, player, stack);
    int heading = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    TileEntityOven te = (TileEntityOven) world.getBlockTileEntity(x, y, z);
    switch (heading) {
    case 0:
      te.setFacing((short) 2);
      break;
    case 1:
      te.setFacing((short) 5);
      break;
    case 2:
      te.setFacing((short) 3);
      break;
    case 3:
      te.setFacing((short) 4);
      break;
    }
    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public void onBlockAdded(World world, int x, int y, int z) {
    super.onBlockAdded(world, x, y, z);
    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    // If active, randomly throw some smoke around
    if (isActive(world, x, y, z)) {
      float startX = (float) x + 1.0F;
      float startY = (float) y + 1.0F;
      float startZ = (float) z + 1.0F;
      for (int i = 0; i < 4; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;
        world.spawnParticle("smoke", (double) (startX + xOffset), (double) (startY + yOffset), (double) (startZ + zOffset), 0.0D, 0.0D, 0.0D);
      }
    }
  }

  private boolean isActive(IBlockAccess blockAccess, int x, int y, int z) {
    return ((TileEntityOven) blockAccess.getBlockTileEntity(x, y, z)).isActive();
  }

}
