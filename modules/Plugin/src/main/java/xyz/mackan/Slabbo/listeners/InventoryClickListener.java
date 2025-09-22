package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.Misc;

public class InventoryClickListener implements Listener {
	private boolean isWaitingForSave = false;
	private int taskId;

	@EventHandler
	public void onInventoryClick (InventoryClickEvent e) {
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.player.enabled")) return;

		InventoryAction action = e.getAction();

		if (
			action != InventoryAction.PLACE_ALL &&
			action != InventoryAction.PLACE_ONE &&
			action != InventoryAction.MOVE_TO_OTHER_INVENTORY
		) return;

		Location inventoryLocation = Misc.getInventoryLocation(e.getInventory());

		if (inventoryLocation == null) return;

		Block chestBlock = inventoryLocation.getBlock();

		if (!Slabbo.getInstance().getChestLinkManager().isChestLinked(chestBlock)) return;

		boolean isTopInventory = false;
		ItemStack item = null;

		if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE) {
			isTopInventory = e.getRawSlot() < e.getInventory().getSize();
			item = e.getCursor();

			if (action == InventoryAction.PLACE_ONE) {
				item = e.getCursor().clone();
				item.setAmount(1);
			}
		}

		if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getClickedInventory().getType() == InventoryType.PLAYER) {
			isTopInventory = true;
			item = e.getCurrentItem();
		}

		if (!isTopInventory) return;

		Shop shop = Slabbo.getInstance().getChestLinkManager().getShopByChestLocation(chestBlock.getLocation());

		ItemStack clonedItem = item.clone();
		ItemStack clonedShopItem = shop.item.clone();

		clonedItem.setAmount(1);
		clonedShopItem.setAmount(1);

		if (!clonedItem.equals(clonedShopItem)) {
			e.setCancelled(true);
			return;
		}

		shop.stock += item.getAmount();

		Slabbo.getInstance().getShopManager().updateShop(shop);

		e.setCurrentItem(null);

		e.getWhoClicked().setItemOnCursor(null);

		e.setCancelled(true);

		int saveTime = Slabbo.getInstance().getConfig().getInt("chestlinks.player.savetime");

		if (!isWaitingForSave) {
			isWaitingForSave = true;
			taskId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Slabbo.getInstance(), new Runnable() {
				public void run () {
					Bukkit.getServer().getScheduler().cancelTask(taskId);
					isWaitingForSave = false;
				}
			}, saveTime * 20); // Delay in ticks
		}
	}

	@EventHandler
	public void onInventoryDrag (InventoryDragEvent e) {
		Inventory inv = e.getInventory();

		if (inv == null) return;

		Location invLocation = Misc.getInventoryLocation(inv);

		if (invLocation == null) return;

		Block chestBlock = invLocation.getBlock();

		if (!Slabbo.getInstance().getChestLinkManager().isChestLinked(chestBlock)) return;

		DragType type = e.getType();

		if (type != DragType.EVEN && type != DragType.SINGLE) return;

		// TODO: Implement this
		e.setCancelled(true);

//		boolean isTopInventory = false;
//
//		Map<Integer, ItemStack> newItems = e.getNewItems();

		// SINGLE, EVEN (Check inv)
	}
}
