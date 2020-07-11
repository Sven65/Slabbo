package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.types.AttributeKey;

import java.util.UUID;

public class ItemUtil {

	public static void dropItem (Location location, ItemStack item) {
		ItemStack clonedItem = item.clone();
		ItemMeta meta = clonedItem.getItemMeta();

		clonedItem.setAmount(64);

		meta.getPersistentDataContainer().set(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.UNIQUE_KEY.getKey(), PersistentDataType.STRING, UUID.randomUUID().toString());

		clonedItem.setItemMeta(meta);

		Item itemEnt = location.getWorld().dropItem(location, clonedItem);
	}
}
