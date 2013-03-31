package kitchenbench;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DefaultOvenRecipe implements IOvenRecipe {

  private ItemStack ingredient;
  
  private ItemStack result;
  
  private Map<Integer, Integer> enchantments = new HashMap<Integer, Integer>();
  
  private int cookTime = 100;

  private String name;

  public DefaultOvenRecipe(Item ingredient, Item result) {
    this.ingredient = new ItemStack(ingredient);
    this.result = new ItemStack(result);
  }
  
  public DefaultOvenRecipe(ItemStack ingredient, ItemStack result) {
    this.ingredient = ingredient;
    this.result = result;
  }
  
  public DefaultOvenRecipe addEnchantment(int enchantmentId, int strength) {
    enchantments.put(enchantmentId, strength);
    return this;
  }
  
  public DefaultOvenRecipe setCookTimeTicks(int cookTime) {
    this.cookTime = cookTime;
    return this;
  }
  
  public DefaultOvenRecipe setCookTimeSeconds(int cookTime) {
    this.cookTime = cookTime * 20;
    return this;
  }
  
  public DefaultOvenRecipe setName(String name) {
    this.name = name;
    result.setItemName(name);
    return this;
  }

  @Override
  public boolean isIngredient(ItemStack item) {
    return ingredient.isItemEqual(item) && ItemStack.areItemStackTagsEqual(ingredient, item);    
  }

  @Override
  public int getCookTime() {    
    return cookTime;
  }

  @Override
  public ItemStack getCookedItem() {       
    if(!enchantments.isEmpty()) {
      EnchantmentHelper.setEnchantments(enchantments, result);
    }    
    return result.copy();
  }
  
}
