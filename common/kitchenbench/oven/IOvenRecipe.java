package kitchenbench.oven;

import net.minecraft.item.ItemStack;

public interface IOvenRecipe {

  boolean isIngredient(ItemStack stack);
  
  int getCookTime();
  
  ItemStack getCookedItem();
     
}
