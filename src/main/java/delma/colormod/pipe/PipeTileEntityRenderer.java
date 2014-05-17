package delma.colormod.pipe;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import org.lwjgl.opengl.GL11;

public class PipeTileEntityRenderer extends TileEntitySpecialRenderer {
	private static final float THICKNESS = PipeBlock.THICKNESS;

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
		PipeTileEntity tileEntity = (PipeTileEntity) tile;
		PipeBlock block = (PipeBlock) tile.blockType;
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
		IIcon icon1;

		IFluidTank tank = tileEntity.getTank();
		float procents = tank.getFluidAmount() / (float) tank.getCapacity();
		FluidStack fluidStack = tank.getFluid();
		if (fluidStack != null) {
			icon = getIconSafe(fluidStack.getFluid().getFlowingIcon());
			icon1 = icon;
			renderPipe(tile, x, y, z, t, THICKNESS * procents, icon, icon1);
		}

		// if (this.hasOverrideBlockTexture())
		// {
		// iicon = this.overrideBlockTexture;
		// iicon1 = this.overrideBlockTexture;
		// }
		// else
		// {

		icon = getBlockIconFromSideAndMetadata(block, 0,
				tile.getBlockMetadata());
		icon1 = icon;// block.func_150097_e();
		// }

		renderPipe(tile, x, y, z, t, THICKNESS, icon, icon1);

		t.draw();
		GL11.glPopMatrix();
	}

	private void renderPipe(TileEntity tileEntity, double x, double y,
			double z, Tessellator t, float thickness, IIcon icon, IIcon icon1) {
		PipeTileEntity tile = (PipeTileEntity) tileEntity;
		PipeBlock block = (PipeBlock) tile.blockType;
		World world = tile.getWorldObj();
		int height = world.getHeight();

		int xx = tile.xCoord;
		int yy = tile.yCoord;
		int zz = tile.zCoord;

		double xStart = x;
		double xMin = x + 0.5D - thickness;
		double xMax = x + 0.5D + thickness;
		double xEnd = x + 1;

		double zStart = z;
		double zMin = z + 0.5D - thickness;
		double zMax = z + 0.5D + thickness;
		double zEnd = z + 1;

		double yStart = y;
		double yMin = y + 0.5D - thickness;
		double yMax = y + 0.5D + thickness;
		double yEnd = y + 1;

		double minU = icon.getMinU();
		double uAt8 = icon.getInterpolatedU(8.0D);
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();

		double uAt7 = icon1.getInterpolatedU(7.0D);
		double uAt9 = icon1.getInterpolatedU(9.0D);
		double minU2 = icon1.getMinU();
		double maxU2 = icon1.getMaxU();
		double minV2 = icon1.getMinV();
		double vAt8 = icon1.getInterpolatedV(8.0D);
		double maxV2 = icon1.getMaxV();

		boolean north = block.canConnectTo(world, xx, yy, zz - 1, NORTH);
		boolean south = block.canConnectTo(world, xx, yy, zz + 1, SOUTH);
		boolean west = block.canConnectTo(world, xx - 1, yy, zz, WEST);
		boolean east = block.canConnectTo(world, xx + 1, yy, zz, EAST);
		boolean down = block.canConnectTo(world, xx, yy - 1, zz, DOWN);
		boolean up = block.canConnectTo(world, xx, yy + 1, zz, UP);
		boolean renderTop = block
				.shouldSideBeRendered(world, xx, yy + 1, zz, 1);
		boolean renderBot = block
				.shouldSideBeRendered(world, xx, yy - 1, zz, 0);

		double weAdd = 0.0001D;
		double nsAdd = -0.0001D;

		boolean oneIs = west || east || north || south || down || up;

		if (!oneIs) {
			renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin, zMax, yMin,
					yMax);
		} else {
			if ((!west || !east) && oneIs) {
				if (west) {
					renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin,
							zMax, yMin, yMax);
					t.addVertexWithUV(xStart, yMax, zMin, minU, minV);
					t.addVertexWithUV(xStart, yMin, zMin, minU, maxV);
					t.addVertexWithUV(xMin, yMin, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yMax, zMin, uAt8, minV);
					t.addVertexWithUV(xMin, yMax, zMin, minU, minV);
					t.addVertexWithUV(xMin, yMin, zMin, minU, maxV);
					t.addVertexWithUV(xStart, yMin, zMin, uAt8, maxV);
					t.addVertexWithUV(xStart, yMax, zMin, uAt8, minV);

					t.addVertexWithUV(xStart, yMax, zMax, uAt8, maxV);
					t.addVertexWithUV(xStart, yMin, zMax, uAt8, minV);
					t.addVertexWithUV(xMin, yMin, zMax, minU, minV);
					t.addVertexWithUV(xMin, yMax, zMax, minU, maxV);
					t.addVertexWithUV(xMin, yMax, zMax, uAt8, maxV);
					t.addVertexWithUV(xMin, yMin, zMax, uAt8, minV);
					t.addVertexWithUV(xStart, yMin, zMax, minU, minV);
					t.addVertexWithUV(xStart, yMax, zMax, minU, maxV);

					if (!south && !north) {
						t.addVertexWithUV(xMin, yMax, zMax, uAt7, minV2);
						t.addVertexWithUV(xMin, yMin, zMax, uAt7, maxV2);
						t.addVertexWithUV(xMin, yMin, zMin, uAt9, maxV2);
						t.addVertexWithUV(xMin, yMax, zMin, uAt9, minV2);
						t.addVertexWithUV(xMin, yMax, zMin, uAt7, minV2);
						t.addVertexWithUV(xMin, yMin, zMin, uAt7, maxV2);
						t.addVertexWithUV(xMin, yMin, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMin, yMax, zMax, uAt9, minV2);
					}

					if (renderTop || yy < height - 1
							&& world.isAirBlock(xx - 1, yy + 1, zz)) {
						t.addVertexWithUV(xStart, yMax + weAdd, zMax, minU,
								vAt8);
						t.addVertexWithUV(xMin, yMax + weAdd, zMax, minU, maxV2);
						t.addVertexWithUV(xMin, yMax + weAdd, zMin, maxU, maxV2);
						t.addVertexWithUV(xStart, yMax + weAdd, zMin, maxU,
								vAt8);
						t.addVertexWithUV(xMin, yMax + weAdd, zMax, minU, vAt8);
						t.addVertexWithUV(xStart, yMax + weAdd, zMax, minU,
								maxV2);
						t.addVertexWithUV(xStart, yMax + weAdd, zMin, maxU,
								maxV2);
						t.addVertexWithUV(xMin, yMax + weAdd, zMin, maxU, vAt8);
					}

					if (renderBot || yy > 1
							&& world.isAirBlock(xx - 1, yy - 1, zz)) {
						t.addVertexWithUV(xStart, yMin - weAdd, zMax, minU,
								vAt8);
						t.addVertexWithUV(xMin, yMin - weAdd, zMax, minU, maxV2);
						t.addVertexWithUV(xMin, yMin - weAdd, zMin, maxU, maxV2);
						t.addVertexWithUV(xStart, yMin - weAdd, zMin, maxU,
								vAt8);
						t.addVertexWithUV(xMin, yMin - weAdd, zMax, minU, vAt8);
						t.addVertexWithUV(xStart, yMin - weAdd, zMax, minU,
								maxV2);
						t.addVertexWithUV(xStart, yMin - weAdd, zMin, maxU,
								maxV2);
						t.addVertexWithUV(xMin, yMin - weAdd, zMin, maxU, vAt8);
					}
				} else if (east) {
					renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin,
							zMax, yMin, yMax);
					t.addVertexWithUV(xMax, yMax, zMin, uAt8, minV);
					t.addVertexWithUV(xMax, yMin, zMin, uAt8, maxV);
					t.addVertexWithUV(xEnd, yMin, zMin, maxU, maxV);
					t.addVertexWithUV(xEnd, yMax, zMin, maxU, minV);
					t.addVertexWithUV(xEnd, yMax, zMin, uAt8, minV);
					t.addVertexWithUV(xEnd, yMin, zMin, uAt8, maxV);
					t.addVertexWithUV(xMax, yMin, zMin, maxU, maxV);
					t.addVertexWithUV(xMax, yMax, zMin, maxU, minV);

					t.addVertexWithUV(xMax, yMax, zMax, maxU, maxV);
					t.addVertexWithUV(xMax, yMin, zMax, maxU, minV);
					t.addVertexWithUV(xEnd, yMin, zMax, uAt8, minV);
					t.addVertexWithUV(xEnd, yMax, zMax, uAt8, maxV);
					t.addVertexWithUV(xEnd, yMax, zMax, maxU, maxV);
					t.addVertexWithUV(xEnd, yMin, zMax, maxU, minV);
					t.addVertexWithUV(xMax, yMin, zMax, uAt8, minV);
					t.addVertexWithUV(xMax, yMax, zMax, uAt8, maxV);

					if (!south && !north) {
						t.addVertexWithUV(xMax, yMax, zMin, uAt7, minV2);
						t.addVertexWithUV(xMax, yMin, zMin, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMax, zMax, uAt9, minV2);
						t.addVertexWithUV(xMax, yMax, zMax, uAt7, minV2);
						t.addVertexWithUV(xMax, yMin, zMax, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin, zMin, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMax, zMin, uAt9, minV2);
					}

					if (renderTop || yy < height - 1
							&& world.isAirBlock(xx + 1, yy + 1, zz)) {
						t.addVertexWithUV(xMax, yMax + weAdd, zMax, minU, minV2);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMax, minU, vAt8);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMin, maxU, vAt8);
						t.addVertexWithUV(xMax, yMax + weAdd, zMin, maxU, minV2);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMax, minU, minV2);
						t.addVertexWithUV(xMax, yMax + weAdd, zMax, minU, vAt8);
						t.addVertexWithUV(xMax, yMax + weAdd, zMin, maxU, vAt8);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMin, maxU, minV2);
					}

					if (renderBot || yy > 1
							&& world.isAirBlock(xx + 1, yy - 1, zz)) {
						t.addVertexWithUV(xMax, yMin - weAdd, zMax, minU, minV2);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMax, minU, vAt8);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMin, maxU, vAt8);
						t.addVertexWithUV(xMax, yMin - weAdd, zMin, maxU, minV2);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMax, minU, minV2);
						t.addVertexWithUV(xMax, yMin - weAdd, zMax, minU, vAt8);
						t.addVertexWithUV(xMax, yMin - weAdd, zMin, maxU, vAt8);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMin, maxU, minV2);
					}
				}
			} else {
				t.addVertexWithUV(xStart, yMax, zMin, minU, minV);
				t.addVertexWithUV(xStart, yMin, zMin, minU, maxV);
				t.addVertexWithUV(xEnd, yMin, zMin, maxU, maxV);
				t.addVertexWithUV(xEnd, yMax, zMin, maxU, minV);
				t.addVertexWithUV(xEnd, yMax, zMin, minU, minV);
				t.addVertexWithUV(xEnd, yMin, zMin, minU, maxV);
				t.addVertexWithUV(xStart, yMin, zMin, maxU, maxV);
				t.addVertexWithUV(xStart, yMax, zMin, maxU, minV);

				t.addVertexWithUV(xStart, yMax, zMax, minU, minV);
				t.addVertexWithUV(xStart, yMin, zMax, minU, maxV);
				t.addVertexWithUV(xEnd, yMin, zMax, maxU, maxV);
				t.addVertexWithUV(xEnd, yMax, zMax, maxU, minV);
				t.addVertexWithUV(xEnd, yMax, zMax, minU, minV);
				t.addVertexWithUV(xEnd, yMin, zMax, minU, maxV);
				t.addVertexWithUV(xStart, yMin, zMax, maxU, maxV);
				t.addVertexWithUV(xStart, yMax, zMax, maxU, minV);

				if (renderTop) {
					t.addVertexWithUV(xStart, yMax + weAdd, zMax, minU, maxV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMax, minU, minV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMin, maxU, minV2);
					t.addVertexWithUV(xStart, yMax + weAdd, zMin, maxU, maxV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMax, minU, maxV2);
					t.addVertexWithUV(xStart, yMax + weAdd, zMax, minU, minV2);
					t.addVertexWithUV(xStart, yMax + weAdd, zMin, maxU, minV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMin, maxU, maxV2);
				} else {
					if (yy < height - 1 && world.isAirBlock(xx - 1, yy + 1, zz)) {
						t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9,
								vAt8);
						t.addVertexWithUV(xMax, yMax + weAdd, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMax + weAdd, zMin, uAt7, maxV2);
						t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7,
								vAt8);
						t.addVertexWithUV(xMax, yMax + weAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9,
								maxV2);
						t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7,
								maxV2);
						t.addVertexWithUV(xMax, yMax + weAdd, zMin, uAt7, vAt8);
					}

					if (yy < height - 1 && world.isAirBlock(xx + 1, yy + 1, zz)) {
						t.addVertexWithUV(xMax, yMax + weAdd, zMax, uAt9, minV2);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, vAt8);
						t.addVertexWithUV(xMax, yMax + weAdd, zMin, uAt7, minV2);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, minV2);
						t.addVertexWithUV(xMax, yMax + weAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xMax, yMax + weAdd, zMin, uAt7, vAt8);
						t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, minV2);
					}
				}

				if (renderBot) {
					t.addVertexWithUV(xStart, yMin - weAdd, zMax, minU, maxV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMax, minU, minV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMin, maxU, minV2);
					t.addVertexWithUV(xStart, yMin - weAdd, zMin, maxU, maxV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMax, minU, maxV2);
					t.addVertexWithUV(xStart, yMin - weAdd, zMax, minU, minV2);
					t.addVertexWithUV(xStart, yMin - weAdd, zMin, maxU, minV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMin, maxU, maxV2);
				} else {
					if (yy > 1 && world.isAirBlock(xx - 1, yy - 1, zz)) {
						t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9,
								vAt8);
						t.addVertexWithUV(xMax, yMin - weAdd, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMin - weAdd, zMin, uAt7, maxV2);
						t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7,
								vAt8);
						t.addVertexWithUV(xMax, yMin - weAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9,
								maxV2);
						t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7,
								maxV2);
						t.addVertexWithUV(xMax, yMin - weAdd, zMin, uAt7, vAt8);
					}

					if (yy > 1 && world.isAirBlock(xx + 1, yy - 1, zz)) {
						t.addVertexWithUV(xMax, yMin - weAdd, zMax, uAt9, minV2);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, vAt8);
						t.addVertexWithUV(xMax, yMin - weAdd, zMin, uAt7, minV2);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, minV2);
						t.addVertexWithUV(xMax, yMin - weAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xMax, yMin - weAdd, zMin, uAt7, vAt8);
						t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, minV2);
					}
				}
			}

			if ((!north || !south) && oneIs) {
				if (north) {
					renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin,
							zMax, yMin, yMax);
					t.addVertexWithUV(xMin, yMax, zStart, uAt8, minV);
					t.addVertexWithUV(xMin, yMin, zStart, uAt8, maxV);
					t.addVertexWithUV(xMin, yMin, zMin, minU, maxV);
					t.addVertexWithUV(xMin, yMax, zMin, minU, minV);
					t.addVertexWithUV(xMin, yMax, zMin, uAt8, minV);
					t.addVertexWithUV(xMin, yMin, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yMin, zStart, minU, maxV);
					t.addVertexWithUV(xMin, yMax, zStart, minU, minV);

					t.addVertexWithUV(xMax, yMax, zStart, minU, maxV);
					t.addVertexWithUV(xMax, yMin, zStart, minU, minV);
					t.addVertexWithUV(xMax, yMin, zMin, uAt8, minV);
					t.addVertexWithUV(xMax, yMax, zMin, uAt8, maxV);
					t.addVertexWithUV(xMax, yMax, zMin, minU, maxV);
					t.addVertexWithUV(xMax, yMin, zMin, minU, minV);
					t.addVertexWithUV(xMax, yMin, zStart, uAt8, minV);
					t.addVertexWithUV(xMax, yMax, zStart, uAt8, maxV);

					if (!east && !west) {
						t.addVertexWithUV(xMin, yMax, zMin, uAt7, minV2);
						t.addVertexWithUV(xMin, yMin, zMin, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin, zMin, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMax, zMin, uAt9, minV2);
						t.addVertexWithUV(xMax, yMax, zMin, uAt7, minV2);
						t.addVertexWithUV(xMax, yMin, zMin, uAt7, maxV2);
						t.addVertexWithUV(xMin, yMin, zMin, uAt9, maxV2);
						t.addVertexWithUV(xMin, yMax, zMin, uAt9, minV2);
					}

					if (renderTop || yy < height - 1
							&& world.isAirBlock(xx, yy + 1, zz - 1)) {
						t.addVertexWithUV(xMin, yMax + nsAdd, zStart, minU2,
								minV2);
						t.addVertexWithUV(xMin, yMax + nsAdd, zMin, minU2, vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMin, maxU2, vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zStart, maxU2,
								minV2);
						t.addVertexWithUV(xMin, yMax + nsAdd, zMin, minU2,
								minV2);
						t.addVertexWithUV(xMin, yMax + nsAdd, zStart, minU2,
								vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zStart, maxU2,
								vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMin, maxU2,
								minV2);
					}

					if (renderBot || yy > 1
							&& world.isAirBlock(xx, yy - 1, zz - 1)) {
						t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9,
								minV2);
						t.addVertexWithUV(xMin, yMin - nsAdd, zMin, uAt9, vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMin, uAt7, vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7,
								minV2);
						t.addVertexWithUV(xMin, yMin - nsAdd, zMin, uAt9, minV2);
						t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9,
								vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7,
								vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMin, uAt7, minV2);
					}
				} else if (south) {
					renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin,
							zMax, yMin, yMax);
					t.addVertexWithUV(xMin, yMax, zMax, maxU, minV);
					t.addVertexWithUV(xMin, yMin, zMax, maxU, maxV);
					t.addVertexWithUV(xMin, yMin, zEnd, uAt8, maxV);
					t.addVertexWithUV(xMin, yMax, zEnd, uAt8, minV);
					t.addVertexWithUV(xMin, yMax, zEnd, maxU, minV);
					t.addVertexWithUV(xMin, yMin, zEnd, maxU, maxV);
					t.addVertexWithUV(xMin, yMin, zMax, uAt8, maxV);
					t.addVertexWithUV(xMin, yMax, zMax, uAt8, minV);

					t.addVertexWithUV(xMax, yMax, zMax, uAt8, maxV);
					t.addVertexWithUV(xMax, yMin, zMax, uAt8, minV);
					t.addVertexWithUV(xMax, yMin, zEnd, maxU, minV);
					t.addVertexWithUV(xMax, yMax, zEnd, maxU, maxV);
					t.addVertexWithUV(xMax, yMax, zEnd, uAt8, maxV);
					t.addVertexWithUV(xMax, yMin, zEnd, uAt8, minV);
					t.addVertexWithUV(xMax, yMin, zMax, maxU, minV);
					t.addVertexWithUV(xMax, yMax, zMax, maxU, maxV);

					if (!east && !west) {
						t.addVertexWithUV(xMax, yMax, zMax, uAt7, minV2);
						t.addVertexWithUV(xMax, yMin, zMax, uAt7, maxV2);
						t.addVertexWithUV(xMin, yMin, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMin, yMax, zMax, uAt9, minV2);
						t.addVertexWithUV(xMin, yMax, zMax, uAt7, minV2);
						t.addVertexWithUV(xMin, yMin, zMax, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMax, zMax, uAt9, minV2);
					}

					if (renderTop || yy < height - 1
							&& world.isAirBlock(xx, yy + 1, zz + 1)) {
						t.addVertexWithUV(xMin, yMax + nsAdd, zMax, maxU2, vAt8);
						t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, maxU2,
								maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, minU2,
								maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMax, minU2, vAt8);
						t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, maxU2, vAt8);
						t.addVertexWithUV(xMin, yMax + nsAdd, zMax, maxU2,
								maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMax, minU2,
								maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, minU2, vAt8);
					}

					if (renderBot || yy > 1
							&& world.isAirBlock(xx, yy - 1, zz + 1)) {
						t.addVertexWithUV(xMin, yMin - nsAdd, zMax, uAt7, vAt8);
						t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, vAt8);
						t.addVertexWithUV(xMin, yMin - nsAdd, zMax, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, vAt8);
					}
				}
			} else {
				t.addVertexWithUV(xMin, yMax, zEnd, minU, minV);
				t.addVertexWithUV(xMin, yMin, zEnd, minU, maxV);
				t.addVertexWithUV(xMin, yMin, zStart, maxU, maxV);
				t.addVertexWithUV(xMin, yMax, zStart, maxU, minV);
				t.addVertexWithUV(xMin, yMax, zStart, minU, minV);
				t.addVertexWithUV(xMin, yMin, zStart, minU, maxV);
				t.addVertexWithUV(xMin, yMin, zEnd, maxU, maxV);
				t.addVertexWithUV(xMin, yMax, zEnd, maxU, minV);

				t.addVertexWithUV(xMax, yMax, zEnd, minU, minV);
				t.addVertexWithUV(xMax, yMin, zEnd, minU, maxV);
				t.addVertexWithUV(xMax, yMin, zStart, maxU, maxV);
				t.addVertexWithUV(xMax, yMax, zStart, maxU, minV);
				t.addVertexWithUV(xMax, yMax, zStart, minU, minV);
				t.addVertexWithUV(xMax, yMin, zStart, minU, maxV);
				t.addVertexWithUV(xMax, yMin, zEnd, maxU, maxV);
				t.addVertexWithUV(xMax, yMax, zEnd, maxU, minV);

				if (renderTop) {
					t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, minU2, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zStart, minU2, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zStart, maxU2, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, maxU2, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zStart, minU2, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, minU2, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, maxU2, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zStart, maxU2, maxV2);
				} else {
					if (yy < height - 1 && world.isAirBlock(xx, yy + 1, zz - 1)) {
						t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt9,
								minV2);
						t.addVertexWithUV(xMin, yMax + nsAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMax, uAt7, vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt7,
								minV2);
						t.addVertexWithUV(xMin, yMax + nsAdd, zMax, uAt9, minV2);
						t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt9,
								vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt7,
								vAt8);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMax, uAt7, minV2);
					}

					if (yy < height - 1 && world.isAirBlock(xx, yy + 1, zz + 1)) {
						t.addVertexWithUV(xMin, yMax + nsAdd, zMax, uAt7, vAt8);
						t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, vAt8);
						t.addVertexWithUV(xMin, yMax + nsAdd, zMax, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, vAt8);
					}
				}

				if (renderBot) {
					t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, minU2, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zStart, minU2, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zStart, maxU2, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, maxU2, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zStart, minU2, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, minU2, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, maxU2, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zStart, maxU2, maxV2);
				} else {
					if (yy > 1 && world.isAirBlock(xx, yy - 1, zz - 1)) {
						t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9,
								minV2);
						t.addVertexWithUV(xMin, yMin - nsAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMax, uAt7, vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7,
								minV2);
						t.addVertexWithUV(xMin, yMin - nsAdd, zMax, uAt9, minV2);
						t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9,
								vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7,
								vAt8);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMax, uAt7, minV2);
					}

					if (yy > 1 && world.isAirBlock(xx, yy - 1, zz + 1)) {
						t.addVertexWithUV(xMin, yMin - nsAdd, zMax, uAt7, vAt8);
						t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMax, uAt9, vAt8);
						t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, vAt8);
						t.addVertexWithUV(xMin, yMin - nsAdd, zMax, uAt7, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zMax, uAt9, maxV2);
						t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, vAt8);
					}
				}
			}

			if ((!down || !up) && oneIs) {
				if (down) {
					renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin,
							zMax, yMin, yMax);
					t.addVertexWithUV(xMax, yStart, zMin, uAt8, minV);
					t.addVertexWithUV(xMin, yStart, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yMin, zMin, minU, maxV);
					t.addVertexWithUV(xMax, yMin, zMin, minU, minV);
					t.addVertexWithUV(xMax, yMin, zMin, uAt8, minV);
					t.addVertexWithUV(xMin, yMin, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yStart, zMin, minU, maxV);
					t.addVertexWithUV(xMax, yStart, zMin, minU, minV);

					t.addVertexWithUV(xMax, yStart, zMax, minU, maxV);
					t.addVertexWithUV(xMin, yStart, zMax, minU, minV);
					t.addVertexWithUV(xMin, yMin, zMax, uAt8, minV);
					t.addVertexWithUV(xMax, yMin, zMax, uAt8, maxV);
					t.addVertexWithUV(xMax, yMin, zMax, minU, maxV);
					t.addVertexWithUV(xMin, yMin, zMax, minU, minV);
					t.addVertexWithUV(xMin, yStart, zMax, uAt8, minV);
					t.addVertexWithUV(xMax, yStart, zMax, uAt8, maxV);

					t.addVertexWithUV(xMin, yStart, zMax, uAt8, minV);
					t.addVertexWithUV(xMin, yStart, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yMin, zMin, minU, maxV);
					t.addVertexWithUV(xMin, yMin, zMax, minU, minV);
					t.addVertexWithUV(xMin, yMin, zMax, uAt8, minV);
					t.addVertexWithUV(xMin, yMin, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yStart, zMin, minU, maxV);
					t.addVertexWithUV(xMin, yStart, zMax, minU, minV);

					t.addVertexWithUV(xMax, yStart, zMax, minU, maxV);
					t.addVertexWithUV(xMax, yStart, zMin, minU, minV);
					t.addVertexWithUV(xMax, yMin, zMin, uAt8, minV);
					t.addVertexWithUV(xMax, yMin, zMax, uAt8, maxV);
					t.addVertexWithUV(xMax, yMin, zMax, minU, maxV);
					t.addVertexWithUV(xMax, yMin, zMin, minU, minV);
					t.addVertexWithUV(xMax, yStart, zMin, uAt8, minV);
					t.addVertexWithUV(xMax, yStart, zMax, uAt8, maxV);
				} else if (up) {
					renderCube(t, minU, maxU, minV, maxV, xMin, xMax, zMin,
							zMax, yMin, yMax);
					t.addVertexWithUV(xMax, yMax, zMin, maxU, minV);
					t.addVertexWithUV(xMin, yMax, zMin, maxU, maxV);
					t.addVertexWithUV(xMin, yEnd, zMin, uAt8, maxV);
					t.addVertexWithUV(xMax, yEnd, zMin, uAt8, minV);
					t.addVertexWithUV(xMax, yEnd, zMin, maxU, minV);
					t.addVertexWithUV(xMin, yEnd, zMin, maxU, maxV);
					t.addVertexWithUV(xMin, yMax, zMin, uAt8, maxV);
					t.addVertexWithUV(xMax, yMax, zMin, uAt8, minV);

					t.addVertexWithUV(xMax, yMax, zMax, uAt8, maxV);
					t.addVertexWithUV(xMin, yMax, zMax, uAt8, minV);
					t.addVertexWithUV(xMin, yEnd, zMax, maxU, minV);
					t.addVertexWithUV(xMax, yEnd, zMax, maxU, maxV);
					t.addVertexWithUV(xMax, yEnd, zMax, uAt8, maxV);
					t.addVertexWithUV(xMin, yEnd, zMax, uAt8, minV);
					t.addVertexWithUV(xMin, yMax, zMax, maxU, minV);
					t.addVertexWithUV(xMax, yMax, zMax, maxU, maxV);

					t.addVertexWithUV(xMin, yMax, zMax, maxU, minV);
					t.addVertexWithUV(xMin, yMax, zMin, maxU, maxV);
					t.addVertexWithUV(xMin, yEnd, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yEnd, zMax, uAt8, minV);
					t.addVertexWithUV(xMin, yEnd, zMax, maxU, minV);
					t.addVertexWithUV(xMin, yEnd, zMin, maxU, maxV);
					t.addVertexWithUV(xMin, yMax, zMin, uAt8, maxV);
					t.addVertexWithUV(xMin, yMax, zMax, uAt8, minV);

					t.addVertexWithUV(xMax, yMax, zMax, uAt8, maxV);
					t.addVertexWithUV(xMax, yMax, zMin, uAt8, minV);
					t.addVertexWithUV(xMax, yEnd, zMin, maxU, minV);
					t.addVertexWithUV(xMax, yEnd, zMax, maxU, maxV);
					t.addVertexWithUV(xMax, yEnd, zMax, uAt8, maxV);
					t.addVertexWithUV(xMax, yEnd, zMin, uAt8, minV);
					t.addVertexWithUV(xMax, yMax, zMin, maxU, minV);
					t.addVertexWithUV(xMax, yMax, zMax, maxU, maxV);
				}
			} else {
				t.addVertexWithUV(xMax, yEnd, zMin, minU, minV);
				t.addVertexWithUV(xMin, yEnd, zMin, minU, maxV);
				t.addVertexWithUV(xMin, yStart, zMin, maxU, maxV);
				t.addVertexWithUV(xMax, yStart, zMin, maxU, minV);
				t.addVertexWithUV(xMax, yStart, zMin, minU, minV);
				t.addVertexWithUV(xMin, yStart, zMin, minU, maxV);
				t.addVertexWithUV(xMin, yEnd, zMin, maxU, maxV);
				t.addVertexWithUV(xMax, yEnd, zMin, maxU, minV);

				t.addVertexWithUV(xMax, yEnd, zMax, minU, minV);
				t.addVertexWithUV(xMin, yEnd, zMax, minU, maxV);
				t.addVertexWithUV(xMin, yStart, zMax, maxU, maxV);
				t.addVertexWithUV(xMax, yStart, zMax, maxU, minV);
				t.addVertexWithUV(xMax, yStart, zMax, minU, minV);
				t.addVertexWithUV(xMin, yStart, zMax, minU, maxV);
				t.addVertexWithUV(xMin, yEnd, zMax, maxU, maxV);
				t.addVertexWithUV(xMax, yEnd, zMax, maxU, minV);

				t.addVertexWithUV(xMin, yEnd, zMax, minU, minV);
				t.addVertexWithUV(xMin, yEnd, zMin, minU, maxV);
				t.addVertexWithUV(xMin, yStart, zMin, maxU, maxV);
				t.addVertexWithUV(xMin, yStart, zMax, maxU, minV);
				t.addVertexWithUV(xMin, yStart, zMax, minU, minV);
				t.addVertexWithUV(xMin, yStart, zMin, minU, maxV);
				t.addVertexWithUV(xMin, yEnd, zMin, maxU, maxV);
				t.addVertexWithUV(xMin, yEnd, zMax, maxU, minV);

				t.addVertexWithUV(xMax, yEnd, zMax, minU, minV);
				t.addVertexWithUV(xMax, yEnd, zMin, minU, maxV);
				t.addVertexWithUV(xMax, yStart, zMin, maxU, maxV);
				t.addVertexWithUV(xMax, yStart, zMax, maxU, minV);
				t.addVertexWithUV(xMax, yStart, zMax, minU, minV);
				t.addVertexWithUV(xMax, yStart, zMin, minU, maxV);
				t.addVertexWithUV(xMax, yEnd, zMin, maxU, maxV);
				t.addVertexWithUV(xMax, yEnd, zMax, maxU, minV);
			}
		}
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
