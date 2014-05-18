package delma.colormod.color;

import net.minecraftforge.fluids.IFluidTank;

public interface IFluidDataProvider {
	IFluidData createData(IFluidTank tank);
}
