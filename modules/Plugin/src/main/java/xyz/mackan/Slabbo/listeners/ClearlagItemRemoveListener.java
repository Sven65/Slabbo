package xyz.mackan.Slabbo.listeners;

import me.minebuilders.clearlag.events.EntityRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.types.MetaKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClearlagItemRemoveListener implements Listener {
	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDespawn (EntityRemoveEvent e) {
		List<Entity> items = e.getEntityList();
		ArrayList<Entity> entitiesToKeep = new ArrayList<Entity>();

		Iterator<Entity> it = items.iterator();

		while (it.hasNext()) {
			Entity itemEntity = it.next();

			boolean isItem = api.isItem(itemEntity);

			if (!isItem) continue;

			Item item = (Item) itemEntity;

			boolean noDespawn = api.getNoDespawn(item);

			if (!noDespawn) continue;

			entitiesToKeep.add(itemEntity);
		}


		Iterator<Entity> keepIterator = entitiesToKeep.iterator();

		while (keepIterator.hasNext()) {
			e.removeEntity(keepIterator.next());
		}
	}
}
