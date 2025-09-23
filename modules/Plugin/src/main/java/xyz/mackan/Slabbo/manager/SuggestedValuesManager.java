package xyz.mackan.Slabbo.manager;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.mackan.Slabbo.Slabbo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SuggestedValuesManager {
    private final Map<String, SuggestedValue> internalDefaults = new HashMap<>();
    private final Map<String, SuggestedValue> overrides = new HashMap<>();

    public SuggestedValuesManager(File dataFolder) {
        loadInternalDefaults();
        loadOverrides(new File(dataFolder, "suggested.yml"));
    }

    private void loadInternalDefaults() {
        // Example defaults. Add more as needed.
        // NOTE: Shops support decimals for buy/sell price, but quantity must be a full number (no decimals).
        internalDefaults.put("DIAMOND", new SuggestedValue(100.0, 50.0, 1));
        internalDefaults.put("IRON_INGOT", new SuggestedValue(10.0, 5.0, 1));
    }

    private void loadOverrides(File file) {
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            // NOTE: Shops support decimals for buy/sell price, but quantity must be a full number (no decimals).
            double buy = config.getDouble(key + ".buy", -1);
            double sell = config.getDouble(key + ".sell", -1);
            int quantity = config.getInt(key + ".quantity", 1);
            overrides.put(key.toUpperCase(), new SuggestedValue(buy, sell, quantity));
        }
    }

    public SuggestedValue getSuggestion(Material material) {
        String key = material.name();
        SuggestedValue value = overrides.containsKey(key) ? overrides.get(key) : internalDefaults.getOrDefault(key, null);
        if (value == null) return null;
        boolean allowCents = false;
        try {
            allowCents = Slabbo.getInstance().getConfig().getBoolean("allowCents", false);
        } catch (Exception e) {
            // Fallback: if config or plugin not loaded, default to false
        }
        if (!allowCents) {
            // Round buy/sell to nearest integer if decimals are not allowed
            return new SuggestedValue(Math.round(value.buy), Math.round(value.sell), value.quantity);
        }
        return value;
    }

    public static class SuggestedValue {
        public final double buy;
        public final double sell;
        public final int quantity;
        public SuggestedValue(double buy, double sell, int quantity) {
            this.buy = buy;
            this.sell = sell;
            this.quantity = quantity;
        }
    }
}
