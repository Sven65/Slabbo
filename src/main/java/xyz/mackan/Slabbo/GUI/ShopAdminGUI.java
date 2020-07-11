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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.mackan.Slabbo.GUI.items.AdminGUIItems;
import xyz.mackan.Slabbo.GUI.items.GUIItems;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.NameUtil;

import java.util.HashMap;
import java.util.UUID;

public class ShopAdminGUI implements Listener {

	private Shop shop;
	private Inventory inv;

	private int transferRate = 1;

	private boolean isChangingRate = false;
	private UUID waitingPlayerId;


	public ShopAdminGUI (Shop shop) {
		this.shop = shop;

		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] Owner");

		initializeItems();
	}

	public void initializeItems () {
		ItemStack shopItem = shop.item.clone();

		shopItem.setAmount(Math.max(shop.quantity, 1));

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock));
		inv.setItem(2, AdminGUIItems.getAmountItem(transferRate));

		inv.setItem(4, shopItem);

		inv.setItem(6, GUIItems.getUserInfoItem(shop));
		inv.setItem(7, AdminGUIItems.getModifyItem());
		inv.setItem(8, AdminGUIItems.getViewAsCustomerItem());
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	public void handleDeposit (HumanEntity humanEntity) {
		Player player = (Player) humanEntity;

		PlayerInventory pInv = player.getInventory();

		int itemCount = 0;

		ItemStack[] itemStacks = pInv.getContents();

		for (ItemStack inventoryItem : itemStacks) {
			if (inventoryItem == null || inventoryItem.getType() == Material.AIR) continue;
			ItemStack clonedItem = inventoryItem.clone();
			clonedItem.setAmount(1);
			if (clonedItem.equals(shop.item)) {
				itemCount += inventoryItem.getAmount();
			}
		}

		int tempTransferRate = Math.min(itemCount, transferRate);

		ItemStack shopItemClone = shop.item.clone();

		shop.stock += tempTransferRate;

		DataUtil.saveShops();

		shopItemClone.setAmount(tempTransferRate);

		pInv.removeItem(shopItemClone);

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock));
	}

	public void handleWithdraw (HumanEntity humanEntity) {
		Player player = (Player) humanEntity;

		PlayerInventory pInv = player.getInventory();

		int tempTransferRate = Math.min(shop.stock, transferRate);

		ItemStack shopItemClone = shop.item.clone();

		shopItemClone.setAmount(tempTransferRate);

		HashMap<Integer, ItemStack> leftovers = pInv.addItem(shopItemClone);

		int leftoverCount = leftovers
				.values()
				.stream()
				.map(stack -> stack.getAmount())
				.reduce(0, (total, el) -> total + el);


		shop.stock -= (tempTransferRate-leftoverCount);

		DataUtil.saveShops();

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock));
	}

	public void handleChangeRate (HumanEntity humanEntity) {
		isChangingRate = true;
		waitingPlayerId = humanEntity.getUniqueId();

		humanEntity.sendMessage("Please type the new rate");
		humanEntity.closeInventory();
	}

	public void handleModify (HumanEntity humanEntity) {
		ShopCreationGUI gui = new ShopCreationGUI(shop.location, shop);

		gui.openInventory(humanEntity);
	}

	public void handleViewAsCustomer (HumanEntity humanEntity) {
		ShopUserGUI gui = new ShopUserGUI(shop, (Player)humanEntity);

		gui.openInventory(humanEntity);
	}


	@EventHandler
	public void onInventoryClick (final InventoryClickEvent e) {
		if (e.getInventory() != inv) return;
		e.setCancelled(true);

		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

		Player p = (Player) e.getWhoClicked();

		int slot = e.getRawSlot();

		if (slot > 8) return; // User clicked outside shop GUI

		switch (slot) {
			case 0:
				handleDeposit(p);
				break;
			case 1:
				handleWithdraw(p);
				break;
			case 2:
				handleChangeRate(p);
				break;
			case 7:
				handleModify(p);
				break;
			case 8:
				handleViewAsCustomer(p);
				break;
		}
	}

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onChat (final AsyncPlayerChatEvent e) {
		if (!e.getPlayer().getUniqueId().equals(waitingPlayerId)) return;

		if (isChangingRate) {
			isChangingRate = false;
			waitingPlayerId = null;

			e.setCancelled(true);

			int value = Integer.parseInt(e.getMessage());

			if (value < 0) { value = 0; }

			transferRate = value;

			new BukkitRunnable() {
				public void run () {
					inv.setItem(2, AdminGUIItems.getAmountItem(transferRate));
					openInventory(e.getPlayer());
				}
			}.runTask(Slabbo.getInstance());
		}
	}
}
