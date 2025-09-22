package xyz.mackan.Slabbo.data;

import org.bukkit.Bukkit;
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
            File dataFile = new File(Slabbo.getDataPath(), "shops.yml");

            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);

            Object shops = configFile.getConfigurationSection("shops").getValues(false);

            HashMap<String, Shop> shopData = (HashMap<String, Shop>) shops;

            return shopData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void saveShops(Map<String, Shop> shops) {
        Bukkit.getScheduler().runTaskAsynchronously(Slabbo.getInstance(), new Runnable() {
            @Override
            public void run () {
                File dataFile = new File(Slabbo.getDataPath(), "shops.yml");

                FileConfiguration configFile = YamlConfiguration.loadConfiguration(dataFile);

                configFile.createSection("shops", shops);

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
}
