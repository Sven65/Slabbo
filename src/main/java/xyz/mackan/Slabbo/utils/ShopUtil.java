package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import xyz.mackan.Slabbo.types.Shop;

import java.util.HashMap;

public class ShopUtil {
	public HashMap<String, Shop> shops = new HashMap<String, Shop>();

	public ShopUtil () { }

	public void loadShops () {
		shops = DataUtil.loadShops();

		if (shops == null) {
			shops = new HashMap<String, Shop>();
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
