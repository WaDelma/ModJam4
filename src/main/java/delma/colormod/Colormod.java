package delma.colormod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import delma.colormod.color.ColorLiquid;
import delma.colormod.liquifier.LiquifierBlock;
import delma.colormod.liquifier.LiquifierContainer;
import delma.colormod.liquifier.LiquifierGui;
import delma.colormod.liquifier.LiquifierTileEntity;
import delma.colormod.liquifier.SlotInventory;
import delma.colormod.pipe.PipeBlock;
import delma.colormod.pipe.PipeRenderer;
import delma.colormod.pipe.PipeTileEntity;
import delma.colormod.vase.VaseBlock;
import delma.colormod.vase.VaseTileEntity;

@Mod(modid = Colormod.MODID, version = Colormod.VERSION)
public class Colormod {
	public static final String MODID = "colormod";
	public static final String VERSION = "0.1";
	public static final int PIPE_RENDERER_ID = RenderingRegistry
			.getNextAvailableRenderId();
	private LiquifierBlock liquifier;
	private VaseBlock vase;
	private PipeBlock pipe;

	@EventHandler
	public void init(FMLInitializationEvent event) {
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
		GameRegistry.registerTileEntity(VaseTileEntity.class, "vaseTile");

		pipe = new PipeBlock();
		// pipe.setBlockTextureName(MODID + ":" + "pipe");
		GameRegistry.registerBlock(pipe, "pipe");
		LanguageRegistry.addName(pipe, "Pipe");
		GameRegistry.registerTileEntity(PipeTileEntity.class, "pipeTile");
		RenderingRegistry.registerBlockHandler(new PipeRenderer());
		
		FluidRegistry.registerFluid(new ColorLiquid());

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
}
