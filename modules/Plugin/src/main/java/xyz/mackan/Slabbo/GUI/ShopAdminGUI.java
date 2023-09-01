package xyz.mackan.Slabbo.GUI;

import org.bukkit.*;

import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
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
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.types.ShopAction;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.InventoryUtil;
import xyz.mackan.Slabbo.utils.NameUtil;
import xyz.mackan.Slabbo.manager.ShopManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class ShopAdminGUI implements Listener {
	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();
	private static SlabboAPI slabboAPI = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();



	private Shop shop;
	private Inventory inv;

	private int transferRate = 1;

	private boolean isChangingRate = false;
	private UUID waitingPlayerId;


	public ShopAdminGUI (Shop shop, Player humanEntity) {
		this.shop = shop;

		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] "+LocaleManager.getString("gui.owner-title"));

		initializeItems(humanEntity);
	}

	public static Location getSoundLocation(Shop shop, Location playerLocation) {
		if (!shop.virtual) return shop.location;
		return playerLocation;
	}

	public void initializeItems (Player humanEntity) {
		boolean disableShops = Slabbo.getInstance().getConfig().getBoolean("disableShops", false);

		ItemStack shopItem = shop.item.clone();

		shopItem.setAmount(Math.max(shop.quantity, 1));



		if (!disableShops) {
			inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
			inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
			inv.setItem(2, AdminGUIItems.getAmountItem(transferRate));

			inv.setItem(6, GUIItems.getUserInfoItem(shop));
			inv.setItem(7, AdminGUIItems.getModifyItem());
			inv.setItem(8, AdminGUIItems.getViewAsCustomerItem());
		}

		inv.setItem(4, shopItem);

		if (disableShops) return;

		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;

		if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enforcepermission")) {
			if (!humanEntity.hasPermission("slabbo.link")) return;
		}

		boolean hasPendingLink = ChestLinkManager.hasPendingLink(humanEntity);
		boolean hasExistingLink = (shop.linkedChestLocation != null && !shop.linkedChestLocation.isEmpty());

		if (hasPendingLink && ChestLinkManager.pendingLinks.containsValue(shop.getLocationString())) {
			// Current shop's being linked
			inv.setItem(5, AdminGUIItems.getUnlinkChestItem());
		} else if (!hasPendingLink && hasExistingLink) {
			// Current shop isn't being linked, but it has one
			inv.setItem(5, AdminGUIItems.getUnlinkChestItem());
		} else if (hasPendingLink && !ChestLinkManager.pendingLinks.containsValue(shop.getLocationString())) {
			// A link is in progress, but it's not the current shop
			inv.setItem(5, AdminGUIItems.getLinkChestItem());
		} else if (!hasPendingLink && !hasExistingLink) {
			inv.setItem(5, AdminGUIItems.getLinkChestItem());
		}
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	public void handleDeposit (HumanEntity humanEntity, ClickType clickType) {
		boolean isBulk = clickType.equals(ClickType.SHIFT_LEFT);

		Player player = (Player) humanEntity;

		PlayerInventory pInv = player.getInventory();

		//off hand go brrrrr
		ItemStack offhandItem = slabboAPI.getItemInOffHand(pInv);

		// Armor slots too!
		ItemStack[] armorItems = pInv.getArmorContents();

		int itemCount = 0;
		int offhandCount = 0;

		int armorCount = 0;

		if (offhandItem != null && offhandItem.getType() != Material.AIR) {
			ItemStack clonedOffhand = offhandItem.clone();
			clonedOffhand.setAmount(1);

			if (clonedOffhand.equals(shop.item)) {
				offhandCount = offhandItem.getAmount();
			}
		}

		if (armorItems.length > 0) {
			for (ItemStack armorItem : armorItems) {
				if (armorItem == null || armorItem.getType() == Material.AIR) continue;
				ItemStack clonedItem = armorItem.clone();
				clonedItem.setAmount(1);
				if (clonedItem.equals(shop.item)) {
					armorCount += armorItem.getAmount();
				}
			}
		}

		ItemStack[] itemStacks = pInv.getContents();

		for (ItemStack inventoryItem : itemStacks) {
			if (inventoryItem == null || inventoryItem.getType() == Material.AIR) continue;
			ItemStack clonedItem = inventoryItem.clone();
			clonedItem.setAmount(1);
			if (clonedItem.equals(shop.item)) {
				itemCount += inventoryItem.getAmount();
			}
		}

		int maxShopStock = Slabbo.getInstance().getConfig().getInt("maxStockSize", -1);

		if (maxShopStock <= -1) maxShopStock = Integer.MAX_VALUE;

		itemCount -= offhandCount;
		itemCount -= armorCount;

		int tempTransferRate = Math.min(itemCount, transferRate);

		if (isBulk) {
			tempTransferRate = itemCount;

			if (shop.stock + tempTransferRate > maxShopStock) {
				tempTransferRate = maxShopStock - shop.stock;
			}
		}

		ItemStack shopItemClone = shop.item.clone();

		if (!shop.admin) {
			if (shop.stock + tempTransferRate > maxShopStock || shop.stock >= maxShopStock) {
				player.playSound(this.getSoundLocation(shop, player.getLocation()), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
				player.sendMessage(ChatColor.RED+LocaleManager.replaceSingleKey("error-message.shop-errors.stock-limit-reached", "limit", maxShopStock));
				return;
			}
			shop.stock += tempTransferRate;
		}

		player.playSound(this.getSoundLocation(shop, player.getLocation()), slabboSound.getSoundByKey("BUY_SELL_SUCCESS"), 1, 1);

		DataUtil.saveShops();

		shopItemClone.setAmount(tempTransferRate);

		pInv.removeItem(shopItemClone);

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
	}

	public void handleWithdraw (HumanEntity humanEntity, ClickType clickType) {
		boolean isBulk = clickType.equals(ClickType.SHIFT_LEFT);
		boolean withdrawOversized = Slabbo.getInstance().getConfig().getBoolean("withdrawOversized", false);


		Player player = (Player) humanEntity;

		PlayerInventory pInv = player.getInventory();

		int tempTransferRate = Math.min(shop.stock, transferRate);


		if (shop.admin) {
			tempTransferRate = transferRate;
		}

		if (isBulk) {
			int maxItems = 4 * 9 * 64;

			if (shop.admin) {
				tempTransferRate = maxItems;
			} else {
				tempTransferRate = Math.min(maxItems, shop.stock);
			}

		}

		if (tempTransferRate <= 0) {
			return;
		}

		ItemStack shopItemClone = shop.item.clone();

		int leftoverCount = InventoryUtil.addItemsToPlayerInventory(pInv, shopItemClone, tempTransferRate, withdrawOversized);


		if (!shop.admin) {
			shop.stock -= (tempTransferRate - leftoverCount);
		}

		player.playSound(this.getSoundLocation(shop, player.getLocation()), slabboSound.getSoundByKey("BUY_SELL_SUCCESS"), 1, 1);

		DataUtil.saveShops();

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
	}

	public void handleChangeRate (HumanEntity humanEntity) {
		isChangingRate = true;
		waitingPlayerId = humanEntity.getUniqueId();

		humanEntity.sendMessage(LocaleManager.getString("general.general.type-new-rate"));
		((Player) humanEntity).playSound(this.getSoundLocation(shop, humanEntity.getLocation()), slabboSound.getSoundByKey("QUESTION"), 1, 1);

		humanEntity.closeInventory();
	}

	public void handleModify (HumanEntity humanEntity) {
		ShopCreationGUI gui = null;
		if (shop.virtual) {
			gui = new ShopCreationGUI(shop,true);
		} else {
			gui = new ShopCreationGUI(shop.location, shop);
		}

		// shop.location
		((Player) humanEntity).playSound(this.getSoundLocation(shop, humanEntity.getLocation()), slabboSound.getSoundByKey("NAVIGATION"), 1, 1);

		gui.openInventory(humanEntity);
	}

	public void handleViewAsCustomer (HumanEntity humanEntity) {
		((Player) humanEntity).playSound(this.getSoundLocation(shop, humanEntity.getLocation()), slabboSound.getSoundByKey("NAVIGATION"), 1, 1);

		ShopUserGUI gui = new ShopUserGUI(shop, (Player)humanEntity);

		gui.openInventory(humanEntity);
	}

	public void handleChestLink (HumanEntity humanEntity) {
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;
		if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enforcepermission") && !humanEntity.hasPermission("slabbo.link")) return;
		Player p = (Player)humanEntity;

		boolean hasPendingLink = ChestLinkManager.hasPendingLink(p);
		boolean hasExistingLink = shop.linkedChestLocation != null && !shop.linkedChestLocation.isEmpty();

		if (hasPendingLink && ChestLinkManager.pendingLinks.containsValue(shop.getLocationString())) {
			// Current shop's being linked
			ChestLinkManager.pendingLinks.remove(p.getUniqueId());
			p.sendMessage(ChatColor.RED+LocaleManager.getString("success-message.chestlink.linking-cancelled"));
			p.playSound(this.getSoundLocation(shop, p.getLocation()), slabboSound.getSoundByKey("CANCEL"), 1, 1);
			inv.setItem(5, AdminGUIItems.getLinkChestItem());
			return;
		} else if (!hasPendingLink && hasExistingLink) {
			// Current shop isn't being linked, but it has one
			ChestLinkManager.links.remove(shop.linkedChestLocation);

			Location blockLocation = ShopManager.fromString(shop.linkedChestLocation);

			Block chestBlock = blockLocation.getBlock();

			ChestLinkManager.setChestName(chestBlock, null);

			shop.linkedChestLocation = null;

			ShopManager.put(shop.getLocationString(), shop);

			p.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.chestlink.linking-removed"));
			inv.setItem(5, AdminGUIItems.getLinkChestItem());

			p.playSound(this.getSoundLocation(shop, p.getLocation()), slabboSound.getSoundByKey("DESTROY"), 1, 1);

			DataUtil.saveShops();

			return;
		} else if (hasPendingLink && !ChestLinkManager.pendingLinks.containsValue(shop.getLocationString())) {
			// A link is in progress, but it's not the current shop
			ChestLinkManager.pendingLinks.remove(p.getUniqueId());
			p.sendMessage(ChatColor.RED+LocaleManager.getString("success-message.chestlink.previous-linking-cancelled"));
			inv.setItem(5, AdminGUIItems.getLinkChestItem());
		}


		ChestLinkManager.pendingLinks.put(p.getUniqueId(), ShopManager.locationToString(shop.location));

		p.sendMessage(LocaleManager.getString("general.chestlink.crouch-to-link"));
		p.closeInventory();

		p.playSound(this.getSoundLocation(shop, p.getLocation()), slabboSound.getSoundByKey("QUESTION"), 1, 1);
	}

	@EventHandler
	public void onInventoryClick (final InventoryClickEvent e) {
		if (!e.getInventory().equals(inv)) return;
		e.setCancelled(true);

		if (Slabbo.getInstance().getConfig().getBoolean("disableShops", false)) return;


		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

		Player p = (Player) e.getWhoClicked();

		int slot = e.getRawSlot();

		if (slot > 8) return; // User clicked outside shop GUI

		switch (slot) {
			case 0:
				handleDeposit(p, e.getClick());
				break;
			case 1:
				handleWithdraw(p, e.getClick());
				break;
			case 2:
				handleChangeRate(p);
				break;
			case 5:
				handleChestLink(p);
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
		if (e.getInventory().equals(inv)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat (final AsyncPlayerChatEvent e) {
		if (!e.getPlayer().getUniqueId().equals(waitingPlayerId)) return;

		if (isChangingRate) {
			isChangingRate = false;
			waitingPlayerId = null;

			e.setCancelled(true);

			int value = 0;

			try {
				value = Integer.parseInt(e.getMessage());
			} catch (NumberFormatException error) {
				e.getPlayer().sendMessage(ChatColor.RED+ LocaleManager.getString("error-message.modify.not-a-valid-number"));
			}

			if (value < 0) { value = 0; }

			transferRate = value;

			new BukkitRunnable() {
				public void run () {
					e.getPlayer().playSound(ShopAdminGUI.getSoundLocation(shop, e.getPlayer().getLocation()), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
					inv.setItem(2, AdminGUIItems.getAmountItem(transferRate));
					openInventory(e.getPlayer());
				}
			}.runTask(Slabbo.getInstance());
		}
	}
}
