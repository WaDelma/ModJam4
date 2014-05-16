package delma.colormod.pipe;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import delma.colormod.Colormod;

public class PipeBlock extends Block implements ITileEntityProvider {

	public PipeBlock() {
		super(Material.glass);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	// public void onNeighborBlockChange(World world, int x, int y, int z,
	// Block block) {
	// if (block instanceof IFluidHandler) {
	// for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
	// int xx = x + dir.offsetX;
	// int yy = y + dir.offsetY;
	// int zz = z + dir.offsetZ;
	// Block temp = world.getBlock(xx, yy, zz);
	// if (temp instanceof IFluidHandler) {
	// this.
	// }
	// }
	// }
	// }
	static final float THICKNESS = 0.0625F * 4;

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z,
			AxisAlignedBB aabb, List list, Entity entity) {
		boolean north = canConnectTo(world, x, y, z - 1, NORTH);
		boolean south = canConnectTo(world, x, y, z + 1, SOUTH);
		boolean west = canConnectTo(world, x - 1, y, z, WEST);
		boolean east = canConnectTo(world, x + 1, y, z, EAST);
		boolean down = canConnectTo(world, x, y - 1, z, DOWN);
		boolean up = canConnectTo(world, x, y + 1, z, UP);

		float min = 0.5F - THICKNESS;
		float max = 0.5F + THICKNESS;

		float start = 0.0F;
		float center = 0.5F;
		float end = 1.0F;

		boolean oneIs = west || east || north || south;

		if ((!west || !east) && oneIs) {
			if (west) {
				setBlockBounds(start, min, min, center, max, max);
				super.addCollisionBoxesToList(world, x, y, z, aabb, list,
						entity);
			} else if (east) {
				setBlockBounds(center, min, min, end, max, max);
				super.addCollisionBoxesToList(world, x, y, z, aabb, list,
						entity);
			}
		} else {
			setBlockBounds(start, min, min, end, max, max);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}

		if ((!north || !south) && oneIs) {
			if (north) {
				setBlockBounds(min, min, start, max, max, center);
				super.addCollisionBoxesToList(world, x, y, z, aabb, list,
						entity);
			} else if (south) {
				setBlockBounds(min, min, center, max, max, end);
				super.addCollisionBoxesToList(world, x, y, z, aabb, list,
						entity);
			}
		} else {
			setBlockBounds(min, min, start, max, max, end);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}

		if ((!down || !up) && oneIs) {
			if (down) {
				setBlockBounds(min, start, min, max, center, max);
				super.addCollisionBoxesToList(world, x, y, z, aabb, list,
						entity);
			} else if (up) {
				setBlockBounds(min, center, min, max, end, max);
				super.addCollisionBoxesToList(world, x, y, z, aabb, list,
						entity);
			}
		} else {
			setBlockBounds(min, start, min, max, end, max);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y,
			int z) {
		boolean north = canConnectTo(access, x, y, z - 1, NORTH);
		boolean south = canConnectTo(access, x, y, z + 1, SOUTH);
		boolean west = canConnectTo(access, x - 1, y, z, WEST);
		boolean east = canConnectTo(access, x + 1, y, z, EAST);
		boolean down = canConnectTo(access, x, y - 1, z, DOWN);
		boolean up = canConnectTo(access, x, y + 1, z, UP);

		float min = 0.5F - THICKNESS;
		float max = 0.5F + THICKNESS;

		float start = 0.0F;
		float center = 0.5F;
		float end = 1.0F;

		float minX = min;
		float maxX = max;
		float minZ = min;
		float maxZ = max;
		float minY = min;
		float maxY = max;

		boolean oneIs = west || east || north || south;

		if ((!west || !east) && oneIs) {
			if (west) {
				minX = start;
			} else if (east) {
				maxX = end;
			}
		} else {
			minX = start;
			maxX = end;
		}

		if ((!north || !south) && oneIs) {
			if (north) {
				minZ = start;
			} else if (south) {
				maxZ = end;
			}
		} else {
			minZ = start;
			maxZ = end;
		}

		if ((!down || !up) && oneIs) {
			if (down) {
				minY = start;
			} else if (up) {
				maxY = end;
			}
		} else {
			minY = start;
			maxY = end;
		}

		setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}

	public boolean canConnectTo(IBlockAccess world, int x, int y, int z,
			ForgeDirection dir) {
		return world.getTileEntity(x, y, z) instanceof IFluidHandler;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new PipeTileEntity();
	}

	// @SideOnly(Side.CLIENT)
	// public int getRenderBlockPass() {
	// return 0;
	// }

	@Override
	public int getRenderType() {
		return Colormod.PIPE_RENDERER_ID;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess access, int x, int y,
			int z, int side) {
		return access.getBlock(x, y, z) == this ? false : super
				.shouldSideBeRendered(access, x, y, z, side);
	}

	public IIcon func_150097_e() {
		return getIcon(0, 0);
	}
}
