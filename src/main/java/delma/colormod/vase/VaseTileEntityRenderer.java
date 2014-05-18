package delma.colormod.vase;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import org.lwjgl.opengl.GL11;

public class VaseTileEntityRenderer extends TileEntitySpecialRenderer {

	public IIcon getBlockIconFromSideAndMetadata(Block block, int side, int meta) {
		return getIconSafe(block.getIcon(side, meta));
	}

	public IIcon getIconSafe(IIcon icon) {
		if (icon == null) {
			icon = ((TextureMap) Minecraft.getMinecraft().getTextureManager()
					.getTexture(TextureMap.locationBlocksTexture))
					.getAtlasSprite("missingno");
		}
		return icon;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y,
			double z, float partialTick) {
		VaseTileEntity tileEntity = (VaseTileEntity) tile;
		VaseBlock block = (VaseBlock) tile.blockType;
		IBlockAccess world = tile.getWorldObj();
		Tessellator t = Tessellator.instance;
		int xx = tile.xCoord;
		int yy = tile.yCoord;
		int zz = tile.zCoord;

		GL11.glPushMatrix();
		t.startDrawingQuads();
		t.setBrightness(block.getMixedBrightnessForBlock(world, xx, yy, zz));

		int colorMultiplier = block.colorMultiplier(world, xx, yy, zz);
		float red = (colorMultiplier >> 16 & 255) / 255.0F;
		float green = (colorMultiplier >> 8 & 255) / 255.0F;
		float blue = (colorMultiplier & 255) / 255.0F;
		if (EntityRenderer.anaglyphEnable) {
			float tempRed = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
			float tempGreen = (red * 30.0F + green * 70.0F) / 100.0F;
			float tempBlue = (red * 30.0F + blue * 70.0F) / 100.0F;
			red = tempRed;
			green = tempGreen;
			blue = tempBlue;
		}
		t.setColorOpaque_F(red, green, blue);

		IIcon icon;

		IFluidTank tank = tileEntity.getTank();
		float procents = tank.getFluidAmount() / (float) tank.getCapacity();
		FluidStack fluidStack = tank.getFluid();
		float thickness = 0.4999f;
		if (fluidStack != null) {
			icon = getIconSafe(fluidStack.getFluid().getFlowingIcon());
			renderThickCube(t, icon, x, y, z, thickness * procents);
		}

		icon = getBlockIconFromSideAndMetadata(block, 0,
				tile.getBlockMetadata());
		renderThickCube(t, icon, x, y, z, thickness);

		t.draw();
		GL11.glPopMatrix();
	}

	private void renderThickCube(Tessellator t, IIcon icon, double x, double y,
			double z, double thickness) {
		double xMin = x + 0.5D - thickness;
		double xMax = x + 0.5D + thickness;

		double zMin = z + 0.5D - thickness;
		double zMax = z + 0.5D + thickness;

		double yMin = y + 0.5D - thickness;
		double yMax = y + 0.5D + thickness;

		double minU = icon.getMinU();
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();

		renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin, zMax, yMin,
				yMax);
	}

	private void renderCube(Tessellator t, double minU, double maxU,
			double minV, double maxV, double xMin, double xMax, double zMin,
			double zMax, double yMin, double yMax) {
		t.addVertexWithUV(xMin, yMax, zMin, minU, minV);
		t.addVertexWithUV(xMin, yMin, zMin, minU, maxV);
		t.addVertexWithUV(xMax, yMin, zMin, maxU, maxV);
		t.addVertexWithUV(xMax, yMax, zMin, maxU, minV);
		t.addVertexWithUV(xMax, yMax, zMin, minU, minV);
		t.addVertexWithUV(xMax, yMin, zMin, minU, maxV);
		t.addVertexWithUV(xMin, yMin, zMin, maxU, maxV);
		t.addVertexWithUV(xMin, yMax, zMin, maxU, minV);

		t.addVertexWithUV(xMin, yMax, zMax, minU, minV);
		t.addVertexWithUV(xMin, yMin, zMax, minU, maxV);
		t.addVertexWithUV(xMax, yMin, zMax, maxU, maxV);
		t.addVertexWithUV(xMax, yMax, zMax, maxU, minV);
		t.addVertexWithUV(xMax, yMax, zMax, minU, minV);
		t.addVertexWithUV(xMax, yMin, zMax, minU, maxV);
		t.addVertexWithUV(xMin, yMin, zMax, maxU, maxV);
		t.addVertexWithUV(xMin, yMax, zMax, maxU, minV);

		t.addVertexWithUV(xMin, yMax, zMin, minU, minV);
		t.addVertexWithUV(xMin, yMin, zMin, minU, maxV);
		t.addVertexWithUV(xMin, yMin, zMax, maxU, maxV);
		t.addVertexWithUV(xMin, yMax, zMax, maxU, minV);
		t.addVertexWithUV(xMin, yMax, zMax, minU, minV);
		t.addVertexWithUV(xMin, yMin, zMax, minU, maxV);
		t.addVertexWithUV(xMin, yMin, zMin, maxU, maxV);
		t.addVertexWithUV(xMin, yMax, zMin, maxU, minV);

		t.addVertexWithUV(xMax, yMax, zMin, minU, minV);
		t.addVertexWithUV(xMax, yMin, zMin, minU, maxV);
		t.addVertexWithUV(xMax, yMin, zMax, maxU, maxV);
		t.addVertexWithUV(xMax, yMax, zMax, maxU, minV);
		t.addVertexWithUV(xMax, yMax, zMax, minU, minV);
		t.addVertexWithUV(xMax, yMin, zMax, minU, maxV);
		t.addVertexWithUV(xMax, yMin, zMin, maxU, maxV);
		t.addVertexWithUV(xMax, yMax, zMin, maxU, minV);

		t.addVertexWithUV(xMax, yMin, zMin, minU, minV);
		t.addVertexWithUV(xMin, yMin, zMin, minU, maxV);
		t.addVertexWithUV(xMin, yMin, zMax, maxU, maxV);
		t.addVertexWithUV(xMax, yMin, zMax, maxU, minV);
		t.addVertexWithUV(xMax, yMin, zMax, minU, minV);
		t.addVertexWithUV(xMin, yMin, zMax, minU, maxV);
		t.addVertexWithUV(xMin, yMin, zMin, maxU, maxV);
		t.addVertexWithUV(xMax, yMin, zMin, maxU, minV);

		t.addVertexWithUV(xMax, yMax, zMin, minU, minV);
		t.addVertexWithUV(xMin, yMax, zMin, minU, maxV);
		t.addVertexWithUV(xMin, yMax, zMax, maxU, maxV);
		t.addVertexWithUV(xMax, yMax, zMax, maxU, minV);
		t.addVertexWithUV(xMax, yMax, zMax, minU, minV);
		t.addVertexWithUV(xMin, yMax, zMax, minU, maxV);
		t.addVertexWithUV(xMin, yMax, zMin, maxU, maxV);
		t.addVertexWithUV(xMax, yMax, zMin, maxU, minV);
	}
}
