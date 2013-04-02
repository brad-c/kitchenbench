package kitchenbench.item;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

public class ItemRoastPotato extends ItemFood {

  public static ItemRoastPotato create(int id) {
    ItemRoastPotato result = new ItemRoastPotato(id);
    result.init();
    return result;
  }

  protected ItemRoastPotato(int id) {
    super(id, 8, 0.6f, false);
    setCreativeTab(CreativeTabs.tabFood);
    setUnlocalizedName("roastPotato");    
  }
  
  protected void init() {
    LanguageRegistry.addName(this, "Roast Potato");
  }
  
  @Override
  public void updateIcons(IconRegister iconRegister) {
    iconIndex = iconRegister.registerIcon("kitchenbench:roastPotato");
  }

}
