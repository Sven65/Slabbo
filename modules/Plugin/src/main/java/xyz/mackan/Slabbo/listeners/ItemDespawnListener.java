package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.types.MetaKey;

public class ItemDespawnListener implements Listener {
	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	@EventHandler
	public void onDespawn (ItemDespawnEvent e) {
		Item entItem = e.getEntity();

		if (api.getNoDespawn(entItem)) {
			e.setCancelled(true);
			return;
		}
	}
}
