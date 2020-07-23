package xyz.mackan.Slabbo.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.types.MetaKey;

public class ItemMergeListener implements Listener {
	@EventHandler
	public void onMerge (ItemMergeEvent e) {
		Item source = e.getEntity();

		ItemStack stack = source.getItemStack();

		if (!stack.hasItemMeta()) {
			return;
		}

		boolean hasKey = source.hasMetadata(MetaKey.NO_MERGE.getKey());

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}

