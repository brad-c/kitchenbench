package kitchenbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class OvenContainer extends Container {

  private TileEntityOven tileEntity;
  private int progress = 0;

  public OvenContainer(InventoryPlayer playerInv, TileEntityOven tileEntity) {

    this.tileEntity = tileEntity;

    addSlotToContainer(new Slot(tileEntity, 0, 56, 17) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return OvenRecipes.isCookable(par1ItemStack);
      }      
    });
    addSlotToContainer(new Slot(tileEntity, 1, 56, 53) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return TileEntityOven.isFuel(par1ItemStack);
      }
    });
    addSlotToContainer(new Slot(tileEntity, 2, 116, 35) {
      @Override
      public boolean isItemValid(ItemStack par1ItemStack) {
        return false;
      }
    });
    
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (int i = 0; i < 9; ++i) {
      addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
    }

  }

  @Override
  public boolean canInteractWith(EntityPlayer entityplayer) {
    return tileEntity.isUseableByPlayer(entityplayer);
  }

  
  public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotIndex) {
    ItemStack itemstack = null;
    Slot slot = (Slot) inventorySlots.get(slotIndex);

    if (slot != null && slot.getHasStack()) {
      ItemStack origStack = slot.getStack();
      itemstack = origStack.copy();

      if (slotIndex == 2) {
        
        if (!mergeItemStack(origStack, 3, 39, true)) {
          return null;
        }
        slot.onSlotChange(origStack, itemstack);
        
      } else if (slotIndex != 1 && slotIndex != 0) {
        
        if (OvenRecipes.isCookable(origStack)) {
          if (!this.mergeItemStack(origStack, 0, 1, false)) {
            return null;
          }
        } else if (TileEntityOven.isFuel(origStack)) {
          if (!this.mergeItemStack(origStack, 1, 2, false)) {
            return null;
          }
        } else if (slotIndex >= 3 && slotIndex < 30) {
          if (!this.mergeItemStack(origStack, 30, 39, false)) {
            return null;
          }
        } else if (slotIndex >= 30 && slotIndex < 39 && !this.mergeItemStack(origStack, 3, 30, false)) {
          return null;
        }
        
      } else if (!mergeItemStack(origStack, 3, 39, false)) {
        return null;
      }

      if (origStack.stackSize == 0) {
        slot.putStack((ItemStack) null);
      } else {
        slot.onSlotChanged();
      }

      if (origStack.stackSize == itemstack.stackSize) {
        return null;
      }

      slot.onPickupFromSlot(entityPlayer, origStack);
    }

    return itemstack;
  }
 

}
