package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Misc {
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
}
