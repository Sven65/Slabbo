package xyz.mackan.Slabbo.GUI;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.ChestLinkUtil;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.NameUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.HashMap;
import java.util.UUID;

public class ShopAdminGUI implements Listener {
	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();


	private Shop shop;
	private Inventory inv;

	private int transferRate = 1;

	private boolean isChangingRate = false;
	private UUID waitingPlayerId;


	public ShopAdminGUI (Shop shop, Player humanEntity) {
		this.shop = shop;

		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] "+Slabbo.localeManager.getString("gui.owner-title"));

		initializeItems(humanEntity);
	}

	public void initializeItems (Player humanEntity) {
		ItemStack shopItem = shop.item.clone();

		shopItem.setAmount(Math.max(shop.quantity, 1));

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
		inv.setItem(2, AdminGUIItems.getAmountItem(transferRate));

		inv.setItem(4, shopItem);

		inv.setItem(6, GUIItems.getUserInfoItem(shop));
		inv.setItem(7, AdminGUIItems.getModifyItem());
		inv.setItem(8, AdminGUIItems.getViewAsCustomerItem());

		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;

		if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enforcepermission")) {
			if (!humanEntity.hasPermission("slabbo.link")) return;
		}

		boolean hasPendingLink = Slabbo.chestLinkUtil.hasPendingLink(humanEntity);
		boolean hasExistingLink = shop.linkedChestLocation != null;

		if (hasPendingLink && Slabbo.chestLinkUtil.pendingLinks.containsValue(shop.getLocationString())) {
			// Current shop's being linked
			inv.setItem(5, AdminGUIItems.getUnlinkChestItem());
		} else if (!hasPendingLink && hasExistingLink) {
			// Current shop isn't being linked, but it has one
			inv.setItem(5, AdminGUIItems.getUnlinkChestItem());
		} else if (hasPendingLink && !Slabbo.chestLinkUtil.pendingLinks.containsValue(shop.getLocationString())) {
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

		if (isBulk) {
			tempTransferRate = itemCount;
		}

		ItemStack shopItemClone = shop.item.clone();

		if (!shop.admin) {
			shop.stock += tempTransferRate;
		}

		player.playSound(shop.location, slabboSound.getSoundByKey("BUY_SELL_SUCCESS"), 1, 1);

		DataUtil.saveShops();

		shopItemClone.setAmount(tempTransferRate);

		pInv.removeItem(shopItemClone);

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
	}

	public void handleWithdraw (HumanEntity humanEntity, ClickType clickType) {
		boolean isBulk = clickType.equals(ClickType.SHIFT_LEFT);

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

		ItemStack shopItemClone = shop.item.clone();

		shopItemClone.setAmount(tempTransferRate);

		HashMap<Integer, ItemStack> leftovers = pInv.addItem(shopItemClone);

		int leftoverCount = leftovers
				.values()
				.stream()
				.map(stack -> stack.getAmount())
				.reduce(0, (total, el) -> total + el);

		if (!shop.admin) {
			shop.stock -= (tempTransferRate - leftoverCount);
		}

		player.playSound(shop.location, slabboSound.getSoundByKey("BUY_SELL_SUCCESS"), 1, 1);

		DataUtil.saveShops();

		inv.setItem(0, AdminGUIItems.getDepositItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
		inv.setItem(1, AdminGUIItems.getWithdrawItem(NameUtil.getName(shop.item), shop.stock, shop.admin));
	}

	public void handleChangeRate (HumanEntity humanEntity) {
		isChangingRate = true;
		waitingPlayerId = humanEntity.getUniqueId();

		humanEntity.sendMessage(Slabbo.localeManager.getString("general.general.type-new-rate"));
		((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("QUESTION"), 1, 1);

		humanEntity.closeInventory();
	}

	public void handleModify (HumanEntity humanEntity) {
		ShopCreationGUI gui = new ShopCreationGUI(shop.location, shop);

		((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("NAVIGATION"), 1, 1);

		gui.openInventory(humanEntity);
	}

	public void handleViewAsCustomer (HumanEntity humanEntity) {
		((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("NAVIGATION"), 1, 1);

		ShopUserGUI gui = new ShopUserGUI(shop, (Player)humanEntity);

		gui.openInventory(humanEntity);
	}

	public void handleChestLink (HumanEntity humanEntity) {
		if (!Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) return;
		if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enforcepermission") && !humanEntity.hasPermission("slabbo.link")) return;
		Player p = (Player)humanEntity;

		boolean hasPendingLink = Slabbo.chestLinkUtil.hasPendingLink(p);
		boolean hasExistingLink = shop.linkedChestLocation != null;

		if (hasPendingLink && Slabbo.chestLinkUtil.pendingLinks.containsValue(shop.getLocationString())) {
			// Current shop's being linked
			Slabbo.chestLinkUtil.pendingLinks.remove(p.getUniqueId());
			p.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("success-message.chestlink.linking-cancelled"));
			p.playSound(shop.location, slabboSound.getSoundByKey("CANCEL"), 1, 1);
			inv.setItem(5, AdminGUIItems.getLinkChestItem());
			return;
		} else if (!hasPendingLink && hasExistingLink) {
			// Current shop isn't being linked, but it has one
			Slabbo.chestLinkUtil.links.remove(shop.linkedChestLocation);

			Location blockLocation = ShopUtil.fromString(shop.linkedChestLocation);

			Block chestBlock = blockLocation.getBlock();

			ChestLinkUtil.setChestName(chestBlock, null);

			shop.linkedChestLocation = null;

			Slabbo.shopUtil.put(shop.getLocationString(), shop);

			p.sendMessage(ChatColor.GREEN+Slabbo.localeManager.getString("success-message.chestlink.linking-removed"));
			inv.setItem(5, AdminGUIItems.getLinkChestItem());

			p.playSound(shop.location, slabboSound.getSoundByKey("DESTROY"), 1, 1);

			DataUtil.saveShops();

			return;
		} else if (hasPendingLink && !Slabbo.chestLinkUtil.pendingLinks.containsValue(shop.getLocationString())) {
			// A link is in progress, but it's not the current shop
			Slabbo.chestLinkUtil.pendingLinks.remove(p.getUniqueId());
			p.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("success-message.chestlink.previous-linking-cancelled"));
			inv.setItem(5, AdminGUIItems.getLinkChestItem());
		}


		Slabbo.chestLinkUtil.pendingLinks.put(p.getUniqueId(), ShopUtil.locationToString(shop.location));

		p.sendMessage(Slabbo.localeManager.getString("general.chestlink.crouch-to-link"));
		p.closeInventory();

		p.playSound(shop.location, slabboSound.getSoundByKey("QUESTION"), 1, 1);
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

	@EventHandler
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
				e.getPlayer().sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.modify.not-a-valid-number"));
			}

			if (value < 0) { value = 0; }

			transferRate = value;

			new BukkitRunnable() {
				public void run () {
					e.getPlayer().playSound(shop.location, slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
					inv.setItem(2, AdminGUIItems.getAmountItem(transferRate));
					openInventory(e.getPlayer());
				}
			}.runTask(Slabbo.getInstance());
		}
	}
}
