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
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.types.Shop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestLinkManager {
	private final ShopManager shopManager;
	private final Map<UUID, String> pendingLinks = new HashMap<>();
	private final Map<String, Shop> links = new HashMap<>();

	public ChestLinkManager(ShopManager shopManager) {
		this.shopManager = shopManager;
	}

	public boolean hasPendingLink(Player p) {
		return pendingLinks.containsKey(p.getUniqueId());
	}

	public boolean isChestLinked(Block block) {
		BlockState blockState = block.getState();
		if (!(blockState instanceof Chest)) return false;

		Chest chest = (Chest) blockState;
		InventoryHolder chestHolder = chest.getInventory().getHolder();

		if (!(chestHolder instanceof DoubleChest)) {
			return links.containsKey(ShopManager.locationToString(block.getLocation()));
		}

		DoubleChest doubleChest = ((DoubleChest) chestHolder);
		Chest leftChest = (Chest) doubleChest.getLeftSide();
		Chest rightChest = (Chest) doubleChest.getRightSide();

		String leftChestLocation = ShopManager.locationToString(leftChest.getLocation());
		String rightChestLocation = ShopManager.locationToString(rightChest.getLocation());

		return (links.containsKey(leftChestLocation) || links.containsKey(rightChestLocation));
	}

	public void setChestName(Block chestBlock, String name) {
		SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();
		if (chestBlock == null) return;
		BlockState blockState = chestBlock.getState();
		if (!(blockState instanceof Chest)) return;
		api.setChestName(chestBlock, name);
	}

	public void removeItemsFromChest(Block chestBlock, ItemStack item) {
		if (chestBlock == null) return;
		BlockState blockState = chestBlock.getState();
		if (!(blockState instanceof Chest)) return;
		Chest chest = (Chest) blockState;
		Inventory inv = chest.getInventory();
		inv.removeItem(item);
		chest.update(true);
	}

	public Shop getShopByChestLocation(Location chestLocation) {
		Shop cachedShop = links.get(ShopManager.locationToString(chestLocation));
		if (cachedShop == null) return null;
		return shopManager.getShop(cachedShop.getLocationString());
	}

	public void removeShopLink(Shop shop) {
		if (shop.linkedChestLocation.isEmpty()) return;
		if (!links.containsKey(shop.linkedChestLocation)) return;
		links.remove(shop.linkedChestLocation);

		Location blockLocation = ShopManager.fromString(shop.linkedChestLocation);
		Block chestBlock = blockLocation.getBlock();
		setChestName(chestBlock, null);
	}

	/**
	 * Returns the map of chest links.
	 */
	public Map<String, Shop> getLinks() {
		return links;
	}

	/**
	 * Clears all chest links.
	 */
	public void clearLinks() {
		links.clear();
	}

	/**
	 * Gets the pending link location string for a player, or null if none.
     */
	public String getPendingLink(Player player) {
		return pendingLinks.get(player.getUniqueId());
	}

	/**
	 * Removes the pending link for a player.
	 */
	public void removePendingLink(Player player) {
		pendingLinks.remove(player.getUniqueId());
	}

	/**
	 * Adds a pending link for a player.
	 */
	public void addPendingLink(Player player, String shopLocation) {
		pendingLinks.put(player.getUniqueId(), shopLocation);
	}

	/**
	 * Adds a chest link.
	 */
	public void addLink(String chestLocation, Shop shop) {
		links.put(chestLocation, shop);
	}
}