package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.types.MetaKey;
import xyz.mackan.Slabbo.types.SlabType;

import java.util.*;

public class ItemUtil {

	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

	public static double getSlabYOffset (Location location) {
		Block block = location.getBlock();

		if (block == null) return 0;

//		BlockData blockData = block.getBlockData();
//
//		boolean isSlab = (blockData instanceof Slab);
//
//		if (!isSlab) return 0;
//
//		Slab slab = (Slab) blockData;
//
//		Slab.Type slabType = slab.getType();

		SlabType slabType = api.getSlabType(block);

		if (slabType == SlabType.BOTTOM) {
			return 0.5;
		} else {
			return 1.0;
		}
	}

	// TODO: Make this API method
	public static void setEntityToShopItem (Item item, Location location) {
		item.setMetadata(MetaKey.NO_PICKUP.getKey(), new FixedMetadataValue(Slabbo.getInstance(), 1));
		item.setMetadata(MetaKey.NO_DESPAWN.getKey(), new FixedMetadataValue(Slabbo.getInstance(), 1));
		item.setMetadata(MetaKey.NO_MERGE.getKey(), new FixedMetadataValue(Slabbo.getInstance(), 1));
		item.setMetadata(MetaKey.SHOP_LOCATION.getKey(), new FixedMetadataValue(Slabbo.getInstance(), ShopUtil.locationToString(location)));
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

//		meta.getPersistentDataContainer().set(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER, 1);
//		meta.getPersistentDataContainer().set(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER, 1);
//		meta.getPersistentDataContainer().set(AttributeKey.NO_MERGE.getKey(), PersistentDataType.INTEGER, 1);
//		meta.getPersistentDataContainer().set(AttributeKey.SHOP_LOCATION.getKey(), PersistentDataType.STRING, ShopUtil.locationToString(location));
//
//		clonedItem.setItemMeta(meta);

		meta.setLore(Arrays.asList("Slabbo Item "+UUID.randomUUID().toString()));

		clonedItem.setItemMeta(meta);

		Item itemEnt = location.getWorld().dropItem(dropLocation, clonedItem);

		setEntityToShopItem(itemEnt, location);

		api.setGravity(itemEnt, false);

		//itemEnt.setGravity(false);

		itemEnt.setVelocity(itemEnt.getVelocity().zero());

		itemEnt.teleport(dropLocation);
	}

	public static List<Item> findSlabboItemsInWorld (World world) {
		Collection<Entity> worldEntities = world.getEntities();

		List<Item> shopItems = new ArrayList<Item>();

		for (Entity entity : worldEntities) {
			boolean isItem = api.isItem(entity);//(entity instanceof Item) || (entity instanceof CraftItem);

			if (!isItem) continue;

			Item item = (Item) entity;

			if (!item.hasMetadata(MetaKey.NO_PICKUP.getKey())) continue;

//			ItemMeta meta = itemStack.getItemMeta();
//
//			PersistentDataContainer container = meta.getPersistentDataContainer();
//
//			int noPickup = container.get(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER);
//			int noDespawn = container.get(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER);

			// TODO: Move this to api.isSlabboItem(Item)

			boolean isSlabboItem = api.isSlabboItem(item);

			if (!isSlabboItem) continue;

			shopItems.add(item);
		}

		return shopItems;
	}

	public static List<Item> findShopItems (Location location) {
		Collection<Entity> nearbyEntites = api.getNearbyEntities(location, 0.5, 2, 0.5);//location.getWorld().getNearbyEntities(location, 0.5, 2, 0.5);

		String locationString = ShopUtil.locationToString(location);

		List<Item> shopItems = new ArrayList<Item>();

		for (Entity entity : nearbyEntites) {
			boolean isItem = api.isItem(entity);

			if (!isItem) continue;

			Item item = (Item) entity;

			ItemStack itemStack = item.getItemStack();

			if (!item.hasMetadata(MetaKey.NO_PICKUP.getKey())) continue;

//			ItemMeta meta = itemStack.getItemMeta();
//
//			PersistentDataContainer container = meta.getPersistentDataContainer();
//
//			String itemLocationString = container.get(AttributeKey.SHOP_LOCATION.getKey(), PersistentDataType.STRING);

			String itemLocationString = item.getMetadata(MetaKey.SHOP_LOCATION.getKey()).get(0).asString();

			if (itemLocationString == null || itemLocationString.equals("")) continue;

			if (itemLocationString.equals(locationString)) {
				shopItems.add(item);
			}
		}

		return shopItems;
	}

	public static void removeShopItemsAtLocation (Location location) {
		List<Item> shopItems = findShopItems(location);

		for (Item shopItem : shopItems) {
			shopItem.remove();
		}
	}

	public static void removeShopItems (World world) {
		List<Item> shopItems = findSlabboItemsInWorld(world);

		for (Item shopItem : shopItems) {
			shopItem.remove();
		}
	}


}
