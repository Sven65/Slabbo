package xyz.mackan.Slabbo.GUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.mackan.Slabbo.GUI.items.GUIItems;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.utils.NameUtil;

public class ShopUserGUI implements Listener {

	private Shop shop;
	private Inventory inv;

	public ShopUserGUI (Shop shop, Player player) {
		this.shop = shop;

		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] Client");

		initializeItems(player);
	}

	public void initializeItems (Player player) {
		ItemStack shopItem = shop.item.clone();

		shopItem.setAmount(shop.quantity);

		inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.stock));
		inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.stock));
		inv.setItem(2, GUIItems.getUserFundsItem(Slabbo.getEconomy().getBalance(player)));

		inv.setItem(4, shopItem);

		inv.setItem(6, GUIItems.getUserInfoItem(shop));
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick (final InventoryClickEvent e) {
		if (e.getInventory() != inv) return;
		e.setCancelled(true);

		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

		Player p = (Player) e.getWhoClicked();

		int slot = e.getRawSlot();

		if (slot <= 8) return; // User clicked shop GUI
	}

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}

}
