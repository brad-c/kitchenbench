package kitchenbench.item;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public class ItemRoastApple extends ItemFood {
  
  public static ItemRoastApple create(int id) {
    ItemRoastApple result = new ItemRoastApple(id);
    result.init();
    return result;
  }

  protected ItemRoastApple(int id) {
    super(id, 5, 0.4f, false);
    setCreativeTab(CreativeTabs.tabFood);
    setUnlocalizedName("roastApple");    
  }
  
  protected void init() {
    LanguageRegistry.addName(this, "Roast Apple");    
  }
  
  @Override
  public void updateIcons(IconRegister iconRegister) {
    iconIndex = iconRegister.registerIcon("kitchenbench:roastApple");
  }

}
