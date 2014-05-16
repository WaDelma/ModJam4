package delma.colormod.liquifier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

public class LiquifierContainer extends Container {

	private final IInventory inventory;
	private InventoryPlayer inventoryPlayer;

	public LiquifierContainer(InventoryPlayer inventoryPlayer,
			IInventory inventory) {
		this.inventory = inventory;
		this.inventoryPlayer = inventoryPlayer;
		inventory.openInventory();
		byte b0 = 51;

		this.addSlotToContainer(new Slot(inventory, 0, 44 + 2 * 18, 20));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, i * 18 + b0));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 58 + b0));
		}
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack result = null;
		Slot slot = (Slot) inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack = slot.getStack();
			result = itemstack.copy();

			if (index < inventory.getSizeInventory()) {
				if (!mergeItemStack(itemstack, inventory.getSizeInventory(),
						inventorySlots.size(), true)) {
					return null;
				}
			} else if (!mergeItemStack(itemstack, 0,
					inventory.getSizeInventory(), false)) {
				return null;
			}

			if (itemstack.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}
		}

		return result;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		inventory.closeInventory();
	}

	public IInventory getPlayerInventory() {
		return inventoryPlayer;
	}
}
