package delma.colormod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import delma.colormod.color.ColorLiquid;
import delma.colormod.color.FluidDataRegistry;
import delma.colormod.liquifier.LiquifierBlock;
import delma.colormod.liquifier.LiquifierContainer;
import delma.colormod.liquifier.LiquifierGui;
import delma.colormod.liquifier.LiquifierTileEntity;
import delma.colormod.liquifier.SlotInventory;
import delma.colormod.pipe.PipeBlock;
import delma.colormod.pipe.PipeTileEntity;
import delma.colormod.pipe.PipeTileEntityRenderer;
import delma.colormod.vase.VaseBlock;
import delma.colormod.vase.VaseTileEntity;
import delma.colormod.vase.VaseTileEntityRenderer;

@Mod(modid = Colormod.MODID, version = Colormod.VERSION)
public class Colormod {
	public static final String MODID = "colormod";
	public static final String VERSION = "0.1";
	private LiquifierBlock liquifier;
	private VaseBlock vase;
	private PipeBlock pipe;
	private ColorLiquid colorLiquid;

	public static final String PIPE_TILE = "pipeTile";
	public static final String VASE_TILE = "vaseTile";

	public static final String COLOR_DATA_FILE = MODID + ".data";

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new FluidDataRegistry());

		liquifier = new LiquifierBlock();
		liquifier.setBlockTextureName(MODID + ":" + "liquifier");
		GameRegistry.registerBlock(liquifier, "liquifier");
		LanguageRegistry.addName(liquifier, "Liquifier");
		GameRegistry.registerTileEntity(LiquifierTileEntity.class,
				"liquifierTile");

		vase = new VaseBlock();
		vase.setBlockTextureName(MODID + ":" + "vase");
		GameRegistry.registerBlock(vase, "vase");
		LanguageRegistry.addName(vase, "Vase");
		GameRegistry.registerTileEntity(VaseTileEntity.class, VASE_TILE);

		pipe = new PipeBlock();
		pipe.setBlockTextureName(MODID + ":" + "pipe");
		GameRegistry.registerBlock(pipe, "pipe");
		LanguageRegistry.addName(pipe, "Pipe");
		GameRegistry.registerTileEntity(PipeTileEntity.class, PIPE_TILE);

		colorLiquid = new ColorLiquid();
		FluidRegistry.registerFluid(colorLiquid);
		FluidDataRegistry.registerFluid(colorLiquid, "colorLiquidTile");

		NetworkRegistry.INSTANCE.registerGuiHandler(MODID, new IGuiHandler() {

			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player,
					World world, int x, int y, int z) {
				return getContainer(player, getHandler(world, x, y, z));
			}

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player,
					World world, int x, int y, int z) {
				if (ID == 0) {
					return new LiquifierGui(getContainer(player,
							getHandler(world, x, y, z)));
				}
				return null;
			}

			private LiquifierTileEntity getHandler(World world, int x, int y,
					int z) {
				return (LiquifierTileEntity) world.getTileEntity(x, y, z);
			}

			private LiquifierContainer getContainer(EntityPlayer player,
					LiquifierTileEntity tile) {
				return new LiquifierContainer(player.inventory,
						new SlotInventory("liquifier", tile));
			}
		});
	}

	@EventHandler
	@SideOnly(Side.CLIENT)
	public void initClient(FMLInitializationEvent event) {
		ClientRegistry.bindTileEntitySpecialRenderer(PipeTileEntity.class,
				new PipeTileEntityRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(VaseTileEntity.class,
				new VaseTileEntityRenderer());
	}
}
