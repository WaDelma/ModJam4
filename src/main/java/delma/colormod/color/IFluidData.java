package delma.colormod.color;

import net.minecraft.world.World;

public interface IFluidData {

	void caller(World world, int x, int y, int z, boolean draining);

	void receiver(World world, int x, int y, int z, boolean draining);
}
