package xyz.mackan.Slabbo.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.types.Shop;

import java.util.HashMap;
import java.util.UUID;

public class ChestLinkManager {
	public static HashMap<UUID, String> pendingLinks = new HashMap<UUID, String>();

	// Chest Location, Shop
	// TODO: Make this Chest Location, Shop Location
	public static HashMap<String, Shop> links = new HashMap<String, Shop>();

	public static boolean hasPendingLink (Player p) {
		UUID id = p.getUniqueId();

		return pendingLinks.containsKey(id);
	}

	public static boolean isChestLinked (Block block) {
		BlockState blockState = block.getState();

		if (!(blockState instanceof Chest)) return false;

		Chest chest = (Chest) blockState;

		InventoryHolder chestHolder = chest.getInventory().getHolder();

		if (!(chestHolder instanceof DoubleChest)) {
			return links.containsKey(ShopManager.locationToString(block.getLocation()));
		}

		DoubleChest doubleChest = ((DoubleChest)chestHolder);

		Chest leftChest = (Chest) doubleChest.getLeftSide();
		Chest rightChest = (Chest) doubleChest.getRightSide();

		String leftChestLocation = ShopManager.locationToString(leftChest.getLocation());
		String rightChestLocation = ShopManager.locationToString(rightChest.getLocation());

		return (links.containsKey(leftChestLocation) || links.containsKey(rightChestLocation));
	}

	public static void setChestName (Block chestBlock, String name) {
		SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

		if (chestBlock == null) return;
		BlockState blockState = chestBlock.getState();

		if (!(blockState instanceof Chest)) return;

		api.setChestName(chestBlock, name);
	}

	public static void removeItemsFromChest (Block chestBlock, ItemStack item) {
		if (chestBlock == null) return;
		BlockState blockState = chestBlock.getState();

		if (!(blockState instanceof Chest)) return;

		Chest chest = (Chest) blockState;

		Inventory inv = chest.getInventory();

		inv.removeItem(item);

		chest.update(true);
	}

	public static Shop getShopByChestLocation (Location chestLocation) {
		Shop cachedShop = links.get(ShopManager.locationToString(chestLocation));

		if (cachedShop == null) return null;

		return ShopManager.shops.get(cachedShop.getLocationString());
	}
}
