package xyz.mackan.Slabbo.types;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("ShopLimit")
public class ShopLimit implements Cloneable, ConfigurationSerializable {
	private static final long serialVersionUID = 8716065921653425642L;

	public int stock;
	public int restockTime;

	public long lastRestock;

	public ShopLimit (int stock, int restockTime, long lastRestock) {
		this.stock = stock;
		this.restockTime = restockTime;
		this.lastRestock = lastRestock;
	}

	@Override
	public Map<String, Object> serialize () {
		LinkedHashMap result = new LinkedHashMap();

		result.put("stock", stock);
		result.put("restockTime", restockTime);
		result.put("lastRestock", lastRestock);

		return result;
	}

	public static ShopLimit deserialize(Map<String, Object> args) {
		int stock = (Integer) args.get("stock");
		int restockTime = (Integer) args.get("restockTime");
		long lastRestock = (long) args.get("lastRestock");

		return new ShopLimit(stock, restockTime, lastRestock);
	}
}
