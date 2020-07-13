package xyz.mackan.Slabbo.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import xyz.mackan.Slabbo.types.Shop;

import java.util.HashMap;
import java.util.UUID;

public class ChestLinkUtil {
	public HashMap<UUID, String> pendingLinks = new HashMap<UUID, String>();

	// Chest Location, Shop
	public HashMap<String, Shop> links = new HashMap<String, Shop>();

	public ChestLinkUtil () { }

	public boolean hasPendingLink (Player p) {
		UUID id = p.getUniqueId();

		return pendingLinks.containsKey(id);
	}

	public boolean isChestLinked (Block block) {
		BlockState blockState = block.getState();

		if (!(blockState instanceof Chest)) return false;

		Chest chest = (Chest) blockState;

		InventoryHolder chestHolder = chest.getInventory().getHolder();

		if (!(chestHolder instanceof DoubleChest)) {
			return links.containsKey(ShopUtil.locationToString(block.getLocation()));
		}

		DoubleChest doubleChest = ((DoubleChest)chestHolder);

		Chest leftChest = (Chest) doubleChest.getLeftSide();
		Chest rightChest = (Chest) doubleChest.getRightSide();

		String leftChestLocation = ShopUtil.locationToString(leftChest.getLocation());
		String rightChestLocation = ShopUtil.locationToString(rightChest.getLocation());

		return (links.containsKey(leftChestLocation) || links.containsKey(rightChestLocation));
	}

	public static void setChestName (Block chestBlock, String name) {
		if (chestBlock == null) return;
		BlockState blockState = chestBlock.getState();

		if (!(blockState instanceof Chest)) return;

		Chest chest = (Chest) blockState;

		chest.setCustomName(name);

		chest.update();
	}
}
