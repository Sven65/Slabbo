package xyz.mackan.Slabbo.types;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scheduler.BukkitRunnable;
import scala.Int;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("ShopLimit")
public class ShopLimit implements Cloneable, ConfigurationSerializable {
	private static final long serialVersionUID = 8716065921653425642L;

	public int buyStock;
	public int sellStock;

	public int buyStockLeft;
	public int sellStockLeft;

	public int restockTime;

	public long lastRestock;

	public boolean enabled = false;

	public ShopLimit (int buyStock, int sellStock, int restockTime, long lastRestock, boolean enabled) {
		this.buyStock = buyStock;
		this.sellStock = sellStock;
		this.restockTime = restockTime;
		this.lastRestock = lastRestock;
		this.enabled = enabled;
	}

	@Override
	public Map<String, Object> serialize () {
		LinkedHashMap result = new LinkedHashMap();

		result.put("buyStock", buyStock);
		result.put("sellStock", sellStock);
		result.put("restockTime", restockTime);
		result.put("lastRestock", lastRestock);
		result.put("enabled", enabled);
		result.put("sellStockLeft", sellStockLeft);
		result.put("buyStockLeft", buyStockLeft);

		return result;
	}

	public static ShopLimit deserialize(Map<String, Object> args) {
		int buyStock = (Integer) args.get("buyStock");
		int sellStock = (Integer) args.get("sellStock");
		int restockTime = (Integer) args.get("restockTime");
		Number lastRestock = (Number) args.get("lastRestock");
		boolean enabled = (boolean) args.get("enabled");

		int buyStockLeft = (Integer) args.get("buyStockLeft");
		int sellStockLeft = (Integer) args.get("sellStockLeft");

		ShopLimit limit = new ShopLimit(buyStock, sellStock, restockTime, lastRestock.longValue(), enabled);

		limit.buyStockLeft = buyStockLeft;
		limit.sellStockLeft = sellStockLeft;

		return limit;
	}

	public void restock () {
		buyStockLeft = buyStock;
		sellStockLeft = sellStock;

		lastRestock = Instant.now().getEpochSecond();
	}
}
