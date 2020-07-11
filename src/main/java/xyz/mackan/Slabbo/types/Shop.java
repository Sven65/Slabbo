package xyz.mackan.Slabbo.types;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("Shop")
public class Shop implements Cloneable, ConfigurationSerializable {
	private static final long serialVersionUID = -1358999872552913870L;

	public int buyPrice;
	public int sellPrice;
	public int quantity;

	public Location location;

	public ItemStack item;

	public int stock = 0;

	public UUID ownerId;

	public boolean admin;

	public Shop (int buyPrice, int sellPrice, int quantity, Location location, ItemStack item, int stock, UUID ownerId, boolean admin) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
		this.stock = stock;
		this.ownerId = ownerId;
		this.admin = admin;
	}

	public Shop (int buyPrice, int sellPrice, int quantity, Location location, ItemStack item, int stock) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
		this.stock = stock;
	}

	public Shop (int buyPrice, int sellPrice, int quantity, Location location, ItemStack item) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
	}

	public Map<String, Object> serialize () {
		LinkedHashMap result = new LinkedHashMap();

		result.put("buyPrice", buyPrice);
		result.put("sellPrice", sellPrice);
		result.put("quantity", quantity);
		result.put("location", location);
		result.put("item", item);
		result.put("stock", stock);
		result.put("admin", admin);
		result.put("ownerId", ownerId.toString());

		return result;
	}

	public static Shop deserialize (Map<String, Object> args) {
		int buyPrice = (Integer) args.get("buyPrice");
		int sellPrice = (Integer) args.get("sellPrice");
		int quantity = (Integer) args.get("quantity");
		int stock = (Integer) args.get("stock");

		Location location = (Location) args.get("location");

		ItemStack item = (ItemStack) args.get("item");

		String loadedOwnerId = (String) args.get("ownerId");

		UUID ownerId = UUID.fromString(loadedOwnerId);
		boolean admin = (boolean) args.get("admin");

		return new Shop(buyPrice, sellPrice, quantity, location, item, stock, ownerId, admin);
	}
}
