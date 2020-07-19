package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.AttributeKey;

import java.util.Collection;

public class ItemUtil {

	public static double getSlabYOffset (Location location) {
		Block block = location.getBlock();

		if (block == null) return 0;

		BlockData blockData = block.getBlockData();

		boolean isSlab = (blockData instanceof Slab);

		if (!isSlab) return 0;

		Slab slab = (Slab) blockData;

		Slab.Type slabType = slab.getType();

		if (slabType == Slab.Type.BOTTOM) {
			return 0.5;
		} else {
			return 1.0;
		}
	}

	public static void dropShopItem (Location location, ItemStack item, int quantity) {
		Location dropLocation = location.clone();

		dropLocation.add(0.5, getSlabYOffset(location), 0.5);

		ItemStack clonedItem = item.clone();
		ItemMeta meta = clonedItem.getItemMeta();

		String displayType = Slabbo.getInstance().getConfig().getString("itemdisplay", "quantity");

		if (displayType.equalsIgnoreCase("quantity")) {
			if (quantity < 1) quantity = 1;
			if (quantity > 64) quantity = 64;

			clonedItem.setAmount(quantity);
		} else {
			clonedItem.setAmount(64);
		}

		meta.getPersistentDataContainer().set(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.NO_MERGE.getKey(), PersistentDataType.INTEGER, 1);
		meta.getPersistentDataContainer().set(AttributeKey.SHOP_LOCATION.getKey(), PersistentDataType.STRING, ShopUtil.locationToString(location));

		clonedItem.setItemMeta(meta);

		Item itemEnt = location.getWorld().dropItem(dropLocation, clonedItem);

		itemEnt.setGravity(false);

		itemEnt.setVelocity(itemEnt.getVelocity().zero());

		itemEnt.teleport(dropLocation);
	}

	public static Item findShopItem (Location location) {
		Collection<Entity> nearbyEntites = location.getWorld().getNearbyEntities(location, 0.5, 2, 0.5);

		String locationString = ShopUtil.locationToString(location);

		Item returnItem = null;

		for (Entity entity : nearbyEntites) {
			boolean isItem = (entity instanceof Item) || (entity instanceof CraftItem);

			if (!isItem) continue;

			Item item = (Item) entity;

			ItemStack itemStack = item.getItemStack();

			if (!itemStack.hasItemMeta()) continue;

			ItemMeta meta = itemStack.getItemMeta();

			PersistentDataContainer container = meta.getPersistentDataContainer();

			String itemLocationString = container.get(AttributeKey.SHOP_LOCATION.getKey(), PersistentDataType.STRING);

			if (itemLocationString == null || itemLocationString.equals("")) continue;

			if (itemLocationString.equals(locationString)) {
				returnItem = item;
				break;
			}
		}

		return returnItem;
	}
}
