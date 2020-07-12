package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.types.AttributeKey;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ItemUtil {

	public static void dropItem (Location location, ItemStack item, UUID uuid) {
		ItemStack clonedItem = item.clone();
		ItemMeta meta = clonedItem.getItemMeta();

		clonedItem.setAmount(64);

		meta.getPersistentDataContainer().set(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.NO_MERGE.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.UNIQUE_KEY.getKey(), PersistentDataType.STRING, uuid.toString());

		clonedItem.setItemMeta(meta);

		Item itemEnt = location.getWorld().dropItem(location, clonedItem);

		itemEnt.setVelocity(itemEnt.getVelocity().zero());
	}

	public static Item findItemEntity (Location location) {
		Collection<Entity> nearbyEntites = location.getWorld().getNearbyEntities(location, 0.5, 2, 0.5);

		Item returnItem = null;

		for (Entity entity : nearbyEntites) {
			boolean isItem = (entity instanceof Item) || (entity instanceof CraftItem);

			if (!isItem) continue;

			Item item = (Item) entity;

			ItemStack itemStack = item.getItemStack();

			if (!itemStack.hasItemMeta()) continue;

			ItemMeta meta = itemStack.getItemMeta();

			PersistentDataContainer container = meta.getPersistentDataContainer();

			boolean hasKey = container.has(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER);

			if (hasKey) {
				returnItem = item;
				break;
			}
		}

		return returnItem;
	}
}
