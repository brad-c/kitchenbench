package kitchenbench;

import javax.xml.ws.ServiceMode;

import kitchenbench.oven.BlockOven;
import kitchenbench.oven.DefaultOvenRecipe;
import kitchenbench.oven.OvenContainer;
import kitchenbench.oven.OvenRecipes;
import kitchenbench.oven.TileEntityOven;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(name = "Kitchen Bench", modid = "KitchenBench", version = "0.0.0.0", dependencies = "required-after:Forge@[7.0,);required-after:FML@[5.0.5,)")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "KitchenBench" }, packetHandler = PacketHandler.class)
public class KitchenBench {

  @Instance("KitchenBench")
  public static KitchenBench instance;

  // Says where the client and server 'proxy' code is loaded.
  @SidedProxy(clientSide = "kitchenbench.ClientProxy", serverSide = "kitchenbench.CommonProxy")
  public static CommonProxy proxy;

  private static final int STARTING_ITEM_ID = 6000;

  private static final int STARTING_BLOCK_ID = 600;

  //private int nextItemId = STARTING_ITEM_ID;

  private int nextBlockId = STARTING_BLOCK_ID;

  public static Items items;
  
  public static Blocks blocks;
  
  public static GuiHandler guiHandler = new GuiHandler();
  

  @PreInit
  public void preInit(FMLPreInitializationEvent event) {
    // Load properties file
  }

  @Init
  public void load(FMLInitializationEvent event) {

    instance = this;
    
    proxy.registerRenderers();
    
    NetworkRegistry.instance().registerGuiHandler(this, guiHandler);
    
    //proxy.registerServerTickHandler();

    items = new Items();
    items.init(STARTING_ITEM_ID, proxy);
    
    blocks = new Blocks();
    blocks.init(STARTING_BLOCK_ID, proxy);        

    addOvenRecipes();        
    
    MinecraftForge.EVENT_BUS.register(this);
    
    

  }

  private void addOvenRecipes() {
    OvenRecipes.addRecipe(new DefaultOvenRecipe(Item.appleRed, items.roastAppleItem).addEnchantment(EnchantmentDamage.sharpness.effectId, 3).setName("Ollies Apple"));
    
    OvenRecipes.addRecipe(new DefaultOvenRecipe(Item.potato, items.roastPotatoItem));
    
    OvenRecipes.addRecipe(new DefaultOvenRecipe(Item.appleRed, items.roastAppleItem));
    
//    ItemStack inputStack;
//    ItemStack resultStack;    
    
//    inputStack = new ItemStack(Item.carrot);
//    resultStack = new ItemStack(Item.goldenCarrot, 2);
//    OvenRecipes.addRecipe(new DefaultOvenRecipe(inputStack,resultStack));
    
//    inputStack = new ItemStack(Block.cobblestone);
//    resultStack = new ItemStack(Block.stoneBrick);
//    OvenRecipes.addRecipe(new DefaultOvenRecipe(inputStack,resultStack));
  }

  @PostInit
  public void postInit(FMLPostInitializationEvent event) {
    // Stub Method
  }
 

}
