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
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.hoppers.enabled")) return;

		Inventory destinationInventory = e.getDestination();
		Inventory sourceInventory = e.getSource();

		Location destinationLocation = Misc.getInventoryLocation(destinationInventory);
		Location sourceLocation = Misc.getInventoryLocation(sourceInventory);

		if (destinationLocation == null || sourceLocation == null) return;

		Block destinationBlock = destinationLocation.getBlock();
		Block sourceBlock = sourceLocation.getBlock();

		if (!Slabbo.getInstance().getChestLinkManager().isChestLinked(destinationBlock)) return;

		if (Slabbo.getInstance().getChestLinkManager().isChestLinked(sourceBlock)) {
			e.setCancelled(true);
			return;
		}

		Shop shop = Slabbo.getInstance().getChestLinkManager().getShopByChestLocation(destinationBlock.getLocation());

		ItemStack item = e.getItem();

		ItemStack itemClone = item.clone();
		ItemStack shopItemClone = shop.item.clone();

		itemClone.setAmount(1);
		shopItemClone.setAmount(1);

		if (!itemClone.equals(shopItemClone)) {
			e.setCancelled(true);
			return;
		}

		shop.stock += item.getAmount();

		Slabbo.getInstance().getShopManager().updateShop(shop);

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Slabbo.getInstance(), new Runnable() {
			@Override
			public void run() {
				destinationInventory.removeItem(item);
			}
		}, 1); // Run it next tick
	}
}