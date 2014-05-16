package delma.colormod.pipe;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import delma.colormod.Colormod;

public class PipeRenderer implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {
	}

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

	private static final float THICKNESS = PipeBlock.THICKNESS;

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block temp, int modelId, RenderBlocks renderer) {
		PipeBlock block = (PipeBlock) temp;
		int height = world.getHeight();
		Tessellator t = Tessellator.instance;
		t.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		int colorMultip = block.colorMultiplier(world, x, y, z);
		float red = (colorMultip >> 16 & 255) / 255.0F;
		float green = (colorMultip >> 8 & 255) / 255.0F;
		float blue = (colorMultip & 255) / 255.0F;

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

		// if (this.hasOverrideBlockTexture())
		// {
		// iicon = this.overrideBlockTexture;
		// iicon1 = this.overrideBlockTexture;
		// }
		// else
		// {
		int meta = world.getBlockMetadata(x, y, z);
		icon = getBlockIconFromSideAndMetadata(block, 0, meta);
		icon1 = block.func_150097_e();
		// }

		double minU = icon.getMinU();
		double uAt8 = icon.getInterpolatedU(8.0D);
		double maxU = icon.getMaxU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		double uAt7 = icon1.getInterpolatedU(7.0D);
		double uAt9 = icon1.getInterpolatedU(9.0D);
		double minV2 = icon1.getMinV();
		double vAt8 = icon1.getInterpolatedV(8.0D);
		double maxV2 = icon1.getMaxV();

		double xStart = x;
		double xMiddle = x + 0.5D;
		double xEnd = x + 1;

		double zStart = z;
		double zMiddle = z + 0.5D;
		double zEnd = z + 1;

		double xMin = x + 0.5D - THICKNESS;
		double xMax = x + 0.5D + THICKNESS;
		double zMin = z + 0.5D - THICKNESS;
		double zMax = z + 0.5D + THICKNESS;
		double yMin = y + 0.5D - THICKNESS;
		double yMax = y + 0.5D + THICKNESS;

		boolean north = block.canConnectTo(world, x, y, z - 1, NORTH);
		boolean south = block.canConnectTo(world, x, y, z + 1, SOUTH);
		boolean west = block.canConnectTo(world, x - 1, y, z, WEST);
		boolean east = block.canConnectTo(world, x + 1, y, z, EAST);
		boolean renderTop = block.shouldSideBeRendered(world, x, y + 1, z, 1);
		boolean renderBot = block.shouldSideBeRendered(world, x, y - 1, z, 0);
		double weAdd = 0.0001D;// 0.01D;
		double nsAdd = 0.000025D;

		boolean oneIs = west || east || north || south;
		if ((!west || !east) && oneIs) {
			if (west) {
				t.addVertexWithUV(xStart, yMax, zMin, minU, minV);
				t.addVertexWithUV(xStart, yMin, zMin, minU, maxV);
				t.addVertexWithUV(xMiddle, yMin, zMin, uAt8, maxV);
				t.addVertexWithUV(xMiddle, yMax, zMin, uAt8, minV);
				t.addVertexWithUV(xMiddle, yMax, zMin, minU, minV);
				t.addVertexWithUV(xMiddle, yMin, zMin, minU, maxV);
				t.addVertexWithUV(xStart, yMin, zMin, uAt8, maxV);
				t.addVertexWithUV(xStart, yMax, zMin, uAt8, minV);

				t.addVertexWithUV(xStart, yMax, zMax, minU, minV);
				t.addVertexWithUV(xStart, yMin, zMax, minU, maxV);
				t.addVertexWithUV(xMiddle, yMin, zMax, uAt8, maxV);
				t.addVertexWithUV(xMiddle, yMax, zMax, uAt8, minV);
				t.addVertexWithUV(xMiddle, yMax, zMax, minU, minV);
				t.addVertexWithUV(xMiddle, yMin, zMax, minU, maxV);
				t.addVertexWithUV(xStart, yMin, zMax, uAt8, maxV);
				t.addVertexWithUV(xStart, yMax, zMax, uAt8, minV);

				if (!south && !north) {
					t.addVertexWithUV(xMiddle, yMax, zMax, uAt7, minV2);
					t.addVertexWithUV(xMiddle, yMin, zMax, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMin, zMin, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMax, zMin, uAt9, minV2);
					t.addVertexWithUV(xMiddle, yMax, zMin, uAt7, minV2);
					t.addVertexWithUV(xMiddle, yMin, zMin, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMin, zMax, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMax, zMax, uAt9, minV2);
				}

				if (renderTop || y < height - 1
						&& world.isAirBlock(x - 1, y + 1, z)) {
					t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, vAt8);
				}

				if (renderBot || y > 1 && world.isAirBlock(x - 1, y - 1, z)) {
					t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, vAt8);
				}
			} else if (east) {
				t.addVertexWithUV(xMiddle, yMax, zMin, uAt8, minV);
				t.addVertexWithUV(xMiddle, yMin, zMin, uAt8, maxV);
				t.addVertexWithUV(xEnd, yMin, zMin, maxU, maxV);
				t.addVertexWithUV(xEnd, yMax, zMin, maxU, minV);
				t.addVertexWithUV(xEnd, yMax, zMin, uAt8, minV);
				t.addVertexWithUV(xEnd, yMin, zMin, uAt8, maxV);
				t.addVertexWithUV(xMiddle, yMin, zMin, maxU, maxV);
				t.addVertexWithUV(xMiddle, yMax, zMin, maxU, minV);

				t.addVertexWithUV(xMiddle, yMax, zMax, uAt8, minV);
				t.addVertexWithUV(xMiddle, yMin, zMax, uAt8, maxV);
				t.addVertexWithUV(xEnd, yMin, zMax, maxU, maxV);
				t.addVertexWithUV(xEnd, yMax, zMax, maxU, minV);
				t.addVertexWithUV(xEnd, yMax, zMax, uAt8, minV);
				t.addVertexWithUV(xEnd, yMin, zMax, uAt8, maxV);
				t.addVertexWithUV(xMiddle, yMin, zMax, maxU, maxV);
				t.addVertexWithUV(xMiddle, yMax, zMax, maxU, minV);

				if (!south && !north) {
					t.addVertexWithUV(xMiddle, yMax, zMin, uAt7, minV2);
					t.addVertexWithUV(xMiddle, yMin, zMin, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMin, zMax, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMax, zMax, uAt9, minV2);
					t.addVertexWithUV(xMiddle, yMax, zMax, uAt7, minV2);
					t.addVertexWithUV(xMiddle, yMin, zMax, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMin, zMin, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMax, zMin, uAt9, minV2);
				}

				if (renderTop || y < height - 1
						&& world.isAirBlock(x + 1, y + 1, z)) {
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, minV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, minV2);
				}

				if (renderBot || y > 1 && world.isAirBlock(x + 1, y - 1, z)) {
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, minV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, minV2);
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
				t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9, maxV2);
				t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, minV2);
				t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, minV2);
				t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7, maxV2);
				t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, maxV2);
				t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9, minV2);
				t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7, minV2);
				t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, maxV2);
			} else {
				if (y < height - 1 && world.isAirBlock(x - 1, y + 1, z)) {
					t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xStart, yMax + weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xStart, yMax + weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, vAt8);
				}

				if (y < height - 1 && world.isAirBlock(x + 1, y + 1, z)) {
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, minV2);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMax + weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xEnd, yMax + weAdd, zMin, uAt7, minV2);
				}
			}

			if (renderBot) {
				t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9, maxV2);
				t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, minV2);
				t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, minV2);
				t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7, maxV2);
				t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, maxV2);
				t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9, minV2);
				t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7, minV2);
				t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, maxV2);
			} else {
				if (y > 1 && world.isAirBlock(x - 1, y - 1, z)) {
					t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xStart, yMin - weAdd, zMax, uAt9, maxV2);
					t.addVertexWithUV(xStart, yMin - weAdd, zMin, uAt7, maxV2);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, vAt8);
				}

				if (y > 1 && world.isAirBlock(x + 1, y - 1, z)) {
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, minV2);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMax, uAt9, minV2);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMax, uAt9, vAt8);
					t.addVertexWithUV(xMiddle, yMin - weAdd, zMin, uAt7, vAt8);
					t.addVertexWithUV(xEnd, yMin - weAdd, zMin, uAt7, minV2);
				}
			}
		}

		if ((!north || !south) && oneIs) {
			if (north) {
				t.addVertexWithUV(xMin, yMax, zStart, minU, minV);
				t.addVertexWithUV(xMin, yMin, zStart, minU, maxV);
				t.addVertexWithUV(xMin, yMin, zMiddle, uAt8, maxV);
				t.addVertexWithUV(xMin, yMax, zMiddle, uAt8, minV);
				t.addVertexWithUV(xMin, yMax, zMiddle, minU, minV);
				t.addVertexWithUV(xMin, yMin, zMiddle, minU, maxV);
				t.addVertexWithUV(xMin, yMin, zStart, uAt8, maxV);
				t.addVertexWithUV(xMin, yMax, zStart, uAt8, minV);

				t.addVertexWithUV(xMax, yMax, zStart, minU, minV);
				t.addVertexWithUV(xMax, yMin, zStart, minU, maxV);
				t.addVertexWithUV(xMax, yMin, zMiddle, uAt8, maxV);
				t.addVertexWithUV(xMax, yMax, zMiddle, uAt8, minV);
				t.addVertexWithUV(xMax, yMax, zMiddle, minU, minV);
				t.addVertexWithUV(xMax, yMin, zMiddle, minU, maxV);
				t.addVertexWithUV(xMax, yMin, zStart, uAt8, maxV);
				t.addVertexWithUV(xMax, yMax, zStart, uAt8, minV);

				if (!east && !west) {
					t.addVertexWithUV(xMin, yMax, zMiddle, uAt7, minV2);
					t.addVertexWithUV(xMin, yMin, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMin, zMiddle, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMax, zMiddle, uAt9, minV2);
					t.addVertexWithUV(xMax, yMax, zMiddle, uAt7, minV2);
					t.addVertexWithUV(xMax, yMin, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMin, yMin, zMiddle, uAt9, maxV2);
					t.addVertexWithUV(xMin, yMax, zMiddle, uAt9, minV2);
				}

				if (renderTop || y < height - 1
						&& world.isAirBlock(x, y + 1, z - 1)) {
					t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt9, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt7, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt9, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt7, minV2);
				}

				if (renderBot || y > 1 && world.isAirBlock(x, y - 1, z - 1)) {
					t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt9, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt7, minV2);
				}
			} else if (south) {
				t.addVertexWithUV(xMin, yMax, zMiddle, uAt8, minV);
				t.addVertexWithUV(xMin, yMin, zMiddle, uAt8, maxV);
				t.addVertexWithUV(xMin, yMin, zEnd, maxU, maxV);
				t.addVertexWithUV(xMin, yMax, zEnd, maxU, minV);
				t.addVertexWithUV(xMin, yMax, zEnd, uAt8, minV);
				t.addVertexWithUV(xMin, yMin, zEnd, uAt8, maxV);
				t.addVertexWithUV(xMin, yMin, zMiddle, maxU, maxV);
				t.addVertexWithUV(xMin, yMax, zMiddle, maxU, minV);

				t.addVertexWithUV(xMax, yMax, zMiddle, uAt8, minV);
				t.addVertexWithUV(xMax, yMin, zMiddle, uAt8, maxV);
				t.addVertexWithUV(xMax, yMin, zEnd, maxU, maxV);
				t.addVertexWithUV(xMax, yMax, zEnd, maxU, minV);
				t.addVertexWithUV(xMax, yMax, zEnd, uAt8, minV);
				t.addVertexWithUV(xMax, yMin, zEnd, uAt8, maxV);
				t.addVertexWithUV(xMax, yMin, zMiddle, maxU, maxV);
				t.addVertexWithUV(xMax, yMax, zMiddle, maxU, minV);

				if (!east && !west) {
					t.addVertexWithUV(xMax, yMax, zMiddle, uAt7, minV2);
					t.addVertexWithUV(xMax, yMin, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMin, yMin, zMiddle, uAt9, maxV2);
					t.addVertexWithUV(xMin, yMax, zMiddle, uAt9, minV2);
					t.addVertexWithUV(xMin, yMax, zMiddle, uAt7, minV2);
					t.addVertexWithUV(xMin, yMin, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMin, zMiddle, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMax, zMiddle, uAt9, minV2);
				}

				if (renderTop || y < height - 1
						&& world.isAirBlock(x, y + 1, z + 1)) {
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, vAt8);
				}

				if (renderBot || y > 1 && world.isAirBlock(x, y - 1, z + 1)) {
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt9, maxV2);
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
				t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, maxV2);
				t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt9, minV2);
				t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt7, minV2);
				t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, maxV2);
				t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt9, maxV2);
				t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, minV2);
				t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, minV2);
				t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt7, maxV2);
			} else {
				if (y < height - 1 && world.isAirBlock(x, y + 1, z - 1)) {
					t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt9, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt7, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt9, minV2);
					t.addVertexWithUV(xMin, yMax + nsAdd, zStart, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zStart, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt7, minV2);
				}

				if (y < height - 1 && world.isAirBlock(x, y + 1, z + 1)) {
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMin, yMax + nsAdd, zEnd, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMax + nsAdd, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zMiddle, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMax + nsAdd, zEnd, uAt9, vAt8);
				}
			}

			if (renderBot) {
				t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, maxV2);
				t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt9, minV2);
				t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt7, minV2);
				t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, maxV2);
				t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt9, maxV2);
				t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, minV2);
				t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, minV2);
				t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt7, maxV2);
			} else {
				if (y > 1 && world.isAirBlock(x, y - 1, z - 1)) {
					t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt9, minV2);
					t.addVertexWithUV(xMin, yMin - nsAdd, zStart, uAt9, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zStart, uAt7, vAt8);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt7, minV2);
				}

				if (y > 1 && world.isAirBlock(x, y - 1, z + 1)) {
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt9, vAt8);
					t.addVertexWithUV(xMin, yMin - nsAdd, zEnd, uAt7, vAt8);
					t.addVertexWithUV(xMin, yMin - nsAdd, zMiddle, uAt7, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zMiddle, uAt9, maxV2);
					t.addVertexWithUV(xMax, yMin - nsAdd, zEnd, uAt9, vAt8);
				}
			}
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return Colormod.PIPE_RENDERER_ID;
	}

}
