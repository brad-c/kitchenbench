package kitchenbench.item;

import kitchenbench.KitchenBench;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ItemGoldenRoastApple extends ItemFood {
  
  public static ItemGoldenRoastApple create(int id) {
    ItemGoldenRoastApple result = new ItemGoldenRoastApple(id);
    result.init();
    return result;
  }
  
  protected ItemGoldenRoastApple(int id) {
    super(id, 5, 0.4f, false);
    setCreativeTab(CreativeTabs.tabFood);
    setAlwaysEdible();
    setPotionEffect(Potion.regeneration.id, 7, 0, 1.0F);
    setUnlocalizedName("roastedApple");    
  }
  
  protected void init() {
    LanguageRegistry.addName(this, "Golden Roast Apple");    
    GameRegistry.addRecipe(new ItemStack(this), " x ", "xyx", " x ", 'x', new ItemStack(Item.goldNugget), 'y', KitchenBench.items.roastAppleItem);
  }
  
  @Override
  public void updateIcons(IconRegister iconRegister) {
    iconIndex = iconRegister.registerIcon("kitchenbench:roastAppleGolden");
  }

}
