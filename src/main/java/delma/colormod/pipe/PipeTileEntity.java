package delma.colormod.pipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import delma.colormod.LiquidHelper;
import delma.colormod.color.ColorLiquid;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.TileFluidHandler;

public class PipeTileEntity extends TileFluidHandler {

	public PipeTileEntity() {
		tank.setCapacity(FluidContainerRegistry.BUCKET_VOLUME * 2);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound compound = tag.getCompoundTag("slot");
		if (compound != null) {
			tank.setFluid(FluidStack.loadFluidStackFromNBT(compound));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (tank.getFluid() != null) {
			NBTTagCompound compound = new NBTTagCompound();
			tank.getFluid().writeToNBT(compound);
			tag.setTag("slot", compound);
		}
	}

	public void updateEntity() {
		LiquidHelper.INSTANCE.drain(worldObj, xCoord, yCoord, zCoord, tank);
	}

	public boolean canUpdate() {
		return true;
	}
}
