package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.types.MetaKey;

public class EntityPickupItemListener implements Listener {
	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	@EventHandler
	public void onPickup(EntityPickupItemEvent e) {
		Item entItem = e.getItem();

		boolean noPickup = api.getNoPickup(entItem);

		if (noPickup) {
			e.setCancelled(true);
			return;
		}
	}
}
