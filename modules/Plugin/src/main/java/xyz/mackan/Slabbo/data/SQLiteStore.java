package xyz.mackan.Slabbo.data;

import xyz.mackan.Slabbo.types.Shop;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLiteStore implements DataStore {
    @Override
    public Map<String, Shop> loadShops() {
        return Map.of();
    }

    @Override
    public void saveShops(Map<String, Shop> shops) {

    }

    @Override
    public void addShop(Shop shop) {

    }

    @Override
    public void updateShop(Shop shop) {

    }

    @Override
    public void removeShop(String shopId) {

    }

    @Override
    public Shop getShop(String shopId) {
        return null;
    }

    @Override
    public List<Shop> getShopsByOwner(UUID owner) {
        return List.of();
    }

    @Override
    public void saveShopsOnMainThread(Map<String, Shop> shops) {
        // Implement synchronous save logic for SQLite, or call your normal saveShops if it's already synchronous
        saveShops(shops);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresCache() {
        return false;
    }
}
