package xyz.mackan.Slabbo.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;

import java.util.*;

public class ShopManager {
	public static HashMap<String, Shop> shops = new HashMap<String, Shop>();
	public static HashMap<UUID, List<Shop>> shopsByOwnerId = new HashMap<UUID, List<Shop>>();

	public static List<String> limitedShops = new ArrayList<String>();

	public static int getOwnerCount (UUID ownerId) {
		if (!shopsByOwnerId.containsKey(ownerId)) return 0;

		return shopsByOwnerId.get(ownerId).size();
	}

	public static void put (String key, Shop value) {
		shops.put(key, value);

		List<Shop> shopList = shopsByOwnerId.get(value.ownerId);

		if (shopList == null) {
			shopList = new ArrayList<Shop>();
		}

		shopList.add(value);

		shopsByOwnerId.put(value.ownerId, shopList);

		if (value.shopLimit != null) {
			limitedShops.add(key);
		}
	}

	public static void updateShop (Shop shop) {
		put(shop.getLocationString(), shop);
	}

	public static void removeShop(Shop shop) {
		String locationString = ShopManager.locationToString(shop.location);

		shops.remove(locationString);

		List<Shop> shopList = shopsByOwnerId.get(shop.ownerId);

		if (shopList != null) {
			shopList.remove(shop);

			shopsByOwnerId.put(shop.ownerId, shopList);
		}

		limitedShops.remove(locationString);
	}

	public static void loadShops () {
		shops = DataUtil.loadShops();

		if (shops == null) {
			shops = new HashMap<String, Shop>();
		} else {

			shops.forEach((k, v) -> {
				if (v.shopLimit != null) {
					limitedShops.add(v.getLocationString());
				}

				List<Shop> shopList = shopsByOwnerId.get(v.ownerId);

				if (shopList == null) {
					shopList = new ArrayList<Shop>();
				}

				shopList.add(v);

				shopsByOwnerId.put(v.ownerId, shopList);

				if (v.linkedChestLocation != null) {
					ChestLinkManager.links.put(v.linkedChestLocation, v);
				}

			});
		}
	}

	public static void clearShops () {
		shops.clear();
		shopsByOwnerId.clear();
		limitedShops.clear();
	}

	public static String locationToString (Location location) {
		return String.format(
				"%s,%d,%d,%d",
				location.getWorld().getName(),
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ()
		);
	}

	public static Location fromString(String locString) {
		String[] parts = locString.split(",");

		String worldName = parts[0];
		String xString = parts[1];
		String yString = parts[2];
		String zString = parts[3];

		double x = Double.parseDouble(xString);
		double y = Double.parseDouble(yString);
		double z = Double.parseDouble(zString);

		World world = Bukkit.getWorld(worldName);

		if (world == null) return null;

		return new Location(world, x, y, z);
	}

}
