package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.mackan.Slabbo.GUI.ShopAdminGUI;
import xyz.mackan.Slabbo.GUI.ShopCreationGUI;
import xyz.mackan.Slabbo.GUI.ShopDeletionGUI;
import xyz.mackan.Slabbo.GUI.ShopUserGUI;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.manager.*;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.pluginsupport.PluginSupport;
import xyz.mackan.Slabbo.types.ShopAction;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.*;

import java.util.HashMap;

public class PlayerInteractListener implements Listener {
	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();
	SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	Material creationItem = Material.getMaterial(Slabbo.getInstance().getConfig().getString("shopCreationItem", "STICK"));

	public ShopAction getRightClickAction (ItemStack itemInHand, Block clickedBlock, Player player) {
		boolean holdingCreationItem = itemInHand != null && itemInHand.getType() == creationItem;

		String clickedLocation = ShopManager.locationToString(clickedBlock.getLocation());

		boolean shopExists = ShopManager.shops.containsKey(clickedLocation);
		Shop shop = ShopManager.shops.get(clickedLocation);

		boolean isShopOwner = false;

		boolean hasPermissionCreateShop = PermissionUtil.canCreateShop(player);
		PluginSupport.CanCreateShopResult canCreateShopResult = PluginSupport.canCreateShop(clickedBlock.getLocation(), player);

		if (!canCreateShopResult.canCreateShop) {
			if(canCreateShopResult.errorMessage != null) player.sendMessage(ChatColor.RED + canCreateShopResult.errorMessage);
			return new ShopAction(ShopAction.ShopActionType.NONE);
		}

		boolean canCreateShop = hasPermissionCreateShop && canCreateShopResult.canCreateShop;
		boolean canUseShop = PermissionUtil.canUseShop(player) && PluginSupport.canUseShop(clickedBlock.getLocation(), player);

		if (shopExists) {
			isShopOwner = shop.ownerId.equals(player.getUniqueId());
		}

		if (holdingCreationItem && !shopExists && canCreateShop) {
			int maxShops = PermissionUtil.getLimit(player);
			int userShops = ShopManager.getOwnerCount(player.getUniqueId());

			if (userShops >= maxShops) {
				return new ShopAction(ShopAction.ShopActionType.CREATION_LIMIT_HIT, maxShops);
			}

			return new ShopAction(ShopAction.ShopActionType.CREATE);
		}

		if (holdingCreationItem && shopExists && canUseShop) {
			if (isShopOwner) {
				return new ShopAction(ShopAction.ShopActionType.OPEN_DELETION_GUI, shop);
			} else {
				return new ShopAction(ShopAction.ShopActionType.OPEN_CLIENT_GUI, shop);
			}
		}

		if (!holdingCreationItem && shopExists && canUseShop) {
			if (isShopOwner) {
				return new ShopAction(ShopAction.ShopActionType.OPEN_ADMIN_GUI, shop);
			} else {
				return new ShopAction(ShopAction.ShopActionType.OPEN_CLIENT_GUI, shop);
			}
		}

		return new ShopAction(ShopAction.ShopActionType.NONE);
	}

	public ShopAction getRestockAction (ItemStack itemInHand, Player player, String clickedLocation) {
		if (itemInHand == null) return new ShopAction(ShopAction.ShopActionType.NONE);

		boolean shopExists = ShopManager.shops.containsKey(clickedLocation);

		Shop shop = ShopManager.shops.get(clickedLocation);

		boolean isShopOwner = false;

		if (shopExists) {
			isShopOwner = shop.ownerId.equals(player.getUniqueId());
		}

		if (!isShopOwner) return new ShopAction(ShopAction.ShopActionType.NONE);

		ItemStack clonedShopItem = shop.item.clone();
		ItemStack clonedHandItem = itemInHand.clone();

		clonedHandItem.setAmount(1);
		clonedShopItem.setAmount(1);

		boolean isShopItem = clonedShopItem.equals(clonedHandItem);

		if (!isShopItem) return new ShopAction(ShopAction.ShopActionType.NONE);

		return new ShopAction(ShopAction.ShopActionType.STOCK_SHOP, shop);
	}

	public ShopAction getLeftClickAction (ItemStack itemInHand, Block clickedBlock, Player player) {
		boolean isSneaking = player.isSneaking();

		String clickedLocation = ShopManager.locationToString(clickedBlock.getLocation());


		if (isSneaking) {
			Material clickedBlockMaterial = clickedBlock.getState().getType();

			boolean isChest = clickedBlockMaterial == Material.CHEST || clickedBlockMaterial == Material.TRAPPED_CHEST;

			if (!isChest) {
				if (!Slabbo.getInstance().getConfig().getBoolean("punchRestock.bulk", true)) return new ShopAction(ShopAction.ShopActionType.NONE);

				ShopAction action = getRestockAction(itemInHand, player, clickedLocation);

				if (action.type == ShopAction.ShopActionType.STOCK_SHOP) {
					return new ShopAction(ShopAction.ShopActionType.BULK_RESTOCK_SHOP, action.extra);
				}

				return new ShopAction(ShopAction.ShopActionType.NONE);
			}

			if (!ChestLinkManager.hasPendingLink(player)) return new ShopAction(ShopAction.ShopActionType.NONE);

			PluginSupport.CanCreateShopResult canCreateShopResult = PluginSupport.canCreateShop(clickedBlock.getLocation(), player);

			if (!canCreateShopResult.canCreateShop) {
				if(canCreateShopResult.errorMessage != null) player.sendMessage(ChatColor.RED + canCreateShopResult.errorMessage);
				return new ShopAction(ShopAction.ShopActionType.NONE);
			}

			return new ShopAction(ShopAction.ShopActionType.LINK_CHEST);
		}

		if (!Slabbo.getInstance().getConfig().getBoolean("punchRestock.single", true)) return new ShopAction(ShopAction.ShopActionType.NONE);

		ShopAction action = getRestockAction(itemInHand, player, clickedLocation);

		return action;
	}

	public void linkChest (Player player, Block clickedBlock) {
		String linkingShopLocation = ChestLinkManager.pendingLinks.get(player.getUniqueId());
		String linkingChestLocation = ShopManager.locationToString(clickedBlock.getLocation());

		boolean isLinked = ChestLinkManager.isChestLinked(clickedBlock);

		if (isLinked) {
			player.sendMessage(ChatColor.DARK_RED + LocaleManager.getString("error-message.chestlink.already-linked"));
			ChestLinkManager.pendingLinks.remove(player.getUniqueId());

			player.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		Shop linkingShop = ShopManager.shops.get(linkingShopLocation);

		linkingShop.linkedChestLocation = linkingChestLocation;

		ShopManager.put(linkingShopLocation, linkingShop);

		ChestLinkManager.pendingLinks.remove(player.getUniqueId());

		ChestLinkManager.links.put(linkingChestLocation, linkingShop);

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("location", linkingShopLocation);

		player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.chestlink.link-success", replacementMap));

		ChestLinkManager.setChestName(clickedBlock, "Slabbo "+LocaleManager.replaceKey("general.chestlink.chest-name", replacementMap));

		player.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("SUCCESS"), 1, 1);

		DataUtil.saveShops();
	}

	public void stockShop (Player player, ItemStack itemInHand, Shop shop) {
		PlayerInventory pInv = player.getInventory();

		pInv.removeItem(itemInHand);

		shop.stock += itemInHand.getAmount();

		ShopManager.put(shop.getLocationString(), shop);

		DataUtil.saveShops();
	}

	public void bulkStockShop (Player player, ItemStack itemInHand, Shop shop) {
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

		ItemStack clonedItem = itemInHand.clone();

		clonedItem.setAmount(itemCount);

		pInv.removeItem(clonedItem);

		shop.stock += itemCount;

		ShopManager.put(shop.getLocationString(), shop);

		DataUtil.saveShops();
	}

	public void handleLeftClick (PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block clickedBlock = e.getClickedBlock();
		ItemStack itemInHand = api.getInteractionItemInHand(e);

		ShopAction action = getLeftClickAction(itemInHand, clickedBlock, player);

		if (action.type == ShopAction.ShopActionType.NONE) return;

		e.setCancelled(true);

		switch (action.type) {
			case LINK_CHEST:
				linkChest(player, clickedBlock);
				break;
			case STOCK_SHOP:
				stockShop(player, itemInHand, (Shop) action.extra);
				break;
			case BULK_RESTOCK_SHOP:
				bulkStockShop(player, itemInHand, (Shop) action.extra);
				break;
		}
	}

	/**
	 * Prevents the shop from being waterlogged
	 * @param e The event to prevent waterlogging with
	 */
	void preventWaterLogging(PlayerInteractEvent e) {
		Block clickedBlock = e.getClickedBlock();

		if (clickedBlock == null) return;

		ItemStack offhand = api.getInteractionItemInHand(e);
		ItemStack mainHand = api.getInteractionItemInOffHand(e);

		String clickedLocation = ShopManager.locationToString(clickedBlock.getLocation());

		boolean shopExists = ShopManager.shops.containsKey(clickedLocation);
		Shop shop = ShopManager.shops.get(clickedLocation);

		if (!shopExists || shop == null) return;

		if (offhand != null && offhand.getType() == Material.WATER_BUCKET || mainHand != null && mainHand.getType() == Material.WATER_BUCKET) e.setCancelled(true);
	}

	@EventHandler
	public void onInteract (PlayerInteractEvent e) {
		preventWaterLogging(e);

		ItemStack itemInHand = api.getInteractionItemInHand(e);

		Player player = e.getPlayer();

		Action action = e.getAction();

		if (api.isInteractionOffHand(e)) return;

		Block clickedBlock = e.getClickedBlock();

		if (action != Action.RIGHT_CLICK_BLOCK) {
			if (action == Action.LEFT_CLICK_BLOCK) {
				handleLeftClick(e);
			}
			return;
		}

		if (!Misc.isValidShopBlock(clickedBlock)) return;

		ShopAction pAction = getRightClickAction(itemInHand, clickedBlock, player);

		switch (pAction.type) {
			case CREATION_LIMIT_HIT: {
				int limit = (Integer) pAction.extra;

				HashMap<String, Object> replacementMap = new HashMap<String, Object>();
				replacementMap.put("limit", limit);

				player.sendMessage(ChatColor.RED + LocaleManager.replaceKey("error-message.general.limit-hit", replacementMap));

				player.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			}
				break;
			case CREATE: {
				ShopCreationGUI gui = new ShopCreationGUI(clickedBlock.getLocation());
				gui.openInventory(e.getPlayer());
			}
				break;
			case OPEN_DELETION_GUI: {
				Shop shop = (Shop) pAction.extra;

				ShopDeletionGUI gui = new ShopDeletionGUI(shop);
				gui.openInventory(e.getPlayer());
			}
				break;
			case OPEN_CLIENT_GUI: {
				Shop shop = (Shop) pAction.extra;

				if (shop.shouldRestock()) {
					shop.doLimitRestock();
					DataUtil.saveShops();
				}

				ShopUserGUI gui = new ShopUserGUI(shop, player);
				gui.openInventory(e.getPlayer());
			}
				break;
			case OPEN_ADMIN_GUI: {
				Shop shop = (Shop) pAction.extra;

				if (shop.shouldRestock()) {
					shop.doLimitRestock();
					DataUtil.saveShops();
				}

				ShopAdminGUI gui = new ShopAdminGUI(shop, player);
				gui.openInventory(e.getPlayer());
			}
				break;
		}
	}
}
