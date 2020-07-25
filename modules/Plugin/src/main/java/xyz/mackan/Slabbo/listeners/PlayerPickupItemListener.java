package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.types.MetaKey;

// For 1.8
public class PlayerPickupItemListener implements Listener {
	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	@EventHandler
	public void onPickup(PlayerPickupItemEvent e) {
		Item entItem = e.getItem();

		boolean hasKey = api.getNoPickup(entItem);

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}
