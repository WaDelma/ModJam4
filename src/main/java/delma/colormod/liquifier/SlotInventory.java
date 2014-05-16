package delma.colormod.liquifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.IFluidHandler;

public class SlotInventory implements IInventory {
	private String name;
	private LiquifierTileEntity tile;
	
	public SlotInventory(String name, LiquifierTileEntity tile){
		this.tile = tile;
		this.name = name;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return tile.slot;
	}

	@Override
	public ItemStack decrStackSize(int index, int amount) {
		return new ItemStack(tile.slot.getItem(), tile.slot.stackSize - amount, tile.slot.getItemDamage());
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		tile.slot = stack;
	}

	@Override
	public String getInventoryName() {
		return name;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}
}
