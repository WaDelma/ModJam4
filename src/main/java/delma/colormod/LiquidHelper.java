package delma.colormod;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public enum LiquidHelper {
	INSTANCE;
	public void drain(World world, int x, int y, int z, IFluidTank tank) {
		FluidStack fluidStack = tank.getFluid();
		Fluid fluid = null;
		if (fluidStack != null) {
			fluid = fluidStack.getFluid();
		}
		Map<ForgeDirection, IFluidHandler> drainable = new HashMap<ForgeDirection, IFluidHandler>();
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int xx = x + dir.offsetX;
			int yy = y + dir.offsetY;
			int zz = z + dir.offsetZ;
			TileEntity tile = world.getTileEntity(xx, yy, zz);
			if (tile instanceof IFluidHandler) {
				IFluidHandler fluidHandler = (IFluidHandler) tile;
				ForgeDirection fromDir = dir.getOpposite();
				if (fluid != null || fluidHandler.canDrain(fromDir, fluid)) {
					drainable.put(fromDir, fluidHandler);
				}
			}
		}
		int spaceLeft = tank.getCapacity() - tank.getFluidAmount();
		int drainAmount = (int) (spaceLeft / (drainable.size() + 1));
		for (Entry<ForgeDirection, IFluidHandler> entry : drainable.entrySet()) {
			tank.fill(
					entry.getValue().drain(entry.getKey(), drainAmount, true),
					true);
		}
	}
}
