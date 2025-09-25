package xyz.mackan.Slabbo.data;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileStore implements DataStore {
    @Override
    @Nullable
    public Map<String, Shop> loadShops() {
        try {
            Bukkit.getLogger().info("Loading shops from file...");

            File dataFile = new File(Slabbo.getDataPath(), "shops.yml");
            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);

            ConfigurationSection shopsSection = configFile.getConfigurationSection("shops");
            if (shopsSection == null) {
                return new HashMap<>(); // Return an empty map if the section is missing
            }

            HashMap<String, Shop> shopData = new HashMap<>();
            for (String key : shopsSection.getKeys(false)) {
                ConfigurationSection shopSection = shopsSection.getConfigurationSection(key);
                if (shopSection == null) continue;
                Shop shop = (Shop) shopSection.getSerializable("shop", Shop.class);
                if (shop == null) continue;
                shop.shopTaxRate = shopSection.getString("shopTaxRate", null);
                shop.shopTaxMode = shopSection.getString("shopTaxMode", null);
                shopData.put(key, shop);
            }


            Bukkit.getLogger().info("Loaded shops from file...");

            return shopData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveShops(Map<String, Shop> shops) {
        Bukkit.getScheduler().runTaskAsynchronously(Slabbo.getInstance(), new Runnable() {
            @Override
            public void run () {
                Bukkit.getLogger().info("Saving shops to file...");

                File dataFile = new File(Slabbo.getDataPath(), "shops.yml");
                YamlConfiguration configFile = new YamlConfiguration();
                ConfigurationSection shopsSection = configFile.createSection("shops");
                for (Map.Entry<String, Shop> entry : shops.entrySet()) {
                    Shop shop = entry.getValue();
                    ConfigurationSection shopSection = shopsSection.createSection(entry.getKey());
                    shopSection.set("shop", shop);
                    shopSection.set("shopTaxRate", shop.shopTaxRate);
                    shopSection.set("shopTaxMode", shop.shopTaxMode);
                }
                try {
                    configFile.save(dataFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addShop(Shop shop) {
        throw new UnsupportedOperationException("Not used with cache");
    }

    @Override
    public void updateShop(Shop shop) {
        throw new UnsupportedOperationException("Not used with cache");
    }

    @Override
    public void removeShop(String shopId) {
        throw new UnsupportedOperationException("Not used with cache");
    }

    @Override
    public Shop getShop(String shopId) {
        throw new UnsupportedOperationException("Not used with cache");
    }

    @Override
    public List<Shop> getShopsByOwner(UUID owner) {
        throw new UnsupportedOperationException("Not used with cache");
    }

    @Override
    public void saveShopsOnMainThread(Map<String, Shop> shops) {

        File dataFile = new File(Slabbo.getDataPath(), "shops.yml");
        FileConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);
        configFile.createSection("shops", shops);
        try {
            configFile.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresCache() {
        return true;
    }

    @Override
    public String getStorageType() {
        return "file";
    }
}
