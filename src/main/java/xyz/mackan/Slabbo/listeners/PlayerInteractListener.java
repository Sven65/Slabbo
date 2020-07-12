package xyz.mackan.Slabbo.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.GUI.ShopAdminGUI;
import xyz.mackan.Slabbo.GUI.ShopCreationGUI;
import xyz.mackan.Slabbo.GUI.ShopDeletionGUI;
import xyz.mackan.Slabbo.GUI.ShopUserGUI;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.ShopAction;
import xyz.mackan.Slabbo.types.ShopActionType;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.PermissionUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

public class PlayerInteractListener implements Listener {
	public ShopAction getAction (ItemStack itemInHand, Block clickedBlock, Player player) {
		boolean holdingStick = itemInHand != null && itemInHand.getType() == Material.STICK;

		String clickedLocation = ShopUtil.locationToString(clickedBlock.getLocation());

		boolean shopExists = Slabbo.shopUtil.shops.containsKey(clickedLocation);
		Shop shop = Slabbo.shopUtil.shops.get(clickedLocation);

		boolean isShopOwner = false;

		if (shopExists) {
			isShopOwner = shop.ownerId.equals(player.getUniqueId());
		}

		if (holdingStick && !shopExists) {
			int maxShops = PermissionUtil.getLimit(player);
			int userShops = Slabbo.shopUtil.getOwnerCount(player.getUniqueId());

			if (userShops >= maxShops) {
				return new ShopAction(ShopActionType.CREATION_LIMIT_HIT, maxShops);
			}

			return new ShopAction(ShopActionType.CREATE);
		}

		if (holdingStick && shopExists) {
			if (isShopOwner) {
				return new ShopAction(ShopActionType.OPEN_DELETION_GUI, shop);
			} else {
				return new ShopAction(ShopActionType.OPEN_CLIENT_GUI, shop);
			}
		}

		if (!holdingStick && shopExists) {
			if (isShopOwner) {
				return new ShopAction(ShopActionType.OPEN_ADMIN_GUI, shop);
			} else {
				return new ShopAction(ShopActionType.OPEN_CLIENT_GUI, shop);
			}
		}

		return new ShopAction(ShopActionType.NONE);
	}

	@EventHandler
	public void onInteract (PlayerInteractEvent e) {
		EquipmentSlot hand = e.getHand();

		if (hand == null || hand != EquipmentSlot.HAND) return;

		ItemStack itemInHand = e.getItem();
		Player player = e.getPlayer();

		Action action = e.getAction();

		Block clickedBlock = e.getClickedBlock();

		if (action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		BlockData blockData = clickedBlock.getBlockData();

		boolean isSlab = (blockData instanceof Slab);

		if (!isSlab) return;

		ShopAction pAction = getAction(itemInHand, clickedBlock, player);

		switch (pAction.type) {
			case CREATION_LIMIT_HIT: {
				int limit = (Integer) pAction.extra;
				player.sendMessage(ChatColor.RED + "You've created all the shops you can! (" + limit + ")");
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

				ShopAdminGUI gui = new ShopAdminGUI(shop);
				gui.openInventory(e.getPlayer());
				break;
			}
		}
	}
}
