package xyz.mackan.Slabbo.data;

import xyz.mackan.Slabbo.types.Shop;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DataStore {
    /**
     * Loads all shops from persistent storage.
     * @return a map of shop IDs to Shop objects
     */
    Map<String, Shop> loadShops();

    /**
     * Saves all shops to persistent storage.
     * @param shops a map of shop IDs to Shop objects
     */
    void saveShops(Map<String, Shop> shops);

    /**
     * Adds a new shop to persistent storage.
     * @param shop the Shop to add
     */
    void addShop(Shop shop);

    /**
     * Updates an existing shop in persistent storage.
     * @param shop the Shop to update
     */
    void updateShop(Shop shop);

    /**
     * Removes a shop from persistent storage.
     * @param shopId the ID of the shop to remove
     */
    void removeShop(String shopId);

    /**
     * Retrieves a shop by its ID.
     * @param shopId the ID of the shop
     * @return the Shop object, or null if not found
     */
    Shop getShop(String shopId);

    /**
     * Retrieves all shops owned by a specific player.
     * @param owner the UUID of the owner
     * @return a list of Shop objects
     */
    List<Shop> getShopsByOwner(UUID owner);

    /**
     * Saves all shops to persistent storage synchronously on the main server thread.
     * This should only be called during plugin disable or in rare cases where
     * synchronous saving is required. For normal operation, use asynchronous saving.
     *
     * @param shops The map of shop location strings to Shop objects to be saved.
     */
    void saveShopsOnMainThread(Map<String, Shop> shops);

    /**
     * Performs any necessary cleanup, such as closing database connections.
     */
    void close();

    /**
     * Indicates whether this DataStore requires all shops to be loaded and cached in memory.
     * <p>
     * For example, file-based storage (like YAML) typically requires caching all shops in memory
     * for efficient access and modification, while database-backed storage (like SQLite) can
     * query shops on demand without caching.
     * </p>
     *
     * @return true if the DataStore requires in-memory caching of all shops, false otherwise
     */
    boolean requiresCache();

    default String getStorageType() {
        return this.getClass().getSimpleName();
    }
}