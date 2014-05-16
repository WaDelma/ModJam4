package delma.colormod.liquifier;

import org.lwjgl.opengl.GL11;

import delma.colormod.Colormod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class LiquifierGui extends GuiContainer {
	private static final ResourceLocation guiTexture = new ResourceLocation(
			Colormod.MODID, "textures/gui/container/liquifier.png");
	private IInventory inventoryPlayer;

	public LiquifierGui(LiquifierContainer container) {
		super(container);
		this.inventoryPlayer = container.getPlayerInventory();
		this.allowUserInput = false;
		this.ySize = 133;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	protected void drawGuiContainerForegroundLayer(int a, int b) {
		fontRendererObj.drawString(getPlayerInvName(), 8, ySize - 96 + 2,
				4210752);
	}

	private String getPlayerInvName() {
		return inventoryPlayer.hasCustomInventoryName() ? inventoryPlayer
				.getInventoryName() : I18n.format(
				inventoryPlayer.getInventoryName(), new Object[0]);
	}

	protected void drawGuiContainerBackgroundLayer(float a, int b, int c) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(guiTexture);
		int centerHor = (width - xSize) / 2;
		int centerVer = (height - ySize) / 2;
		drawTexturedModalRect(centerHor, centerVer, 0, 0, xSize, ySize);
	}
}
