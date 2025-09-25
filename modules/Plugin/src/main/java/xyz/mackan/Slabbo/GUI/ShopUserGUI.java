package xyz.mackan.Slabbo.GUI;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.mackan.Slabbo.GUI.items.GUIItems;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.InventoryUtil;
import xyz.mackan.Slabbo.utils.NameUtil;

import java.util.HashMap;

public class ShopUserGUI implements Listener {
	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();
	SlabboAPI slabboAPI = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	private Shop shop;
	private Inventory inv;

	public ShopUserGUI (Shop shop, Player player) {
		this.shop = shop;

		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, LocaleManager.getString("general.shop-prefix")+ LocaleManager.getString("gui.client"));

		initializeItems(player);
	}

	public void initializeItems (Player player) {
		boolean isLimitedShop = shop.admin && shop.shopLimit != null && shop.shopLimit.enabled;


		ItemStack shopItem = shop.item.clone();

		shopItem.setAmount(Math.max(shop.quantity, 1));

		if (shop.buyPrice > -1 && shop.quantity > 0) {
			if (isLimitedShop) {
				inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.shopLimit.buyStockLeft, shop.admin, isLimitedShop));
			} else {
				inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.stock, shop.admin, isLimitedShop));
			}
		}

		if (shop.sellPrice > -1 && shop.quantity > 0) {
			if (isLimitedShop) {
				inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.shopLimit.sellStockLeft, shop.admin, isLimitedShop));
			} else {
				inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.stock, shop.admin, isLimitedShop));
			}
		}

		inv.setItem(4, shopItem);

		if (Slabbo.getInstance().getConfig().getBoolean("disableShops", false)) return;

		inv.setItem(6, GUIItems.getSellersNoteItem(shop.note));
		inv.setItem(7, GUIItems.getUserFundsItem(Slabbo.getEconomy().getBalance(player)));
		inv.setItem(8, GUIItems.getUserInfoItem(shop));
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	public void handleBuy (HumanEntity humanEntity) {
		double playerFunds = Slabbo.getEconomy().getBalance((OfflinePlayer)humanEntity);

		boolean isLimitedShop = shop.admin && shop.shopLimit != null && shop.shopLimit.enabled;


		if (shop.stock <= 0 && !shop.admin) {
			humanEntity.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.shop-errors.out-of-stock"));
			((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		if (shop.stock < shop.quantity && !shop.admin) {
			humanEntity.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.shop-errors.not-enough-stock"));
			((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		if (isLimitedShop) {
			if (shop.shopLimit.buyStockLeft <= 0) {
				humanEntity.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.shop-errors.buy-limit-reached"));
				((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
				return;
			}
		}

		int itemCount = Math.min(shop.stock, shop.quantity);

		if (shop.admin) {
			if (isLimitedShop) {
				itemCount = Math.min(shop.shopLimit.buyStockLeft, shop.quantity);
			} else {
				itemCount = shop.quantity;
			}
		}

		double totalCost = shop.buyPrice;// * itemCount;

		if (playerFunds < totalCost) {
			humanEntity.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.shop-errors.not-enough-funds"));
			((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		boolean sellOversized = Slabbo.getInstance().getConfig().getBoolean("sellOversized", false);

		PlayerInventory pInv = humanEntity.getInventory();

		ItemStack shopItemClone = shop.item.clone();

		int leftoverCount = InventoryUtil.addItemsToPlayerInventory(pInv, shopItemClone, itemCount, sellOversized);

		int totalBought = itemCount - leftoverCount;

		if (totalBought < shop.quantity) {
			shopItemClone.setAmount(totalBought);
			pInv.removeItem(shopItemClone);

			humanEntity.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.shop-errors.not-enough-inventory-space"));
			((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		if (!shop.admin) {
			shop.stock -= totalBought;
		}

		if (isLimitedShop) {
			shop.shopLimit.buyStockLeft -= totalBought;
		}

		Slabbo.getInstance().getShopManager().updateShop(shop);

		//int actualCost = totalBought * shop.buyPrice;
		double actualCost = shop.buyPrice;

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("count", totalBought);
		replacementMap.put("item", "'"+NameUtil.getName(shop.item)+"'");
		replacementMap.put("cost", LocaleManager.getCurrencyString(actualCost));
		replacementMap.put("user", humanEntity.getName());

		String userMessage = LocaleManager.replaceKey("success-message.client.buy-success", replacementMap);
		String ownerMessage = LocaleManager.replaceKey("success-message.owner.buy-success", replacementMap);

		humanEntity.sendMessage(ChatColor.GREEN+userMessage);

		((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BUY_SELL_SUCCESS"), 1, 1);


		Slabbo.getEconomy().withdrawPlayer((OfflinePlayer)humanEntity, actualCost);

		OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shop.ownerId);

		if (!shop.admin) {
			Slabbo.getEconomy().depositPlayer(shopOwner, actualCost);

			if (shopOwner.isOnline()) {
				shopOwner.getPlayer().sendMessage(ChatColor.GREEN+ownerMessage);
			}
		}

		if (shop.commandList != null) {
			replacementMap.put("newStock", shop.stock);
			shop.commandList.executeBuyCommands(replacementMap);
		}

		if (isLimitedShop) {
			inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.shopLimit.buyStockLeft, shop.admin, isLimitedShop));
			inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.shopLimit.sellStockLeft, shop.admin, isLimitedShop));
		} else {
			inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.stock, shop.admin, isLimitedShop));
			inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.stock, shop.admin, isLimitedShop));
		}

		inv.setItem(7, GUIItems.getUserFundsItem(Slabbo.getEconomy().getBalance((OfflinePlayer)humanEntity)));
	}

	// Player selling to a shop
	// @TODO: Refactor (because this is massive.)
	public void handleSell(HumanEntity humanEntity) {
		OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(shop.ownerId);

		boolean isLimitedShop = shop.admin && shop.shopLimit != null && shop.shopLimit.enabled;

		double shopFunds = Slabbo.getEconomy().getBalance(shopOwner);

		if (shop.admin) {
			shopFunds = Integer.MAX_VALUE;
		}

		PlayerInventory pInv = humanEntity.getInventory();

		int itemCount = 0;

		ItemStack[] itemStacks = slabboAPI.getStorageContents(pInv);

		// Count items in main inventory
		for (ItemStack inventoryItem : itemStacks) {
			if (inventoryItem == null || inventoryItem.getType() == Material.AIR) continue;
			ItemStack clonedItem = inventoryItem.clone();
			clonedItem.setAmount(1);
			if (clonedItem.equals(shop.item)) {
				itemCount += inventoryItem.getAmount();
			}
		}

		// Use abstract API for offhand; returns null in 1.8
		ItemStack offhandStack = slabboAPI.getItemInOffHand(pInv);

		if (offhandStack != null && offhandStack.getType() != Material.AIR) {
			ItemStack offhandClone = offhandStack.clone();
			offhandClone.setAmount(1);
			if (offhandClone.equals(shop.item)) {
				itemCount += offhandStack.getAmount();
			}
		}

		if (itemCount < shop.quantity || itemCount <= 0) {
			humanEntity.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.shop-errors.not-enough-items"));
			((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		itemCount = Math.min(itemCount, shop.quantity);

		if (isLimitedShop) {
			if (itemCount > shop.shopLimit.sellStockLeft) {
				humanEntity.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.shop-errors.sell-limit-reached"));
				((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
				return;
			}
		}

		double totalCost = shop.sellPrice;

		if (shopFunds < totalCost) {
			humanEntity.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.shop-errors.not-enough-shop-funds"));
			((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		ItemStack shopItemClone = shop.item.clone();

		if (!shop.admin) {
			shop.stock += itemCount;
		}

		if (isLimitedShop) {
			shop.shopLimit.sellStockLeft -= itemCount;
		}
		Slabbo.getInstance().getShopManager().updateShop(shop);

		shopItemClone.setAmount(itemCount);

		ItemStack itemInShop = shop.item.clone();
		itemInShop.setAmount(1);

		// ===== Fixed removal logic =====
		int remainingToRemove = itemCount;

		// Remove from offhand first
		offhandStack = slabboAPI.getItemInOffHand(pInv);
		if (offhandStack != null && offhandStack.getType() != Material.AIR && offhandStack.isSimilar(shop.item)) {
			int offhandAmount = offhandStack.getAmount();
			if (offhandAmount >= remainingToRemove) {
				offhandStack.setAmount(offhandAmount - remainingToRemove);
				if (offhandStack.getAmount() <= 0) {
					pInv.setItemInOffHand(null);
				} else {
					pInv.setItemInOffHand(offhandStack);
				}
				remainingToRemove = 0;
			} else {
				pInv.setItemInOffHand(null);
				remainingToRemove -= offhandAmount;
			}
		}

		// Remove remaining items from inventory
		if (remainingToRemove > 0) {
			ItemStack[] contents = pInv.getContents();
			for (int i = 0; i < contents.length && remainingToRemove > 0; i++) {
				ItemStack stack = contents[i];
				if (stack != null && stack.isSimilar(shop.item)) {
					int stackAmount = stack.getAmount();
					if (stackAmount > remainingToRemove) {
						stack.setAmount(stackAmount - remainingToRemove);
						contents[i] = stack;
						remainingToRemove = 0;
					} else {
						contents[i] = null;
						remainingToRemove -= stackAmount;
					}
				}
			}
			pInv.setContents(contents);
		}
		// ===== End fixed removal logic =====

		// --- SHOP TAX LOGIC START ---
		boolean taxEnabled = Slabbo.getInstance().getConfig().getBoolean("enableShopTax", false);
		boolean sellerExempt = shopOwner.isOnline() && shopOwner.getPlayer().hasPermission("slabbo.tax.exempt");
		boolean buyerExempt = humanEntity.hasPermission("slabbo.tax.exempt");
		String taxMode = Shop.resolveShopTaxMode(shop, shop.location);
		String taxRate = Shop.resolveShopTaxRate(shop, shop.location);

		// Calculate tax only if enabled and not exempt
		double taxAmount = 0.0;
		if (taxEnabled && !sellerExempt && !shop.admin) {
			taxAmount = Shop.calculateTaxAmount(taxRate, totalCost);
		}

		if (taxEnabled && taxAmount > 0) {
			if (taxMode.equalsIgnoreCase("buyer")) {
				// Buyer pays tax up front (not typical for sell, but supported)
				Slabbo.getEconomy().depositPlayer((OfflinePlayer) humanEntity, totalCost - taxAmount);
				if (!shop.admin) {
					Slabbo.getEconomy().withdrawPlayer(shopOwner, totalCost);
				}
				// Inform seller of tax
				humanEntity.sendMessage(ChatColor.YELLOW + "Tax deducted: " + LocaleManager.getCurrencyString(taxAmount));
			} else {
				// Seller pays tax (deducted from their profit)
				Slabbo.getEconomy().depositPlayer((OfflinePlayer) humanEntity, totalCost);
				if (!shop.admin) {
					Slabbo.getEconomy().withdrawPlayer(shopOwner, totalCost + taxAmount);
					if (taxAmount > 0 && shopOwner.isOnline()) {
						shopOwner.getPlayer().sendMessage(ChatColor.YELLOW + "Tax applied: " + LocaleManager.getCurrencyString(taxAmount));
					}
				}
			}
		} else {
			// No tax or exempt
			Slabbo.getEconomy().depositPlayer((OfflinePlayer) humanEntity, totalCost);
			if (!shop.admin) {
				Slabbo.getEconomy().withdrawPlayer(shopOwner, totalCost);
			}
		}
		// --- SHOP TAX LOGIC END ---

		HashMap<String, Object> replacementMap = new HashMap<>();
		replacementMap.put("count", itemCount);
		replacementMap.put("item", "'" + NameUtil.getName(shop.item) + "'");
		replacementMap.put("cost", LocaleManager.getCurrencyString(totalCost));
		replacementMap.put("user", humanEntity.getName());

		String userMessage = LocaleManager.replaceKey("success-message.client.sell-success", replacementMap);
		String ownerMessage = LocaleManager.replaceKey("success-message.owner.sell-success", replacementMap);

		humanEntity.sendMessage(ChatColor.GREEN + userMessage);
		((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("BUY_SELL_SUCCESS"), 1, 1);

		if (shop.commandList != null) {
			replacementMap.put("newStock", shop.stock);
			shop.commandList.executeSellCommands(replacementMap);
		}

		if (!shop.admin) {
			Slabbo.getEconomy().withdrawPlayer(shopOwner, totalCost);

			if (shopOwner.isOnline()) {
				shopOwner.getPlayer().sendMessage(ChatColor.GREEN + ownerMessage);
			}
		}

		if (shop.buyPrice > -1 && shop.quantity > 0) {
			if (isLimitedShop) {
				inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.shopLimit.buyStockLeft, shop.admin, isLimitedShop));
			} else {
				inv.setItem(0, GUIItems.getUserBuyItem(NameUtil.getName(shop.item), shop.quantity, shop.buyPrice, shop.stock, shop.admin, isLimitedShop));
			}
		}

		if (shop.sellPrice > -1 && shop.quantity > 0) {
			if (isLimitedShop) {
				inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.shopLimit.sellStockLeft, shop.admin, isLimitedShop));
			} else {
				inv.setItem(1, GUIItems.getUserSellItem(NameUtil.getName(shop.item), shop.quantity, shop.sellPrice, shop.stock, shop.admin, isLimitedShop));
			}
		}

		inv.setItem(7, GUIItems.getUserFundsItem(Slabbo.getEconomy().getBalance((OfflinePlayer) humanEntity)));
	}




	@EventHandler
	public void onInventoryClick (final InventoryClickEvent e) {
		if (!e.getInventory().equals(inv)) return;
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
		if (e.getInventory().equals(inv)) {
			e.setCancelled(true);
		}
	}

}
