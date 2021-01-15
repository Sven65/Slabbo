package xyz.mackan.Slabbo.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.abstractions.SlabboItemAPI;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.pluginsupport.PluginSupport;
import xyz.mackan.Slabbo.types.SlabType;

import java.util.*;

public class ItemUtil {

	private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();
	private static SlabboItemAPI itemAPI = Bukkit.getServicesManager().getRegistration(SlabboItemAPI.class).getProvider();

	public static double getSlabYOffset (Location location) {
		Block block = location.getBlock();

		if (block == null) return 0;

		boolean stairsEnabled = Slabbo.getInstance().getConfig().getBoolean("stairs", true);
		boolean barriersEnabled = Slabbo.getInstance().getConfig().getBoolean("barriershops", true);

		boolean isUpsideDownStair = api.isUpsideDownStair(block);
		boolean isBarrier = block.getType() == Material.BARRIER;

		if ((stairsEnabled && isUpsideDownStair) || (barriersEnabled && isBarrier)) {
			return 1.0;
		}

		SlabType slabType = api.getSlabType(block);


		if (slabType == SlabType.NONE) {
			block.setType(itemAPI.getDefaultSlab().getType());

			slabType = SlabType.BOTTOM;
		}

		if (slabType == SlabType.BOTTOM) {
			return 0.5;
		} else {
			return 1.0;
		}
	}

	public static void setEntityToShopItem (Item item, Location location) {
		api.setNoPickup(item, 1);
		api.setNoDespawn(item, 1);
		api.setNoMerge(item, 1);
		api.setShopLocation(item, location);
	}

	public static void dropShopItem (Location location, ItemStack item, int quantity) {
		Location dropLocation = location.clone();

		dropLocation.add(0.5, getSlabYOffset(location), 0.5);

		ItemStack clonedItem = item.clone();
		ItemMeta meta = clonedItem.getItemMeta();

		String displayType = Slabbo.getInstance().getConfig().getString("itemdisplay", "quantity");

		if (clonedItem.getType() == Material.AIR) {
			return;
		}

		if (displayType.equalsIgnoreCase("quantity")) {
			if (quantity < 1) quantity = 1;
			if (quantity > 64) quantity = 64;

			clonedItem.setAmount(quantity);
		} else if (displayType.equalsIgnoreCase("single")) {
			clonedItem.setAmount(1);
		} else if (displayType.equalsIgnoreCase("none")) {
			return;
		} else {
			clonedItem.setAmount(64);
		}

		if (clonedItem.hasItemMeta() && meta != null) {
			meta.setDisplayName("Slabbo Item " + ShopManager.locationToString(location));

			if (PluginSupport.EnabledPlugins.holoDropsX) {
				ArrayList<String> lore = new ArrayList<String>();

				lore.add("Display Item");

				meta.setLore(Arrays.asList("Display Item"));
			}

			clonedItem.setItemMeta(meta);
		}

		Item itemEnt = location.getWorld().dropItem(dropLocation, clonedItem);

		setEntityToShopItem(itemEnt, location);

		api.setGravity(itemEnt, false);

		itemEnt.setVelocity(itemEnt.getVelocity().zero());

		itemEnt.teleport(dropLocation);
	}

	public static List<Item> findSlabboItemsInWorld (World world) {
		Collection<Entity> worldEntities = world.getEntities();

		List<Item> shopItems = new ArrayList<Item>();

		for (Entity entity : worldEntities) {
			boolean isItem = api.isItem(entity);

			if (!isItem) continue;

			Item item = (Item) entity;

			if (!api.isSlabboItem(item)) continue;;

			shopItems.add(item);
		}

		return shopItems;
	}

	public static List<Item> findShopItems (Location location) {
		Collection<Entity> nearbyEntites = api.getNearbyEntities(location, 0.5, 2, 0.5);

		String locationString = ShopManager.locationToString(location);

		List<Item> shopItems = new ArrayList<Item>();

		for (Entity entity : nearbyEntites) {
			boolean isItem = api.isItem(entity);

			if (!isItem) continue;

			Item item = (Item) entity;

			if (!api.isSlabboItem(item)) continue;

			String itemLocationString = api.getShopLocation(item);

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

	public static ItemStack setContainerIntValue(ItemStack itemStack, NamespacedKey key, int value) {
		ItemMeta itemMeta = itemStack.getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		container.set(key, PersistentDataType.INTEGER, value);

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	public static ItemStack setContainerStringValue(ItemStack itemStack, NamespacedKey key, String value) {
		ItemMeta itemMeta = itemStack.getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		container.set(key, PersistentDataType.STRING, value);

		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	public static int getContainerIntValue (ItemStack itemStack, NamespacedKey key) {
		if (itemStack == null || key == null) return -1;

		if (!itemStack.hasItemMeta()) return -1;

		ItemMeta itemMeta = itemStack.getItemMeta();

		if (itemMeta == null) return -1;

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		if (container == null) return -1;

		if (!container.has(key, PersistentDataType.INTEGER)) return -1;

		return container.get(key, PersistentDataType.INTEGER);
	}

	public static String getContainerStringValue (ItemStack itemStack, NamespacedKey key) {
		if (!itemStack.hasItemMeta()) return null;

		ItemMeta itemMeta = itemStack.getItemMeta();

		if (itemMeta == null) return null;

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		if (container == null) return null;

		return container.get(key, PersistentDataType.STRING);
	}

	public static String getLoreValue (ItemStack itemStack, String key) {
		if (!itemStack.hasItemMeta()) return "";

		ItemMeta itemMeta = itemStack.getItemMeta();

		if (!itemMeta.hasLore()) return "";

		List<String> lore = itemMeta.getLore();

		String value = "";

		for (String line : lore) {
			if (!line.startsWith(key)) continue;

			value = line.replace(key+"=", "");
		}

		return value;
	}

	public static ItemStack setLoreValue (ItemStack itemStack, String key, String value) {
		ItemMeta meta = itemStack.getItemMeta();

		List<String> currentLore = new ArrayList<String>();

		if (meta.hasLore()) {
			currentLore = meta.getLore();
		}

		currentLore.add(key+"="+value);

		meta.setLore(currentLore);

		itemStack.setItemMeta(meta);

		return itemStack;
	}
}
