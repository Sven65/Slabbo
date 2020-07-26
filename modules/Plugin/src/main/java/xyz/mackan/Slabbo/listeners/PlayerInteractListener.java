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
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.pluginsupport.GriefPreventionSupport;
import xyz.mackan.Slabbo.pluginsupport.PluginSupport;
import xyz.mackan.Slabbo.pluginsupport.WorldguardSupport;
import xyz.mackan.Slabbo.types.ShopAction;
import xyz.mackan.Slabbo.types.ShopActionType;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.*;

import java.util.HashMap;

public class PlayerInteractListener implements Listener {
	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();
	SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();


	public ShopAction getRightClickAction (ItemStack itemInHand, Block clickedBlock, Player player) {
		boolean holdingStick = itemInHand != null && itemInHand.getType() == Material.STICK;

		String clickedLocation = ShopUtil.locationToString(clickedBlock.getLocation());

		boolean shopExists = Slabbo.shopUtil.shops.containsKey(clickedLocation);
		Shop shop = Slabbo.shopUtil.shops.get(clickedLocation);

		boolean isShopOwner = false;

		boolean canCreateShop = PermissionUtil.canCreateShop(player) && PluginSupport.canCreateShop(clickedBlock.getLocation(), player);
		boolean canUseShop = PermissionUtil.canUseShop(player) && PluginSupport.canUseShop(clickedBlock.getLocation(), player);

		if (shopExists) {
			isShopOwner = shop.ownerId.equals(player.getUniqueId());
		}

		if (holdingStick && !shopExists && canCreateShop) {
			int maxShops = PermissionUtil.getLimit(player);
			int userShops = Slabbo.shopUtil.getOwnerCount(player.getUniqueId());

			if (userShops >= maxShops) {
				return new ShopAction(ShopActionType.CREATION_LIMIT_HIT, maxShops);
			}

			return new ShopAction(ShopActionType.CREATE);
		}

		if (holdingStick && shopExists && canUseShop) {
			if (isShopOwner) {
				return new ShopAction(ShopActionType.OPEN_DELETION_GUI, shop);
			} else {
				return new ShopAction(ShopActionType.OPEN_CLIENT_GUI, shop);
			}
		}

		if (!holdingStick && shopExists && canUseShop) {
			if (isShopOwner) {
				return new ShopAction(ShopActionType.OPEN_ADMIN_GUI, shop);
			} else {
				return new ShopAction(ShopActionType.OPEN_CLIENT_GUI, shop);
			}
		}

		return new ShopAction(ShopActionType.NONE);
	}

	public ShopAction getRestockAction (ItemStack itemInHand, Player player, String clickedLocation) {
		if (itemInHand == null) return new ShopAction(ShopActionType.NONE);

		boolean shopExists = Slabbo.shopUtil.shops.containsKey(clickedLocation);

		Shop shop = Slabbo.shopUtil.shops.get(clickedLocation);

		boolean isShopOwner = false;

		if (shopExists) {
			isShopOwner = shop.ownerId.equals(player.getUniqueId());
		}

		if (!isShopOwner) return new ShopAction(ShopActionType.NONE);

		ItemStack clonedShopItem = shop.item.clone();
		ItemStack clonedHandItem = itemInHand.clone();

		clonedHandItem.setAmount(1);
		clonedShopItem.setAmount(1);

		boolean isShopItem = clonedShopItem.equals(clonedHandItem);

		if (!isShopItem) return new ShopAction(ShopActionType.NONE);

		return new ShopAction(ShopActionType.STOCK_SHOP, shop);
	}

	public ShopAction getLeftClickAction (ItemStack itemInHand, Block clickedBlock, Player player) {
		boolean isSneaking = player.isSneaking();

		String clickedLocation = ShopUtil.locationToString(clickedBlock.getLocation());


		if (isSneaking) {
			Material clickedBlockMaterial = clickedBlock.getState().getType();

			boolean isChest = clickedBlockMaterial == Material.CHEST || clickedBlockMaterial == Material.TRAPPED_CHEST;

			if (!isChest) {
				ShopAction action = getRestockAction(itemInHand, player, clickedLocation);

				if (action.type == ShopActionType.STOCK_SHOP) {
					return new ShopAction(ShopActionType.BULK_RESTOCK_SHOP, action.extra);
				}

				return new ShopAction(ShopActionType.NONE);
			}

			if (!Slabbo.chestLinkUtil.hasPendingLink(player)) return new ShopAction(ShopActionType.NONE);

			boolean canCreateShop = PluginSupport.canCreateShop(clickedBlock.getLocation(), player);

			if (!canCreateShop) return new ShopAction(ShopActionType.NONE);

			return new ShopAction(ShopActionType.LINK_CHEST);
		}

		ShopAction action = getRestockAction(itemInHand, player, clickedLocation);

		return action;
	}

	public void linkChest (Player player, Block clickedBlock) {
		String linkingShopLocation = Slabbo.chestLinkUtil.pendingLinks.get(player.getUniqueId());
		String linkingChestLocation = ShopUtil.locationToString(clickedBlock.getLocation());

		boolean isLinked = Slabbo.chestLinkUtil.isChestLinked(clickedBlock);

		if (isLinked) {
			player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.chestlink.already-linked"));
			Slabbo.chestLinkUtil.pendingLinks.remove(player.getUniqueId());

			player.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		Shop linkingShop = Slabbo.shopUtil.shops.get(linkingShopLocation);

		linkingShop.linkedChestLocation = linkingChestLocation;

		Slabbo.shopUtil.put(linkingShopLocation, linkingShop);

		Slabbo.chestLinkUtil.pendingLinks.remove(player.getUniqueId());

		Slabbo.chestLinkUtil.links.put(linkingChestLocation, linkingShop);

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("location", linkingShopLocation);

		player.sendMessage(ChatColor.GREEN+Slabbo.localeManager.replaceKey("success-message.chestlink.link-success", replacementMap));

		ChestLinkUtil.setChestName(clickedBlock, "Slabbo "+Slabbo.localeManager.replaceKey("general.chestlink.chest-name", replacementMap));

		player.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("SUCCESS"), 1, 1);

		DataUtil.saveShops();
	}

	public void stockShop (Player player, ItemStack itemInHand, Shop shop) {
		PlayerInventory pInv = player.getInventory();

		pInv.removeItem(itemInHand);

		shop.stock += itemInHand.getAmount();

		Slabbo.shopUtil.put(shop.getLocationString(), shop);

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

		Slabbo.shopUtil.put(shop.getLocationString(), shop);

		DataUtil.saveShops();
	}

	public void handleLeftClick (PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Block clickedBlock = e.getClickedBlock();
		ItemStack itemInHand = api.getInteractionItemInHand(e);

		ShopAction action = getLeftClickAction(itemInHand, clickedBlock, player);

		if (action.type == ShopActionType.NONE) return;

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

	@EventHandler
	public void onInteract (PlayerInteractEvent e) {
		ItemStack itemInHand = api.getInteractionItemInHand(e);

		Player player = e.getPlayer();

		Action action = e.getAction();

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

				player.sendMessage(ChatColor.RED + Slabbo.localeManager.replaceKey("error-message.general.limit-hit", replacementMap));

				player.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
				break;
			}
			case CREATE: {
				ShopCreationGUI gui = new ShopCreationGUI(clickedBlock.getLocation());
				gui.openInventory(e.getPlayer());
				break;
			}
			case OPEN_DELETION_GUI: {
				Shop shop = (Shop) pAction.extra;

				ShopDeletionGUI gui = new ShopDeletionGUI(shop);
				gui.openInventory(e.getPlayer());
				break;
			}
			case OPEN_CLIENT_GUI: {
				Shop shop = (Shop) pAction.extra;

				ShopUserGUI gui = new ShopUserGUI(shop, player);
				gui.openInventory(e.getPlayer());
				break;
			}
			case OPEN_ADMIN_GUI: {
				Shop shop = (Shop) pAction.extra;

				ShopAdminGUI gui = new ShopAdminGUI(shop, player);
				gui.openInventory(e.getPlayer());
				break;
			}
		}
	}
}
