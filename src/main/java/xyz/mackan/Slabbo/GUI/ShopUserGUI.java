package xyz.mackan.Slabbo.GUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.type.Slab;
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

		shopItem.setAmount(Math.max(shop.quantity, 1));

		if (shop.buyPrice > 0) {
			inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.stock));
		}

		if (shop.sellPrice > 0) {
			inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.stock));
		}

		inv.setItem(4, shopItem);

		inv.setItem(7, GUIItems.getUserFundsItem(Slabbo.getEconomy().getBalance(player)));
		inv.setItem(8, GUIItems.getUserInfoItem(shop));
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	public void handleBuy (HumanEntity humanEntity) {
		double playerFunds = Slabbo.getEconomy().getBalance((OfflinePlayer)humanEntity);

		if (shop.stock == 0) {
			humanEntity.sendMessage(ChatColor.RED+"This shop is out of stock!");
			return;
		}

		int itemCount = Math.min(shop.stock, shop.quantity);

		int totalCost = shop.buyPrice * itemCount;

		if (playerFunds < totalCost) {
			humanEntity.sendMessage(ChatColor.RED+"You don't have enough funds!");
			return;
		}

		PlayerInventory pInv = humanEntity.getInventory();

		ItemStack shopItemClone = shop.item.clone();

		shopItemClone.setAmount(shop.quantity);

		HashMap<Integer, ItemStack> leftovers = pInv.addItem(shopItemClone);

		// TODO: Make this do a dry run to see if the player can acutally get all the items
		int leftoverCount = leftovers
				.values()
				.stream()
				.map(stack -> stack.getAmount())
				.reduce(0, (total, el) -> total + el);


		int totalBought = shop.quantity - leftoverCount;

		shop.stock -= totalBought;

		int actualCost = totalBought * shop.buyPrice;

		humanEntity
			.sendMessage(String.format(
				ChatColor.GREEN+"Bought %d %s for a total of $%d at your shop.",
				totalBought,
				NameUtil.getName(shop.item),
				actualCost
			)
		);

		Slabbo.getEconomy().withdrawPlayer((OfflinePlayer)humanEntity, actualCost);

		OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shop.ownerId);

		Slabbo.getEconomy().depositPlayer(shopOwner, actualCost);

		if (shopOwner.isOnline()) {
			shopOwner
			.getPlayer()
			.sendMessage(
				String.format(
					ChatColor.GREEN+"%s bought %d %s for a total of $%d at your shop.",
					humanEntity.getName(),
					totalBought,
					NameUtil.getName(shop.item),
					actualCost
				)
			);
		}

		DataUtil.saveShops();

		inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.stock));
		inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.stock));
		inv.setItem(7, GUIItems.getUserFundsItem(Slabbo.getEconomy().getBalance((OfflinePlayer)humanEntity)));
	}

	public void handleSell (HumanEntity humanEntity) {
		OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shop.ownerId);

		double shopFunds = Slabbo.getEconomy().getBalance(shopOwner);

		PlayerInventory pInv = humanEntity.getInventory();

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

		if (itemCount <= 0) {
			humanEntity.sendMessage(ChatColor.RED+"You don't have enough items!");
			return;
		}

		itemCount = Math.min(itemCount, shop.quantity);

		int totalCost = shop.sellPrice * itemCount;

		if (shopFunds < totalCost) {
			humanEntity.sendMessage(ChatColor.RED+"The shop doesn't have enough funds!");
			return;
		}

		ItemStack shopItemClone = shop.item.clone();

		shop.stock += itemCount;

		DataUtil.saveShops();

		shopItemClone.setAmount(itemCount);

		pInv.removeItem(shopItemClone);

		Slabbo.getEconomy().withdrawPlayer(shopOwner, totalCost);

		Slabbo.getEconomy().depositPlayer((OfflinePlayer)humanEntity, totalCost);

		if (shopOwner.isOnline()) {
			shopOwner
					.getPlayer()
					.sendMessage(
							String.format(
								ChatColor.GREEN+"%s sold %d %s for a total of $%d at your shop.",
								humanEntity.getName(),
								itemCount,
								NameUtil.getName(shop.item),
								totalCost
							)
					);
		}

		inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.stock));
		inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.stock));
		inv.setItem(7, GUIItems.getUserFundsItem(Slabbo.getEconomy().getBalance((OfflinePlayer)humanEntity)));

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
				handleBuy(p);
				break;
			case 1:
				handleSell(p);
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
