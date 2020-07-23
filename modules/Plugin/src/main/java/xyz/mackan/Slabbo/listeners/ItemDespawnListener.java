package xyz.mackan.Slabbo.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import xyz.mackan.Slabbo.types.MetaKey;

public class ItemDespawnListener implements Listener {
	@EventHandler
	public void onDespawn (ItemDespawnEvent e) {
		Item entItem = e.getEntity();

//		ItemStack stack = entItem.getItemStack();

//		if (!stack.hasItemMeta()) {
//			return;
//		}

		boolean hasKey = entItem.hasMetadata(MetaKey.NO_DESPAWN.getKey());

		if (hasKey) {
			e.setCancelled(true);
			return;
		}
	}
}
