package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.Misc;


public class InventoryMoveListener implements Listener {
	private boolean isWaitingForSave;
	private int taskId;

	@EventHandler
	public void onInventoryMove (InventoryMoveItemEvent e) {
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;

		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.hoppers.enabled")) return;

		Inventory destinationInventory = e.getDestination();
		Inventory sourceInventory = e.getSource();

		if (destinationInventory == null) return;

		Location destinationLocation = Misc.getInventoryLocation(destinationInventory);
		Location sourceLocation = Misc.getInventoryLocation(sourceInventory);

		if (destinationLocation == null || sourceLocation == null ) return;

		Block destinationBlock = destinationLocation.getBlock();
		Block sourceBlock = sourceLocation.getBlock();


		if (!ChestLinkManager.isChestLinked(destinationBlock)) return;

		if (ChestLinkManager.isChestLinked(sourceBlock)) {
			e.setCancelled(true);
			return;
		}

		Shop shop = ChestLinkManager.getShopByChestLocation(destinationBlock.getLocation());

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

		ShopManager.put(shop.getLocationString(), shop);

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Slabbo.getInstance(), new Runnable() {
			@Override
			public void run () {
				destinationInventory.removeItem(item);
			}
		}, 1); // Run it next tick

		int saveTime = Slabbo.getInstance().getConfig().getInt("chestlinks.hoppers.savetime");

		if (!isWaitingForSave) {
			isWaitingForSave = true;
			taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Slabbo.getInstance(), new Runnable() {
				public void run () {
					DataUtil.saveShops();
					Bukkit.getServer().getScheduler().cancelTask(taskId);
					isWaitingForSave = false;
				}
			}, saveTime * 20); // Delay in ticks
		}
	}
}
