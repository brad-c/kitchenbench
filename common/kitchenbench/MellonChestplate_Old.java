package kitchenbench;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

//public class MellonChestplate extends ItemArmor {

public class MellonChestplate_Old extends Item {

  public MellonChestplate_Old(int itemId) {
    // super(par1, par2EnumArmorMaterial, par3, par4);
    super(itemId);
    setMaxStackSize(1);
    setCreativeTab(CreativeTabs.tabCombat);
    setUnlocalizedName("mellonChestplate");
  }

  @Override
  public void updateIcons(IconRegister iconRegister) {
    iconIndex = iconRegister.registerIcon("kitchenbench:mellonChestplate");
  }
}
