package xyz.mackan.Slabbo.commands;

import co.aikar.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.types.Shop;

import java.util.Set;

public class Conditions {
	static ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();

	public static Shop getLookingAtShop (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		String locationString = ShopManager.locationToString(lookingAt.getLocation());

		if (ShopManager.shops.containsKey(locationString)) {
			return ShopManager.shops.get(locationString);
		}

		player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
		throw new ConditionFailedException(LocaleManager.getString("error-message.general.not-a-shop"));
	}

	/**
	 * Gets the linked chest the player is looking at
	 * @param player The player
	 * @return The linked chest the user is looking at, if any
	 */
	public static Block getLookingAtLinkedChest (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		if (ChestLinkManager.isChestLinked(lookingAt)) {
			return lookingAt;
		}

		player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
		throw new ConditionFailedException(LocaleManager.getString("error-message.general.not-a-linked-chest"));
	}

	public static boolean isPlayer (CommandIssuer issuer) {
		if(!issuer.isPlayer()) {
			throw new ConditionFailedException("Only players can execute this.");
		}

		return true;
	}

	public static boolean isShopOwner (Shop shop, Player player) {
		return shop.ownerId.equals(player.getUniqueId());
	}

	public static void registerConditions (PaperCommandManager manager) {
		manager.getCommandConditions().addCondition("lookingAtShop", context -> {
			BukkitCommandIssuer issuer = context.getIssuer();

			isPlayer(issuer);

			Player player = issuer.getPlayer();

			getLookingAtShop(player);
		});

		manager.getCommandConditions().addCondition("lookingAtLinkedChest", context -> {
			BukkitCommandIssuer issuer = context.getIssuer();

			isPlayer(issuer);

			Player player = issuer.getPlayer();

			getLookingAtLinkedChest(player);
		});

		manager.getCommandConditions().addCondition("canExecuteOnShop", context -> {
			BukkitCommandIssuer issuer = context.getIssuer();

			isPlayer(issuer);

			Player player = issuer.getPlayer();

			String permissionNode = context.getConfigValue("othersPerm", "");

			Shop shop = getLookingAtShop(player);

			if (!isShopOwner(shop, player)) {
				if (!player.hasPermission(permissionNode)) {
					player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
					throw new ConditionFailedException(LocaleManager.getString("error-message.general.not-shop-owner"));
				}
			}
		});

		manager.getCommandConditions().addCondition("canExecuteOnLinkedChest", context -> {
			BukkitCommandIssuer issuer = context.getIssuer();

			isPlayer(issuer);

			Player player = issuer.getPlayer();

			String permissionNode = context.getConfigValue("othersPerm", "");

			Block lookingAt = getLookingAtLinkedChest(player);

			Shop shop = ChestLinkManager.getShopByChestLocation(lookingAt.getLocation());

			if (!isShopOwner(shop, player)) {
				if (!player.hasPermission(permissionNode)) {
					player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
					throw new ConditionFailedException(LocaleManager.getString("error-message.general.not-linked-chest-owner"));
				}
			}
		});

		manager.getCommandConditions().addCondition("isAdminShop", context -> {
			BukkitCommandIssuer issuer = context.getIssuer();

			isPlayer(issuer);

			Player player = issuer.getPlayer();

			Shop shop = getLookingAtShop(player);

			if (!shop.admin) {
				throw new ConditionFailedException(LocaleManager.getString("error-message.general.not-admin-shop"));
			}
		});
	}
}
