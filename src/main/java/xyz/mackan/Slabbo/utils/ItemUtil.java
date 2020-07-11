package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.types.AttributeKey;

public class ItemUtil {

	public static void dropItem (Location location, ItemStack item) {
		ItemMeta meta = item.getItemMeta();

		meta.getPersistentDataContainer().set(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER, 1);

		item.setItemMeta(meta);

		Item itemEnt = location.getWorld().dropItem(location, item);
	}
}
