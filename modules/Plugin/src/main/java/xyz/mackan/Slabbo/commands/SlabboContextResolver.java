package xyz.mackan.Slabbo.commands;

import co.aikar.commands.*;
import co.aikar.commands.contexts.ContextResolver;
import co.aikar.commands.contexts.IssuerOnlyContextResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.types.Shop;

import java.util.Arrays;
import java.util.Set;

public class SlabboContextResolver {
	static ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();

	public Shop shop;

	SlabboContextResolver (Shop shop) {
		this.shop = shop;
	}

	public static Shop getLookingAtShop (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		String locationString = ShopManager.locationToString(lookingAt.getLocation());

		if (ShopManager.shops.containsKey(locationString)) {
			return ShopManager.shops.get(locationString);
		}

		return null;
	}

	public static IssuerOnlyContextResolver<SlabboContextResolver, BukkitCommandExecutionContext> getContextResolver () {
		return (c) -> {
			Player player = c.getPlayer();

			Shop shop = getLookingAtShop(player);

			if (shop == null) {
				//player.sendMessage(ChatColor.RED+ LocaleManager.getString("error-message.general.not-a-shop"));
				player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);

				throw new ConditionFailedException(LocaleManager.getString("error-message.general.not-a-shop"));
			}

			return new SlabboContextResolver(getLookingAtShop(player));
		};
	}
}
