package xyz.mackan.Slabbo.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.types.AttributeKey;

public class ItemMergeListener implements Listener {
	@EventHandler
	public void onMerge (ItemMergeEvent e) {
		Item source = e.getEntity();

		ItemStack stack = source.getItemStack();

		if (!stack.hasItemMeta()) {
			return;
		}

		boolean hasKey = source.hasMetadata(AttributeKey.NO_MERGE.getKey());

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}

