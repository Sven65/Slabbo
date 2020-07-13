package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DataUtil {
	public static void saveShops () {
		Bukkit.getScheduler().runTaskAsynchronously(Slabbo.getInstance(), new Runnable() {
			@Override
			public void run () {
				File dataFile = new File(Slabbo.getDataPath(), "shops.yml");

				FileConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);

				configFile.createSection("shops", Slabbo.shopUtil.shops);

				try {
					configFile.save(dataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static HashMap<String, Shop> loadShops() {
		try {
			File dataFile = new File(Slabbo.getDataPath(), "shops.yml");


			YamlConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);

			Object shops = configFile.getConfigurationSection("shops").getValues(false);

			HashMap<String, Shop> shopData = (HashMap<String, Shop>) shops;

			return shopData;
		} catch (Exception e) {
			return null;
		}
	}
}
