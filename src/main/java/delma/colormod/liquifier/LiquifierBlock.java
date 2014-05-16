package delma.colormod.liquifier;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import delma.colormod.Colormod;
import delma.colormod.vase.VaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class LiquifierBlock extends Block implements ITileEntityProvider{

	public LiquifierBlock() {
		super(Material.iron);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int p_149727_6_, float p_149727_7_,
			float p_149727_8_, float p_149727_9_) {
		FMLNetworkHandler.openGui(player, Colormod.MODID, 0, world, x, y, z);
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new LiquifierTileEntity();
	}

}
