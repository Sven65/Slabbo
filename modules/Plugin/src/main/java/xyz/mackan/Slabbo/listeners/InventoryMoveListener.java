package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.Misc;

public class InventoryMoveListener implements Listener {
	@EventHandler
	public void onInventoryMove(InventoryMoveItemEvent e) {
		Bukkit.getLogger().info("InventoryMoveItemEvent triggered");

		// Check if chest linking is enabled
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;

		Inventory sourceInventory = e.getSource();
		Inventory destinationInventory = e.getDestination();

		Location sourceLocation = Misc.getInventoryLocation(sourceInventory);
		Location destinationLocation = Misc.getInventoryLocation(destinationInventory);

		Bukkit.getLogger().info("Source Location: " + sourceLocation);
		Bukkit.getLogger().info("Destination Location: " + destinationLocation);

		if (sourceLocation == null || destinationLocation == null) return;

		Block sourceBlock = sourceLocation.getBlock();
		Block destinationBlock = destinationLocation.getBlock();

		Bukkit.getLogger().info("Is chest linked? Source: " + Slabbo.getInstance().getChestLinkManager().isChestLinked(sourceBlock));

		// Only process if destination is a linked chest
		if (!Slabbo.getInstance().getChestLinkManager().isChestLinked(destinationBlock)) return;

		Bukkit.getLogger().info("Destination is a linked chest");

		// Prevent moving between two linked chests
		if (Slabbo.getInstance().getChestLinkManager().isChestLinked(sourceBlock)) {
			e.setCancelled(true);
			return;
		}

		Bukkit.getLogger().info("Source is not a linked chest");

		Shop shop = Slabbo.getInstance().getChestLinkManager().getShopByChestLocation(destinationBlock.getLocation());
		if (shop == null) {
			e.setCancelled(true);
			return;
		}

		Bukkit.getLogger().info("Found shop for linked chest: " + shop.location);

		ItemStack item = e.getItem();
		if (item == null) {
			e.setCancelled(true);
			return;
		}

		// Normalize item for comparison
		ItemStack itemClone = item.clone();
		ItemStack shopItemClone = shop.item.clone();
		itemClone.setAmount(1);
		shopItemClone.setAmount(1);

		if (!itemClone.equals(shopItemClone)) {
			e.setCancelled(true);
			return;
		}

		Bukkit.getLogger().info("Item matches shop item: " + itemClone);

		// Improved extraction logic: handle double chests and log inventory types
		boolean isInsertion = Misc.isHopper(sourceInventory) && Misc.isChest(destinationInventory);
		boolean isExtraction = Misc.isChest(sourceInventory) && Misc.isHopper(destinationInventory);
		Bukkit.getLogger().info("[Slabbo] isInsertion: " + isInsertion + ", isExtraction: " + isExtraction);
		Bukkit.getLogger().info("[Slabbo] Source Inventory Holder: " + (sourceInventory.getHolder() != null ? sourceInventory.getHolder().getClass().getName() : "null"));
		Bukkit.getLogger().info("[Slabbo] Destination Inventory Holder: " + (destinationInventory.getHolder() != null ? destinationInventory.getHolder().getClass().getName() : "null"));

		// Always log event trigger and inventory types for debugging
		Bukkit.getLogger().info("[Slabbo] InventoryMoveItemEvent triggered. SourceType: " + sourceInventory.getType() + ", DestType: " + destinationInventory.getType());
		Bukkit.getLogger().info("[Slabbo] SourceHolder: " + (sourceInventory.getHolder() != null ? sourceInventory.getHolder().getClass().getName() : "null") + ", DestHolder: " + (destinationInventory.getHolder() != null ? destinationInventory.getHolder().getClass().getName() : "null"));

		// Handle insertion (hopper to chest)
		if (isInsertion) {
			if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.hoppers.insertionEnabled")) {
				e.setCancelled(true);
				Bukkit.getLogger().info("[Slabbo] Insertion cancelled by config");
				return;
			}
			shop.stock += item.getAmount();
			Slabbo.getInstance().getShopManager().updateShop(shop);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Slabbo.getInstance(), () -> destinationInventory.removeItem(item), 1);
			Bukkit.getLogger().info("[Slabbo] Insertion succeeded, stock updated");
			return;
		}

		// Handle extraction (chest to hopper)
		if (isExtraction) {
			if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.hoppers.extractionEnabled")) {
				e.setCancelled(true);
				Bukkit.getLogger().info("[Slabbo] Extraction cancelled by config");
				return;
			}
			if (shop.stock < item.getAmount()) {
				e.setCancelled(true);
				Bukkit.getLogger().info("[Slabbo] Extraction cancelled, not enough stock");
				return;
			}
			shop.stock -= item.getAmount();
			Slabbo.getInstance().getShopManager().updateShop(shop);
			Bukkit.getLogger().info("[Slabbo] Extraction succeeded, stock updated");
			// Do not cancel event, allow item to move to hopper
			return;
		}

		// If not insertion or extraction, cancel event
		e.setCancelled(true);
	}
}