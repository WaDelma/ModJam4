package delma.colormod.vase;

import delma.colormod.LiquidHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.TileFluidHandler;

public class VaseTileEntity extends TileFluidHandler {
	public VaseTileEntity(){
		tank.setCapacity(FluidContainerRegistry.BUCKET_VOLUME * 10);
	}
	
	public void updateEntity() {
		LiquidHelper.INSTANCE.drain(worldObj, xCoord, yCoord, zCoord, tank);
	}
}
