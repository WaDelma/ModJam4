package delma.colormod.liquifier;

import delma.colormod.color.ColorLiquid;
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

public class LiquifierTileEntity extends TileFluidHandler {
	public ItemStack slot;
	private int currentTime;
	private final int time = 30;

	public LiquifierTileEntity() {
		tank.setCapacity(FluidContainerRegistry.BUCKET_VOLUME * 10);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound compound = tag.getCompoundTag("slot");
		if (compound != null) {
			slot = ItemStack.loadItemStackFromNBT(compound);
		}
		currentTime = tag.getInteger("currentTime");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (slot != null) {
			NBTTagCompound compound = new NBTTagCompound();
			slot.writeToNBT(compound);
			tag.setTag("slot", compound);
		}
		tag.setInteger("currentTime", currentTime);
	}
	
	public void updateEntity() {
		if (slot == null) {
			currentTime = time;
			return;
		}
		if (currentTime > 0) {
			currentTime--;
		}
		if (!worldObj.isRemote) {
			if (currentTime == 0) {
				if (tank.getFluidAmount() == tank.getCapacity()) {
					return;
				}
				currentTime = time;
				if (slot.stackSize > 0) {
					slot.stackSize--;
					int amount = FluidContainerRegistry.BUCKET_VOLUME;
					tank.fill(new FluidStack(new ColorLiquid(), amount), true);
					if (slot.stackSize == 0) {
						slot = null;
					}
					markDirty();
				}
			}
		}
	}

	public boolean canUpdate() {
		return true;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}
}
