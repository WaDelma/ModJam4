package delma.colormod.color;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidTank;

public class ColorLiquid extends Fluid implements IFluidDataProvider {

	public ColorLiquid() {
		super("color");
	}

	@Override
	public IFluidData createData(IFluidTank tank) {
		return new ColorLiquidData();
	}

	public static class ColorLiquidData implements IFluidData {
		public List<Pixel> pixels;

		@Override
		public void caller(World world, int x, int y, int z, boolean draining) {
			System.out.print("(" + x + ", " + y + ", " + z + "): ");
			System.out.println("I do " + (draining ? "drainging" : "filling"));
		}

		@Override
		public void receiver(World world, int x, int y, int z, boolean draining) {
			if (!draining) {

			}
			System.out.print("(" + x + ", " + y + ", " + z + "): ");
			System.out.println("Somebody does "
					+ (draining ? "drainging" : "filling") + " to me");
		}

	}
}
