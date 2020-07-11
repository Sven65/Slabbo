package xyz.mackan.Slabbo.utils;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataUtil {
	public static boolean saveShops () {
		File dataFile = new File(Slabbo.getDataPath(), "shops.yml");

		FileConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);

		configFile.createSection("shops", Slabbo.shopUtil.shops);

//		Slabbo.shopUtil.shops.forEach((k, v) -> {
//			String configKey = String.format(
//				"%s,%d,%d,%d",
//				v.location.getWorld().getName(),
//				v.location.getBlockX(),
//				v.location.getBlockY(),
//				v.location.getBlockZ()
//			);
//
//			configFile.createSection(configKey, v.serialize());
//		});

		try {
			configFile.save(dataFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static HashMap<String, Shop> loadShops() {
		try {
			File dataFile = new File(Slabbo.getDataPath(), "shops.yml");


			YamlConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);

			System.out.println("Config: "+configFile.saveToString());

			Object shops = configFile.getConfigurationSection("shops").getValues(false);

			HashMap<String, Shop> shopData = (HashMap<String, Shop>) shops;

			return shopData;
		} catch (Exception e) {
			return null;
		}
	}
}
