package xyz.mackan.Slabbo.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.NameUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin (PlayerJoinEvent e) {
		Player player = e.getPlayer();
		UUID userId = player.getUniqueId();

		if(!Slabbo.shopUtil.shopsByOwnerId.containsKey(userId)) return;

		List<Shop> shops = Slabbo.shopUtil.shopsByOwnerId.get(userId);

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		for (Shop shop : shops) {
			if (shop.admin) continue;

			if (shop.stock == 0) {
				replacementMap.put("location", ShopUtil.locationToString(shop.location));
				replacementMap.put("item", NameUtil.getName(shop.item));


				player.sendMessage(Slabbo.localeManager.replaceKey("general.general.restock-message", replacementMap));
			}
		}

		if (player.hasPermission("slabbo.notifyupdate")) {
			if (Slabbo.hasUpdate) {
				player.sendMessage(ChatColor.LIGHT_PURPLE + "There's a new Slabbo update available!");
			}
		}
	}
}
