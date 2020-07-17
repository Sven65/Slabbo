package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.ChestLinkUtil;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		if(e.getInventory().getLocation() == null) return;

		Block chestBlock = e.getInventory().getLocation().getBlock();

		if (chestBlock == null) return;

		if (!Slabbo.chestLinkUtil.isChestLinked(chestBlock)) return;



		boolean isTopInventory = false;
		ItemStack item = null;

		Inventory chestInv = null;

		if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE) {
			isTopInventory = e.getRawSlot() < e.getInventory().getSize();
			chestInv = e.getClickedInventory();
			item = e.getCursor();

			if (action == InventoryAction.PLACE_ONE) {
				item = e.getCursor().clone();
				item.setAmount(1);
			}
		}

		if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getClickedInventory().getType() == InventoryType.PLAYER) {
			isTopInventory = true;
			item = e.getCurrentItem();
			chestInv = e.getInventory();
		}

		if (!isTopInventory) return;

		Shop shop = Slabbo.chestLinkUtil.getShopByChestLocation(chestBlock.getLocation());

		ItemStack clonedItem = item.clone();
		ItemStack clonedShopItem = shop.item.clone();

		clonedItem.setAmount(1);
		clonedShopItem.setAmount(1);

		if (!clonedItem.equals(clonedShopItem)) {
			e.setCancelled(true);
			return;
		}

		shop.stock += item.getAmount();

		Slabbo.shopUtil.put(shop.getLocationString(), shop);

		e.setCurrentItem(null);

		e.getWhoClicked().setItemOnCursor(null);

		e.setCancelled(true);

		int saveTime = Slabbo.getInstance().getConfig().getInt("chestlinks.player.savetime");

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

	@EventHandler
	public void onInventoryDrag (InventoryDragEvent e) {
		Block chestBlock = e.getInventory().getLocation().getBlock(); // TODO check this

		if (chestBlock == null) return;

		if (!Slabbo.chestLinkUtil.isChestLinked(chestBlock)) return;

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
