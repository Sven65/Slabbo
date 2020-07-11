package xyz.mackan.Slabbo.GUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.mackan.Slabbo.GUI.items.AdminGUIItems;
import xyz.mackan.Slabbo.GUI.items.GUIItems;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.NameUtil;

import java.util.HashMap;

public class ShopAdminGUI implements Listener {

	private Shop shop;
	private Inventory inv;

	private int transferRate = 1;

	public ShopAdminGUI (Shop shop) {
		this.shop = shop;

		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] Owner");

		initializeItems();
	}

	public void initializeItems () {
		ItemStack shopItem = shop.item.clone();

		shopItem.setAmount(shop.quantity);

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


		shop.stock -= (tempTransferRate+leftoverCount);

		DataUtil.saveShops();

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock));
	}

	public void handleChangeRate () {}

	public void handleModify () {}

	public void handleViewAsCustomer (Player player) {
		ShopUserGUI gui = new ShopUserGUI(shop, player);

		gui.openInventory(player);
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
				handleChangeRate();
				break;
			case 7:
				handleModify();
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

}
