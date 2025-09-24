package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

public class Misc {
	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();


	public static String countStacks (int items) {
		double stacks = items / 64;

		NumberFormat formatter = new DecimalFormat("#0.00");

		return formatter.format(stacks);
	}

	public static Location getInventoryLocation (Inventory inventory) {
		InventoryHolder holder = inventory.getHolder();

		if (!(holder instanceof BlockState)) return null;

		Block chestBlock = ((BlockState) holder).getBlock();

		if (chestBlock == null) return null;

		Location invLocation = chestBlock.getLocation();

		return invLocation;
	}

	public static boolean isValidShopBlock (Block block) {
		boolean isSlab = api.isSlab(block);

		boolean stairsEnabled = Slabbo.getInstance().getConfig().getBoolean("stairs", true);

		boolean barriersEnabled = Slabbo.getInstance().getConfig().getBoolean("barriershops", true);

		boolean isUpsideDownStair = api.isUpsideDownStair(block);

		boolean isBarrier = block.getType() == Material.BARRIER;

		boolean validBlock = isSlab || (stairsEnabled && isUpsideDownStair) || (barriersEnabled && isBarrier);

		return validBlock;
	}

	/**
	 * Taken from https://stackoverflow.com/a/36854196
	 * returns a view (not a new list) of the sourceList for the
	 * range based on page and pageSize
	 * @param sourceList
	 * @param page, page number should start from 1
	 * @param pageSize
	 * @return
	 */
	public static <T> List<T> getPage (List<T> sourceList, int page, int pageSize) {
		if(pageSize <= 0 || page <= 0) {
			throw new IllegalArgumentException("invalid page size: " + pageSize);
		}

		int fromIndex = (page - 1) * pageSize;
		if (sourceList == null || sourceList.size() < fromIndex) {
			return Collections.emptyList();
		}

		// toIndex exclusive
		return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
	}

	public static <T> T getValueOrDefault(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	/**
	 * Returns true if the inventory is a hopper (block, HopperInventory, or minecart hopper).
	 */
	public static boolean isHopper(Inventory inventory) {
		if (inventory == null) return false;
		// Block hopper
		InventoryHolder holder = inventory.getHolder();
		if (holder instanceof BlockState) {
			Block block = ((BlockState) holder).getBlock();
			if (block != null && block.getType() == Material.HOPPER) return true;
		}
		// HopperInventory (used by block hoppers)
		if (inventory.getType() == org.bukkit.event.inventory.InventoryType.HOPPER) return true;
		// Minecart hoppers (if you want to support them)
		if (holder != null && holder.getClass().getSimpleName().contains("MinecartHopper")) return true;
		return false;
	}

	/**
	 * Returns true if the inventory is a chest (single, double, or trapped chest).
	 */
	public static boolean isChest(Inventory inventory) {
		if (inventory == null) return false;
		InventoryHolder holder = inventory.getHolder();
		if (holder instanceof BlockState) {
			Block block = ((BlockState) holder).getBlock();
			if (block == null) return false;
			Material type = block.getType();
			if (type == Material.CHEST || type == Material.TRAPPED_CHEST) return true;
		}
		// DoubleChest inventory
		if (holder != null && holder.getClass().getSimpleName().equals("DoubleChest")) return true;
		// DoubleChestInventory type
		if (inventory.getType() == org.bukkit.event.inventory.InventoryType.CHEST) return true;
		return false;
	}
}
