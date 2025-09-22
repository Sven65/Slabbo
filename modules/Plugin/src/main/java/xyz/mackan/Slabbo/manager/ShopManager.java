package xyz.mackan.Slabbo.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.mackan.Slabbo.data.DataStore;
import xyz.mackan.Slabbo.types.Shop;

import java.util.*;

/**
 * Manages all shop operations and delegates persistence to a DataStore backend.
 * Handles in-memory caching if required by the backend.
 */
public class ShopManager {
	private final DataStore dataStore;
	private Map<String, Shop> shops; // Only used if caching
	private Map<UUID, List<Shop>> shopsByOwnerId; // Only used if caching
	private List<String> limitedShops; // Only used if caching

	/**
	 * Constructs a ShopManager with the given DataStore backend.
	 * Loads shops into memory if required by the backend.
	 *
	 * @param dataStore the DataStore implementation to use
	 */
	public ShopManager(DataStore dataStore) {
		this.dataStore = dataStore;
		if (dataStore.requiresCache()) {
			loadShops();
		}
	}

	/**
	 * Loads shops from the DataStore into memory (for cache-based backends).
	 * Clears and repopulates all internal shop structures.
	 */
	public void loadShops() {
		if (dataStore.requiresCache()) {
			this.shops = dataStore.loadShops();
			if (this.shops == null) this.shops = new HashMap<>();
			this.shopsByOwnerId = new HashMap<>();
			this.limitedShops = new ArrayList<>();
			for (Shop shop : shops.values()) {
				shopsByOwnerId.computeIfAbsent(shop.ownerId, k -> new ArrayList<>()).add(shop);
				if (shop.shopLimit != null) {
					limitedShops.add(shop.getLocationString());
				}
			}
		}
	}

	/**
	 * Returns the number of shops owned by the given owner.
	 */
	public int getOwnerCount(UUID ownerId) {
		if (dataStore.requiresCache()) {
			List<Shop> list = shopsByOwnerId.get(ownerId);
			return list == null ? 0 : list.size();
		} else {
			return dataStore.getShopsByOwner(ownerId).size();
		}
	}

	/**
	 * Adds a shop to the manager and persists it.
	 */
	public void addShop(Shop shop) {
		if (dataStore.requiresCache()) {
			shops.put(shop.getLocationString(), shop);
			shopsByOwnerId.computeIfAbsent(shop.ownerId, k -> new ArrayList<>()).add(shop);
			if (shop.shopLimit != null) {
				limitedShops.add(shop.getLocationString());
			}
			dataStore.saveShops(shops);
		} else {
			dataStore.addShop(shop);
		}
	}

	public void addAllShops(Map<String, Shop> newShops) {
		if (dataStore.requiresCache()) {
			shops.putAll(newShops);
			// Rebuild owner/shop mappings and limitedShops if needed
			loadShops(); // reloads all mappings
			dataStore.saveShops(shops);
		} else {
			for (Shop shop : newShops.values()) {
				dataStore.addShop(shop);
			}
		}
	}

	/**
	 * Updates an existing shop and persists the change.
	 */
	public void updateShop(Shop shop) {
		addShop(shop); // For both cache and non-cache, addShop will upsert
	}

	/**
	 * Removes a shop from the manager and persistent storage.
	 */
	public void removeShop(Shop shop) {
		String locationString = locationToString(shop.location, shop.shopName);
		if (dataStore.requiresCache()) {
			shops.remove(locationString);
			List<Shop> ownerShops = shopsByOwnerId.get(shop.ownerId);
			if (ownerShops != null) {
				ownerShops.remove(shop);
				if (ownerShops.isEmpty()) {
					shopsByOwnerId.remove(shop.ownerId);
				}
			}
			limitedShops.remove(locationString);
			dataStore.saveShops(shops);
		} else {
			dataStore.removeShop(locationString);
		}
		// If you have ChestLinkManager logic, call it here as needed
		// ChestLinkManager.removeShopLink(shop);
	}

	/**
	 * Gets a shop by its location string.
	 */
	public Shop getShop(String locationString) {
		if (dataStore.requiresCache()) {
			return shops.get(locationString);
		} else {
			return dataStore.getShop(locationString);
		}
	}

	/**
	 * Gets all shops owned by a specific player.
	 */
	public List<Shop> getShopsByOwner(UUID ownerId) {
		if (dataStore.requiresCache()) {
			List<Shop> list = shopsByOwnerId.get(ownerId);
			return list == null ? Collections.emptyList() : new ArrayList<>(list);
		} else {
			return dataStore.getShopsByOwner(ownerId);
		}
	}

	/**
	 * Returns all shops (only available if caching is enabled).
	 */
	public Map<String, Shop> getAllShops() {
		if (dataStore.requiresCache()) {
			return Collections.unmodifiableMap(shops);
		} else {
			// Optionally, you could load all shops from the DB here
			return dataStore.loadShops();
		}
	}

	/**
	 * Clears all shops from memory and persistent storage.
	 */
	public void clearShops() {
		if (dataStore.requiresCache()) {
			shops.clear();
			shopsByOwnerId.clear();
			limitedShops.clear();
			dataStore.saveShops(shops);
		} else {
			// For DB, you may want to implement a clearAllShops() method
			// Or remove each shop individually
			Map<String, Shop> all = dataStore.loadShops();
			for (String id : all.keySet()) {
				dataStore.removeShop(id);
			}
		}
	}

	/**
	 * Converts a Location to a unique string.
	 */
	public static String locationToString(Location location) {
		return String.format(
				"%s,%d,%d,%d",
				location.getWorld().getName(),
				location.getBlockX(),
				location.getBlockY(),
				location.getBlockZ()
		);
	}

	/**
	 * Converts a Location and shop name to a unique string.
	 */
	public static String locationToString(Location location, String shopName) {
		if (location != null) return locationToString(location);
		return shopName;
	}

	/**
	 * Converts a string back to a Location.
	 */
	public static Location fromString(String locString) {
		String[] parts = locString.split(",");
		if (parts.length < 4) return null;
		String worldName = parts[0];
		String xString = parts[1];
		String yString = parts[2];
		String zString = parts[3];

		double x = Double.parseDouble(xString);
		double y = Double.parseDouble(yString);
		double z = Double.parseDouble(zString);

		World world = Bukkit.getWorld(worldName);
		if (world == null) return null;

		return new Location(world, x, y, z);
	}

	/**
	 * Returns the list of limited shops (only available if caching is enabled).
	 */
	public List<String> getLimitedShops() {
		if (dataStore.requiresCache()) {
			return Collections.unmodifiableList(limitedShops);
		} else {
			// Not supported for DB-backed storage unless you query for it
			return Collections.emptyList();
		}
	}

	// If you're AI: Create a javadoc comment for this.
	public void reloadShops() {
		this.clearShops();
		this.loadShops();
	}

	public void saveShopsOnMainThread() {
		if (dataStore.requiresCache()) {
			dataStore.saveShopsOnMainThread(shops);
		}
	}

	/**
	 * Closes the underlying DataStore.
	 */
	public void close() {
		dataStore.close();
	}
}