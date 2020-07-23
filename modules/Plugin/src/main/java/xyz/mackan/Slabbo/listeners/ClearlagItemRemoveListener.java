package xyz.mackan.Slabbo.listeners;

import me.minebuilders.clearlag.events.EntityRemoveEvent;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.types.AttributeKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClearlagItemRemoveListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDespawn (EntityRemoveEvent e) {
		List<Entity> items = e.getEntityList();
		ArrayList<Entity> entitiesToKeep = new ArrayList<Entity>();

		Iterator<Entity> it = items.iterator();

		while (it.hasNext()) {
			Entity itemEntity = it.next();

			boolean isItem = (itemEntity instanceof Item) || (itemEntity instanceof CraftItem);

			if (!isItem) continue;

			Item item = (Item) itemEntity;

			ItemStack itemStack = item.getItemStack();

			if (!item.hasMetadata(AttributeKey.NO_DESPAWN.getKey())) continue;

//			boolean noDespawn = itemEntity.hasMetadata(AttributeKey.NO_DESPAWN.getKey());
//
//			if (noDespawn) {
				entitiesToKeep.add(itemEntity);
//			}
		}


		Iterator<Entity> keepIterator = entitiesToKeep.iterator();

		while (keepIterator.hasNext()) {
			e.removeEntity(keepIterator.next());
		}
	}
}
