package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Item vending machine implementation.
 * <p>
 * When an item is taken, the content is automatically refilled. But notice that
 * simple repository means everyone SHARE a SAME inventory. Conflict operation might
 * occur if it's not dealt with care.
 * <p>
 * If you like to check about stuff, remember to override {@link AbstractRepository#takeItemAt(UUID, int, int, ItemStack)}.
 * And, if you would like to attach special tags, the {@link ItemStack} provided in the parameter is the taken item that
 * could be modified.
 * <p>
 * And you have to load content on your own. Because you may want to use something other than
 * {@link tech.dsstudio.minecraft.abstractRepository.buttons.Button}
 */
public abstract class SimpleSinglePageItemVendorRepository extends SimpleSinglePageRepository {
	@Override
	public ItemOperationResult takeItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		return ItemOperationResult.REVERT_CONTENT;
	}

	@Override
	public ItemOperationResult putItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		return ItemOperationResult.CANCEL_EVENT;
	}

	@Override
	public boolean canPutItemAt(UUID uuid, int slot, int rawSlot) {
		return false;
	}

	@Override
	public boolean canTakeItemAt(UUID uuid, int slot, int rawSlot) {
		return true;
	}
}
