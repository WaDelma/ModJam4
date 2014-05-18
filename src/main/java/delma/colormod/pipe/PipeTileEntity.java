package delma.colormod.pipe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.TileFluidHandler;
import delma.colormod.LiquidHelper;
import delma.colormod.liquifier.LiquidTank;

public class PipeTileEntity extends TileFluidHandler {

	private int curTime;

	public PipeTileEntity() {
		tank = new LiquidTank(this, FluidContainerRegistry.BUCKET_VOLUME / 2);
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

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0,
				tagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager networkManager,
			S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());

	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	public IFluidTank getTank() {
		return tank;
	}
}
