package delma.colormod.vase;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.TileFluidHandler;
import delma.colormod.LiquidHelper;

public class VaseTileEntity extends TileFluidHandler {
	private int curTime;

	public VaseTileEntity() {
		tank.setCapacity(FluidContainerRegistry.BUCKET_VOLUME * 10);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tag.setInteger("curTime", curTime);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		curTime = tag.getInteger("curTime");
	}

	@Override
	public void updateEntity() {
		if (!LiquidHelper.INSTANCE.drain(worldObj, xCoord, yCoord, zCoord,
				tank, false)) {
			return;
		}
		FluidStack fluidStack = tank.getFluid();
		float time = 10;
		if (fluidStack != null) {
			time = Math.abs(fluidStack.getFluid().getDensity()) / 100f;
		}
		if (curTime < time) {
			curTime++;
		} else if (LiquidHelper.INSTANCE.drain(worldObj, xCoord, yCoord,
				zCoord, tank, true)) {
			if (!worldObj.isRemote) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				markDirty();
			}
			curTime = 0;
		}
	}

	public IFluidTank getTank() {
		return tank;
	}
}
