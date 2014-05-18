package delma.colormod.color;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidEvent.FluidDrainingEvent;
import net.minecraftforge.fluids.FluidEvent.FluidFillingEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import delma.colormod.Colormod;

//TODO: How to make this usable
public class FluidDataRegistry {
	public static class IdentifierData {
		public HashMap<TankIdentifier, IFluidData> data = new HashMap<TankIdentifier, IFluidData>();
	}

	private static Map<String, IdentifierData> infos = new HashMap<String, IdentifierData>();
	private static Gson json = new GsonBuilder()
			.enableComplexMapKeySerialization().create();
	private static Map<String, IFluidDataProvider> providers = new HashMap<String, IFluidDataProvider>();

	public static void registerFluid(IFluidDataProvider fluidDataProvider,
			String name) {
		providers.put(name, fluidDataProvider);
	}

	@SubscribeEvent
	public void receive(FluidEvent event) {
		World world = event.world;
		int x = event.x;
		int y = event.y;
		int z = event.z;

		boolean draining = event instanceof FluidDrainingEvent;
		boolean filling = event instanceof FluidFillingEvent;
		if (draining || filling) {
			FluidStack eventFluidStack = event.fluid;
			if (eventFluidStack != null) {
				IFluidTank tank;
				if (draining) {
					tank = ((FluidDrainingEvent) event).tank;
				} else {
					tank = ((FluidFillingEvent) event).tank;
				}
				int index = findIndexForTank(tank, world.getTileEntity(x, y, z));
				Fluid eventFluid = eventFluidStack.getFluid();
				iterateReceivers(world, x, y, z, tank, index, draining,
						eventFluid);
			}
		}
	}

	private void iterateReceivers(World world, int x, int y, int z,
			IFluidTank tank, int index, boolean draining, Fluid eventFluid) {
		for (Entry<String, IFluidDataProvider> entry : providers
				.entrySet()) {
			if (isObjectSubOf(eventFluid, entry.getValue())) {
				String dataName = entry.getKey();
				TankIdentifier id = new TankIdentifier(x, y, z, index,
						world.provider.dimensionId);
				IFluidData data = getData(getID(dataName).data, id,
						eventFluid, tank);
				data.receiver(world, x, y, z, draining);
				doDataUpdate(world, x, y, z, dataName, draining);
			}
		}
	}

	private int findIndexForTank(IFluidTank tank, TileEntity tile) {
		List<Field> fields = getFields(tile.getClass());
		int index = 0;
		for (Field field : fields) {
			Object object = get(field, tile);
			if (object instanceof IFluidTank) {
				if (object == tank) {
					break;
				}
				index++;
			}
		}
		return index;
	}

	private void doDataUpdate(World world, int x, int y, int z,
			String dataName, boolean draining) {
		removeInvalid(world, dataName);
		List<TileEntity> tileEntities = world.loadedTileEntityList;
		for (TileEntity tile : tileEntities) {
			int xx = tile.xCoord;
			int yy = tile.yCoord;
			int zz = tile.zCoord;
			if (xx == x && yy == y && zz == z) {
				continue;
			}
			if (tile instanceof IFluidHandler) {
				findCallerFromTile(tile, world, xx, yy, zz, dataName, draining);
			}
		}
	}

	private void findCallerFromTile(TileEntity tile, World world, int xx,
			int yy, int zz, String dataName, boolean draining) {
		List<Field> fields = getFields(tile.getClass());
		int index = 0;
		for (Field field : fields) {
			Object object = get(field, tile);
			if (object instanceof IFluidTank) {
				IFluidTank tank = (IFluidTank) object;
				FluidStack fluidStack = tank.getFluid();
				if (fluidStack != null) {
					Fluid fluid = fluidStack.getFluid();
					if (isObjectSubOf(fluid, providers.get(dataName))) {
						TankIdentifier id = new TankIdentifier(xx, yy, zz,
								index, world.provider.dimensionId);
						IFluidData data = getData(getID(dataName).data, id,
								fluid, tank);
						data.caller(world, xx, yy, zz, draining);
						index++;
					}
				}
			}
		}
	}

	private boolean isObjectSubOf(Object sub, Object sup) {
		return sup.getClass().isAssignableFrom(sub.getClass());
	}

	private IFluidData getData(Map<TankIdentifier, IFluidData> map,
			TankIdentifier id, Fluid fluid, IFluidTank tank) {
		IFluidData data = map.get(id);
		if (data == null) {
			data = ((IFluidDataProvider) fluid).createData(tank);
			map.put(id, data);
		}
		return data;
	}

	private IdentifierData getID(String string) {
		IdentifierData result = infos.get(string);
		if (result == null) {
			result = new IdentifierData();
			infos.put(string, result);
		}
		return result;
	}

	private void removeInvalid(World world, String string) {
		for (Iterator<TankIdentifier> it = getID(string).data.keySet()
				.iterator(); it.hasNext();) {
			TankIdentifier id = it.next();
			if (id.dimID == world.provider.dimensionId) {
				if (!world.blockExists(id.x, id.y, id.z)) {
					continue;
				}
				if (!(world.getTileEntity(id.x, id.y, id.z) instanceof IFluidHandler)) {
					it.remove();
				}
			}
		}
	}

	private Charset charset = StandardCharsets.UTF_8;

	@SubscribeEvent
	public void receiveLoadEvent(Load event) {
		File saveDir = event.world.getSaveHandler().getWorldDirectory();
		File saveFile = new File(saveDir, Colormod.COLOR_DATA_FILE);
		try {
			Files.touch(saveFile);
			byte[] bytes = Files.toByteArray(saveFile);
			infos = json.fromJson(new String(bytes, charset), HashMap.class);
			if (infos == null) {
				infos = new HashMap<String, IdentifierData>();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void receiveSaveEvent(Save event) {
		File saveDir = event.world.getSaveHandler().getWorldDirectory();
		File saveFile = new File(saveDir, Colormod.COLOR_DATA_FILE);
		byte[] bytes = json.toJson(infos, HashMap.class).getBytes(charset);
		try {
			Files.touch(saveFile);
			Files.write(bytes, saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Object get(Field field, Object object) {
		field.setAccessible(true);
		try {
			return field.get(object);
		} catch (Exception e) {
			return null;
		}
	}

	private List<Field> getFields(Class<?> clazz) {
		List<Field> result = new ArrayList<Field>();
		do {
			for (Field field : clazz.getDeclaredFields()) {
				result.add(field);
			}
		} while ((clazz = clazz.getSuperclass()) != null);
		return result;
	}
}
