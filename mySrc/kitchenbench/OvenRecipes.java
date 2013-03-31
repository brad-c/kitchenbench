package kitchenbench;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;

public final class OvenRecipes {
    
  public static Set<IOvenRecipe> recipes = new HashSet<IOvenRecipe>();
  
  public static void addRecipe(IOvenRecipe recipe) {
    if(recipe != null) {
      recipes.add(recipe);
    }
  }
  
  public static boolean isCookable(ItemStack stack) {
    return getRecipe(stack) != null;
  }
  
  public static IOvenRecipe getRecipe(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    for(IOvenRecipe recipe : recipes) {
      if(recipe.isIngredient(stack)) {
        return recipe;
      }
    }
    return null;
  }
  
  private OvenRecipes() {    
  }

}
