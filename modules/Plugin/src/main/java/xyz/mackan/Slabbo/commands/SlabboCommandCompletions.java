package xyz.mackan.Slabbo.commands;

import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.types.Shop;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SlabboCommandCompletions {

	public static List<String> getImportFiles () {
		File folder = new File(Slabbo.getDataPath());

		return Arrays.stream(folder.listFiles())
				.filter(file -> file.isFile() && !file.getName().equalsIgnoreCase("shops.yml") && file.getName().endsWith(".yml"))
				.map(file -> file.getName())
				.collect(Collectors.toList());
	}

	public static List<String> getVirtualShopNames() {
		return Slabbo.getInstance().getShopManager().getAllShops().values().stream()
				.filter(shop -> shop.virtual)
				.map(shop -> shop.shopName)
				.collect(Collectors.toList());
	}

	public static List<String> getVirtualAdminShopNames() {
		return Slabbo.getInstance().getShopManager().getAllShops().values().stream()
				.filter(shop -> shop.virtual)
				.filter(shop -> shop.admin)
				.map(shop -> shop.shopName)
				.collect(Collectors.toList());
	}
}