package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
		boolean isUpsideDownStair = api.isUpsideDownStair(block);

		boolean validBlock = isSlab || (stairsEnabled && isUpsideDownStair);

		return validBlock;
	}
}
