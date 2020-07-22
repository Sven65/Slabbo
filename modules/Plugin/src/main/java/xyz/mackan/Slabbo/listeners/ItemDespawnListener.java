package xyz.mackan.Slabbo.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.types.AttributeKey;

public class ItemDespawnListener implements Listener {
	@EventHandler
	public void onDespawn (ItemDespawnEvent e) {
		Item entItem = e.getEntity();

		ItemStack stack = entItem.getItemStack();

		if (!stack.hasItemMeta()) {
			return;
		}

		ItemMeta meta = stack.getItemMeta();

		PersistentDataContainer container = meta.getPersistentDataContainer();

		boolean hasKey = container.has(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER);

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}
