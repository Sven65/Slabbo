package xyz.mackan.Slabbo.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.types.MetaKey;

public class EntityPickupItemListener implements Listener {
	@EventHandler
	public void onPickup(EntityPickupItemEvent e) {
		Item entItem = e.getItem();

		ItemStack stack = entItem.getItemStack();

		if (!stack.hasItemMeta()) {
			return;
		}

//		ItemMeta meta = stack.getItemMeta();
//
//		PersistentDataContainer container = meta.getPersistentDataContainer();
//
//		boolean hasKey = container.has(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER);
		boolean hasKey = entItem.hasMetadata(MetaKey.NO_PICKUP.getKey());

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}
