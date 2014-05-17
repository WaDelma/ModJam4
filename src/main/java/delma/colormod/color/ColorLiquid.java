package delma.colormod.color;

import java.util.Set;

import net.minecraftforge.fluids.Fluid;

public class ColorLiquid extends Fluid {

	private Set<Pixel> pixels;

	public ColorLiquid() {
		super("color");
	}
}
