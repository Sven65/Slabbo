package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;

public class InventoryPickupItemListener implements Listener {
	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	@EventHandler
	public void onPickupItem (InventoryPickupItemEvent e) {
		Item item = e.getItem();

		if (api.isSlabboItem(item)) {
			e.setCancelled(true);
			return;
		}
	}
}
