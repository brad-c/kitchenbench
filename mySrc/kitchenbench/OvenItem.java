package kitchenbench;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

public class OvenItem extends ItemBlock {

  public OvenItem(int id) {
    super(id);
    setMaxDamage(0);
    setHasSubtypes(false);
    setUnlocalizedName("ovenItem");
    setCreativeTab(CreativeTabs.tabDecorations);
  }
  
}
