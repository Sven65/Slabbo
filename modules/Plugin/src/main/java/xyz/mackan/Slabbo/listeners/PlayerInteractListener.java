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
import xyz.mackan.Slabbo.utils.ChestLinkUtil;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.PermissionUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.HashMap;

public class PlayerInteractListener implements Listener {
	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();

	public ShopAction getAction (ItemStack itemInHand, Block clickedBlock, Player player) {
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

	public void handleLeftClick (PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (!p.isSneaking()) return;

		if (!Slabbo.chestLinkUtil.hasPendingLink(p)) return;

		Block clickedBlock = e.getClickedBlock();

		Material clickedBlockMaterial = clickedBlock.getBlockData().getMaterial();

		if (clickedBlockMaterial != Material.CHEST && clickedBlockMaterial != Material.TRAPPED_CHEST) return;

		e.setCancelled(true);

		boolean canCreateShop = true;

		if (Slabbo.enabledPlugins.worldguard) {
			canCreateShop = WorldguardSupport.canCreateShop(clickedBlock.getLocation(), p);
		}

		if (Slabbo.enabledPlugins.griefprevention) {
			canCreateShop = GriefPreventionSupport.canCreateShop(clickedBlock.getLocation(), p);
		}

		if (!canCreateShop) {
			return;
		}

		String linkingShopLocation = Slabbo.chestLinkUtil.pendingLinks.get(p.getUniqueId());
		String linkingChestLocation = ShopUtil.locationToString(clickedBlock.getLocation());

		boolean isLinked = Slabbo.chestLinkUtil.isChestLinked(clickedBlock);

		if (isLinked) {
			p.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.chestlink.already-linked"));
			Slabbo.chestLinkUtil.pendingLinks.remove(p.getUniqueId());

			p.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
			return;
		}

		Shop linkingShop = Slabbo.shopUtil.shops.get(linkingShopLocation);

		linkingShop.linkedChestLocation = linkingChestLocation;

		Slabbo.shopUtil.put(linkingShopLocation, linkingShop);

		Slabbo.chestLinkUtil.pendingLinks.remove(p.getUniqueId());

		Slabbo.chestLinkUtil.links.put(linkingChestLocation, linkingShop);

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("location", linkingShopLocation);

		p.sendMessage(ChatColor.GREEN+Slabbo.localeManager.replaceKey("success-message.chestlink.link-success", replacementMap));

		ChestLinkUtil.setChestName(clickedBlock, "Slabbo "+Slabbo.localeManager.replaceKey("general.chestlink.chest-name", replacementMap));

		p.playSound(clickedBlock.getLocation(), slabboSound.getSoundByKey("SUCCESS"), 1, 1);

		DataUtil.saveShops();

		//p.isSneaking();
	}

	@EventHandler
	public void onInteract (PlayerInteractEvent e) {
		SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

		ItemStack itemInHand = api.getInteractionItemInHand(e);

		if (itemInHand == null) return;

		Player player = e.getPlayer();

		Action action = e.getAction();

		Block clickedBlock = e.getClickedBlock();

		if (action != Action.RIGHT_CLICK_BLOCK) {
			if (action == Action.LEFT_CLICK_BLOCK) {
				handleLeftClick(e);
			}
			return;
		}

		boolean isSlab = api.isSlab(clickedBlock);

		if (!isSlab) return;

		ShopAction pAction = getAction(itemInHand, clickedBlock, player);

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
