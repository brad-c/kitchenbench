package kitchenbench;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.network.NetworkModHandler;

import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipesArmor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityOven extends TileEntity implements IInventory {

  private static final short COOK_TIME_TICKS = 100; //5 seconds 
  
  private static final short BURN_TIME_TICKS = 600; //30 seconds of heat per piece of fuel
  
  public short facing = 3;

  private short progress = 0;
  
  private short temperature = 0;      
  
  private IOvenRecipe recipeCooking = null;

  public ItemStack[] inventory;

  private int ticksSinceSync = -1;
  
  private boolean firstUpdate = true;

  public TileEntityOven() {
    inventory = new ItemStack[3];
  }

  @Override
  public String getInvName() {
    return "Kitchen Oven";
  }

  @Override
  public boolean isInvNameLocalized() {
    return false;
  }

  public short getFacing() {
    return facing;
  }

  public void setFacing(short facing) {
    this.facing = facing;
  }

  public boolean isActive() {
    return temperature > 0;
  }
  
  public short getTemperature() {
    return temperature;
  }
  
  public void setTemperature(short temperature) {
    this.temperature = temperature;
  }

  public short getProgress() {
    return progress;
  }

  public void setProgress(short progress) {
    this.progress = progress;
  }
  
  public int getFuelRemainingScaled(int scale) {
    return temperature * scale / BURN_TIME_TICKS;
  }
  
  public int getCookProgressScaled(int scale) {
    return progress * scale / COOK_TIME_TICKS;
  }  

  @Override
  public boolean isStackValidForSlot(int i, ItemStack itemStack) {
    System.out.println("___________________ isStackValidForSlot " + i);
    if (i == 0 && OvenRecipes.isCookable(itemStack)) {
      return true;
    } else if (i == 1 && isFuel(itemStack)) {
      return true;
    }
    return false;
  }

  public static boolean isFuel(ItemStack item) {
    return item != null && isFuel(item.itemID);
  }

  public static boolean isFuel(int itemId) {
    return itemId == Item.redstone.itemID;
  } 
  
  public boolean hasFuel() {
    return inventory[1] != null && inventory[1].stackSize > 0;
  }
  
  public boolean hasInput() {
    return inventory[0] != null && inventory[0].stackSize > 0;
  }

  @Override
  public void updateEntity() {
    super.updateEntity();

    if (worldObj == null || worldObj.isRemote) { // do all updates only on the server                                                 
      return;
    }

    boolean requiresSync = false;

    // First update, send state to client
    if (firstUpdate) {
      firstUpdate = false;
      requiresSync = true;
    }
    
    if(temperature >= 0) {
      --temperature;  
      requiresSync = true;
    }
    
    //Process any current items
    requiresSync |= checkProgress();    
    //Then see if we need to start a new one    
    requiresSync |= cookNextInput();

    if (requiresSync) {
      //this will cause 'getPacketDescription()' to be called and its result will be sent to the PacketHandler on the other end of
      //client/server connection
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

  }    

  protected boolean checkProgress() {
    if(recipeCooking  == null) {
      return false;
    }
    boolean requiresSync = false;
    //Make sure we have some heat
    if(temperature < 0 && hasFuel()) {
      decrStackSize(1, 1);
      temperature = BURN_TIME_TICKS;
      requiresSync = true;
    }    
    //if we do, do some cooking
    if(temperature >= 0) {
      ++progress;
      requiresSync = true;
    }
    //then check if we are done
    if(progress >= COOK_TIME_TICKS) {
      itemCooked();       
      requiresSync = true;
    }    
    return requiresSync;    
  }

  private void itemCooked() {
    ItemStack result = recipeCooking.getCookedItem();
    if(inventory[2] == null) {            
      inventory[2] = result.copy();
    } else {
      inventory[2].stackSize += result.stackSize;
    }
    recipeCooking = null;    
    progress = 0;
  }

  private boolean cookNextInput() {
    IOvenRecipe nextRecipe = canCookNextInput(); 
    if(nextRecipe == null) {  
      return false;
    }
    //Sort out our heat
    if(temperature < 0) {
      decrStackSize(1, 1);
      temperature = BURN_TIME_TICKS;
    }
    //then get our recipe and take away the source items
    recipeCooking = nextRecipe;
    decrStackSize(0, 1);
    return true;
  }
  
  protected IOvenRecipe canCookNextInput() {
    if(recipeCooking != null) {
      return null; //already cooking something
    }
    boolean hasHeatSource = hasFuel() || temperature > 0;    
    if(!hasHeatSource) {
      return null; //no heat to cook
    }
    if(!hasInput()) {
      return null; //nothing to cook
    }
    
    IOvenRecipe nextRecipe = OvenRecipes.getRecipe(inventory[0]);
    if(nextRecipe == null) {
      return null; //no recipe
    }  
    
   //make sure we can merge the recipe output with our result
    if(inventory[2] == null) {
      return nextRecipe;
    }
    ItemStack cookedItem = nextRecipe.getCookedItem();
    if(inventory[2].stackSize + cookedItem.stackSize > inventory[2].getMaxStackSize()) {
      return null; //no room for output
    }    
    if(cookedItem.isItemEqual(inventory[2]) && ItemStack.areItemStackTagsEqual(inventory[2], cookedItem)) {     
      return nextRecipe;
    } //else can't merge the current output and this items output 
    
    return null;    
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  @Override
  public boolean isUseableByPlayer(EntityPlayer player) {
    if (worldObj == null) {
      return true;
    }
    if (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this) {
      return false;
    }
    return player.getDistanceSq((double) xCoord + 0.5D, (double) yCoord + 0.5D, (double) zCoord + 0.5D) <= 64D;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);

    facing = nbtRoot.getShort("facing");
    temperature = nbtRoot.getShort("temperature");
    progress = nbtRoot.getShort("progress");

    // read in the inventories contents
    inventory = new ItemStack[this.getSizeInventory()];
    NBTTagList itemList = nbtRoot.getTagList("Items");

    for (int i = 0; i < itemList.tagCount(); i++) {
      NBTTagCompound itemStack = (NBTTagCompound) itemList.tagAt(i);
      byte slot = itemStack.getByte("Slot");
      if (slot >= 0 && slot < inventory.length) {
        inventory[slot] = ItemStack.loadItemStackFromNBT(itemStack);
      }
    }
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);

    nbtRoot.setShort("facing", facing);
    nbtRoot.setShort("temperature", temperature);
    nbtRoot.setShort("progress", progress);

    // write inventory list
    NBTTagList itemList = new NBTTagList();
    for (int i = 0; i < inventory.length; i++) {
      if (inventory[i] != null) {
        NBTTagCompound itemStack = new NBTTagCompound();
        itemStack.setByte("Slot", (byte) i);
        inventory[i].writeToNBT(itemStack);
        itemList.appendTag(itemStack);
      }
    }
    nbtRoot.setTag("Items", itemList);
  }

  // ----- Basic inventory stuff

  ItemStack[] getInventory() {
    return inventory;
  }
  
  @Override
  public int getSizeInventory() {
    return inventory.length;
  }

  @Override
  public int getInventoryStackLimit() {
    return 64;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return inventory[slot];
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if (this.inventory[fromSlot] == null) {
      return null;
    }
    if (inventory[fromSlot].stackSize <= amount) {
      ItemStack result = inventory[fromSlot];
      inventory[fromSlot] = null;
      return result;
    }
    return inventory[fromSlot].splitStack(amount);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    this.inventory[slot] = contents;
    if (contents != null && contents.stackSize > getInventoryStackLimit()) {
      contents.stackSize = getInventoryStackLimit();
    }
  }

  @Override
  public ItemStack getStackInSlotOnClosing(int i) {
    return null;
  }

  @Override
  public void openChest() {
  }

  @Override
  public void closeChest() {
  }
  
}
