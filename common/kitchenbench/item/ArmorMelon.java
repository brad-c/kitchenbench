package kitchenbench.item;

import java.util.HashMap;
import java.util.Map;

import kitchenbench.CommonProxy;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.IArmorTextureProvider;

public class ArmorMelon {

  public static ArmorMelon create(int id, CommonProxy proxy) {
    ArmorMelon ma = new ArmorMelon(id, proxy);
    ma.init();
    return ma;
  }

  private final EnumArmorMaterial material;

  private int lastId;

  private MelonArmorItem chestplate;
  private MelonArmorItem helmet;
  private MelonArmorItem leggings;
  private MelonArmorItem boots;
  
  private int rendererPrefix;

  private ArmorMelon(int id, CommonProxy proxy) {
    
    rendererPrefix = proxy.addArmor("MellonArmor");
    
    material = EnumHelper.addArmorMaterial("Mellon", 24, new int[] { 3, 8, 6, 3 }, 25);       
    
    lastId = id;
    chestplate = new MelonArmorItem(lastId, 1, "melonChestplate", EnchantmentDamage.protection.effectId, 2);
    
    lastId++;    
    helmet = new MelonArmorItem(lastId, 0, "melonHelmet",EnchantmentDamage.respiration.effectId, 1);
    
    lastId++;    
    leggings = new MelonArmorItem(lastId, 2, "melonLeggings",EnchantmentDamage.protection.effectId, 2);
    
    lastId++;
    boots = new MelonArmorItem(lastId, 3, "melonBoots",EnchantmentDamage.featherFalling.effectId, 4);
    
  }

  private void init() {    
    LanguageRegistry.addName(chestplate, "Melon Chestplate");
    GameRegistry.addRecipe(new ItemStack(chestplate), "x x", "xxx", "xxx", 'x', new ItemStack(Block.melon));
    
    LanguageRegistry.addName(helmet, "Melon Helmet");
    GameRegistry.addRecipe(new ItemStack(helmet), "xxx", "x x", "   ", 'x', new ItemStack(Block.melon));
    
    LanguageRegistry.addName(leggings, "Melon Leggings");
    GameRegistry.addRecipe(new ItemStack(leggings), "xxx", "x x", "x x", 'x', new ItemStack(Block.melon));
    
    LanguageRegistry.addName(boots, "Melon Boots");
    GameRegistry.addRecipe(new ItemStack(boots), "   ", "x x", "x x", 'x', new ItemStack(Block.melon));
  }

  public int getLastId() {
    return lastId;
  }

  public MelonArmorItem getChestplate() {
    return chestplate;
  }

  public MelonArmorItem getHelmet() {
    return helmet;
  }

  public void setHelmet(MelonArmorItem helmet) {
    this.helmet = helmet;
  }

  public MelonArmorItem getBoots() {
    return boots;
  }

  public void setBoots(MelonArmorItem boots) {
    this.boots = boots;
  }

  public void setChestplate(MelonArmorItem chestplate) {
    this.chestplate = chestplate;
  }

  private String getTexture(ItemStack itemstack) {
    int id = itemstack.itemID;
    if (id == chestplate.itemID || id == boots.itemID || id == helmet.itemID) {
      return "/mods/kitchenbench/textures/armor/MelonArmor_1.png";
    }
    if (id == leggings.itemID) {
      return "/mods/kitchenbench/textures/armor/MelonArmor_2.png";
    }
    return null;
  }

  private class MelonArmorItem extends ItemArmor implements IArmorTextureProvider {

    private String name;
    private int enchantment;
    private int enchantmentStrength;

    public MelonArmorItem(int id, int type, String name, int enchantment, int enchantmentStrength) {
      super(id, material, rendererPrefix, type); 
      this.name = name;
      this.enchantment = enchantment;
      this.enchantmentStrength = enchantmentStrength;
      setMaxStackSize(1);
      setCreativeTab(CreativeTabs.tabCombat);
      setUnlocalizedName(name);
    }

    @Override
    public void updateIcons(IconRegister iconRegister) {
      iconIndex = iconRegister.registerIcon("kitchenbench:" + name);
    }

    @Override
    public String getArmorTextureFile(ItemStack itemstack) {
      return getTexture(itemstack);
    }

    @Override
    public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
      super.onCreated(par1ItemStack, par2World, par3EntityPlayer);
      if (enchantment >= 0 && enchantmentStrength > 0) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(enchantment, enchantmentStrength);
        EnchantmentHelper.setEnchantments(map, par1ItemStack);
      }
    }

  }

}
