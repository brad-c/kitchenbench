package kitchenbench;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOven extends GuiContainer {

  private TileEntityOven ovenEntity;

  public GuiOven(InventoryPlayer par1InventoryPlayer, TileEntityOven furnaceInventory) {
    super(new OvenContainer(par1InventoryPlayer, furnaceInventory));
    this.ovenEntity = furnaceInventory;
  }

  /**
   * Draw the foreground layer for the GuiContainer (everything in front of the
   * items)
   */
  protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    String s = this.ovenEntity.isInvNameLocalized() ? this.ovenEntity.getInvName() : StatCollector.translateToLocal(this.ovenEntity.getInvName());
    this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
    this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.renderEngine.bindTexture("/mods/kitchenbench/textures/gui/oven.png");
    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;
    
    drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
    int i1;

    if (ovenEntity.isActive()) {
      i1 = ovenEntity.getFuelRemainingScaled(12);
      drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
    }

    i1 = ovenEntity.getCookProgressScaled(24);
    drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
  }
}
