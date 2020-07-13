package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;


public class InventoryMoveListener implements Listener {
	private boolean isWaitingForSave;
	private int taskId;

	@EventHandler
	public void onInventoryMove (InventoryMoveItemEvent e) {
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;

		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.hoppers.enabled")) return;

		Inventory destinationInventory = e.getDestination();

		ItemStack item = e.getItem();

		Block destinationBlock = destinationInventory.getLocation().getBlock();

		if (!Slabbo.chestLinkUtil.isChestLinked(destinationBlock)) return;

		Shop shop = Slabbo.chestLinkUtil.getShopByChestLocation(destinationBlock.getLocation());

		ItemStack itemClone = item.clone();
		ItemStack shopItemClone = shop.item.clone();

		itemClone.setAmount(1);
		shopItemClone.setAmount(1);

		if (!itemClone.equals(shopItemClone)) {
			e.setCancelled(true);
			return;
		}

		shop.stock += item.getAmount();

		Slabbo.shopUtil.put(shop.getLocationString(), shop);

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
