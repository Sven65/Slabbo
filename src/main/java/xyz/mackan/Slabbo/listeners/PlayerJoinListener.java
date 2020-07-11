package xyz.mackan.Slabbo.listeners;

import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.NameUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
	@EventHandler
	public void onJoin (PlayerJoinEvent e) {
		Player player = e.getPlayer();
		UUID userId = player.getUniqueId();

		if(!Slabbo.shopUtil.shopsByOwnerId.containsKey(userId)) return;

		List<Shop> shops = Slabbo.shopUtil.shopsByOwnerId.get(userId);

		for (Shop shop : shops) {
			if (shop.stock == 0) {
				player.sendMessage("Your shop at " + ShopUtil.locationToString(shop.location) + " is out of "+ NameUtil.getName(shop.item));
			}
		}
	}
}

