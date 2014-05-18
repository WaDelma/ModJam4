package delma.colormod.liquifier;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidTank;

public class LiquidTank extends FluidTank {
	public LiquidTank(TileEntity tile, int capacity) {
		super(null, capacity);
		this.tile = tile;
	}

}
