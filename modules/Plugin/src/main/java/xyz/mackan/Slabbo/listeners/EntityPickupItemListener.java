package xyz.mackan.Slabbo.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.w3c.dom.Attr;
import xyz.mackan.Slabbo.types.AttributeKey;

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

		System.out.println("NO PICKUP: "+entItem.getMetadata(AttributeKey.NO_PICKUP.getKey()));

		boolean hasKey = entItem.hasMetadata(AttributeKey.NO_PICKUP.getKey());

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}
