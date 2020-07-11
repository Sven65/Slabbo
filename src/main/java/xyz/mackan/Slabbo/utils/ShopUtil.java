package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import xyz.mackan.Slabbo.types.Shop;

import java.util.*;

public class ShopUtil {
	public HashMap<String, Shop> shops = new HashMap<String, Shop>();
	public HashMap<UUID, List<Shop>> shopsByOwnerId = new HashMap<UUID, List<Shop>>();

	public ShopUtil () { }

	public int getOwnerCount (UUID ownerId) {
		if (!shopsByOwnerId.containsKey(ownerId)) return 0;

		return shopsByOwnerId.get(ownerId).size();
	}

	public void put (String key, Shop value) {
		shops.put(key, value);

		List<Shop> shopList = shopsByOwnerId.get(value.ownerId);

		if (shopList == null) {
			shopList = new ArrayList<Shop>();
		}

		shopList.add(value);

		shopsByOwnerId.put(value.ownerId, shopList);
	}

	public void loadShops () {
		shops = DataUtil.loadShops();

		if (shops == null) {
			shops = new HashMap<String, Shop>();
		} else {

			shops.forEach((k, v) -> {
				List<Shop> shopList = shopsByOwnerId.get(v.ownerId);

				if (shopList == null) {
					shopList = new ArrayList<Shop>();
				}

				shopList.add(v);

				shopsByOwnerId.put(v.ownerId, shopList);
			});
		}
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

}
