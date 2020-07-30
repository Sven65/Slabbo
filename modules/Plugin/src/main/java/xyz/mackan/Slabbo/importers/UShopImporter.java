package xyz.mackan.Slabbo.importers;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.manager.ShopManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UShopImporter {
	public static ImportResult importUShops (File file) {
		List<String> skippedShops = new ArrayList<String>();
		List<Shop> shops = new ArrayList<Shop>();

		FileConfiguration uShopConfig = new YamlConfiguration();

		try {
			uShopConfig.load(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return null;
		}

		for (String key : uShopConfig.getKeys(false)) {
			if (ShopManager.shops.containsKey(key)) {
				skippedShops.add(key);
				continue;
			}

			ConfigurationSection shopConfig = uShopConfig.getConfigurationSection(key);

			String shopOwnerId = shopConfig.getString("host");

			UUID shopOwnerUUID = UUID.fromString(shopOwnerId);

			ItemStack itemStack = shopConfig.getItemStack("item");

			int buyPrice = shopConfig.getInt("buyPrice");
			int sellPrice = shopConfig.getInt("sellPrice");

			int quantity = shopConfig.getInt("stack");

			int stock = shopConfig.getInt("amount");

			boolean isAdmin = shopConfig.getBoolean("admin");

			if (isAdmin) {
				stock = 0;
			}

			Location shopLocation = ShopManager.fromString(key);

			Shop shop = new Shop(buyPrice, sellPrice, quantity, shopLocation, itemStack, stock, shopOwnerUUID, isAdmin, null);

			shops.add(shop);
		}

		return new ImportResult(shops, skippedShops);
	}
}
