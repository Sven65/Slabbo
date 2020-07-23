package xyz.mackan.Slabbo.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import xyz.mackan.Slabbo.types.MetaKey;

// For 1.8
public class PlayerPickupItemListener implements Listener {
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		Item entItem = e.getItem();

		boolean hasKey = entItem.hasMetadata(MetaKey.NO_PICKUP.getKey());

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}
