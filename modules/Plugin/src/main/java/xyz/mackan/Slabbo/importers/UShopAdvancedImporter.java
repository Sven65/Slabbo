package xyz.mackan.Slabbo.importers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.types.Shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class UShopAdvancedImporter {

    public static ImportResult importUShops(File file) {
        List<String> skippedShops = new ArrayList<String>();
        List<Shop> shops = new ArrayList<Shop>();

        Yaml yaml = new Yaml(buildDumperOptions());

        Map<String, Object> root;
        try (FileInputStream fis = new FileInputStream(file)) {
            Object loaded = yaml.load(fis);
            if (!(loaded instanceof Map<?, ?>)) {
                Bukkit.getLogger().warning(LocaleManager.getString("error-message.import.ushops.file_root_not_mapping"));
                return new ImportResult(shops, skippedShops);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> tempRoot = (Map<String, Object>) loaded;
            root = tempRoot;
        } catch (YAMLException ye) {
            Bukkit.getLogger().severe(LocaleManager.replaceSingleKey("error-message.import.ushops.malformed_yaml", "error", ye.getMessage()));
            ye.printStackTrace();
            return new ImportResult(shops, skippedShops);
        } catch (IOException ioe) {
            Bukkit.getLogger().severe(LocaleManager.replaceSingleKey("error-message.import.ushops.failed_read_file", "error", ioe.getMessage()));
            ioe.printStackTrace();
            return new ImportResult(shops, skippedShops);
        } catch (Exception ex) {
            Bukkit.getLogger().severe(LocaleManager.replaceSingleKey("error-message.import.ushops.unexpected_error", "error", ex.getMessage()));
            ex.printStackTrace();
            return new ImportResult(shops, skippedShops);
        }

        for (Map.Entry<String, Object> entry : root.entrySet()) {
            String key = entry.getKey().trim();

            if (key.isEmpty() || containsInvisibleCharacters(key)) {
                skippedShops.add(key);
                Bukkit.getLogger().warning(LocaleManager.replaceSingleKey("error-message.import.ushops.skipping_blank_key", "key", key));
                continue;
            }

            try {
                if (Slabbo.getInstance().getShopManager().getShop(key) != null) {
                    skippedShops.add(key);
                    continue;
                }

                Object rawSection = entry.getValue();
                if (!(rawSection instanceof Map<?, ?>)) {
                    skippedShops.add(key);
                    Bukkit.getLogger().warning(LocaleManager.replaceSingleKey("error-message.import.ushops.skipping_non_map_section", "key", key));
                    continue;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> section = (Map<String, Object>) rawSection;

                String shopOwnerId = stringOrNull(section.get("host"));
                UUID shopOwnerUUID = (shopOwnerId == null) ? null : UUID.fromString(shopOwnerId);

                ItemStack itemStack = buildItemStackSafely(section.get("item"));
                if (itemStack == null) {
                    itemStack = new ItemStack(Material.BARRIER);
                    Bukkit.getLogger().warning(LocaleManager.replaceSingleKey("error-message.import.ushops.itemstack_failed", "key", key));
                }

                int buyPrice = intOrDefault(section.get("buyPrice"), 0);
                int sellPrice = intOrDefault(section.get("sellPrice"), 0);
                int quantity = intOrDefault(section.get("stack"), 1);
                int stock = intOrDefault(section.get("amount"), 0);
                boolean isAdmin = boolOrDefault(section.get("admin"), false);
                if (isAdmin) stock = 0;

                Location shopLocation = ShopManager.fromString(key);
                if (shopLocation == null) {
                    Bukkit.getLogger().warning(LocaleManager.replaceSingleKey("error-message.import.ushops.null_location", "key", key));
                }

                Shop shop = new Shop(buyPrice, sellPrice, quantity, shopLocation, itemStack, stock, shopOwnerUUID, isAdmin, null);
                shops.add(shop);

            } catch (Exception e) {
                Bukkit.getLogger().severe(LocaleManager.replaceKey("error-message.import.ushops.import_failed", new HashMap<String, Object>() {{
                    put("key", key);
                    put("error", e.getMessage());
                }}));
                e.printStackTrace();
            }
        }

        return new ImportResult(shops, skippedShops);
    }

    // ----- helpers -----

    private static DumperOptions buildDumperOptions() {
        DumperOptions opts = new DumperOptions();
        opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        opts.setPrettyFlow(true);
        return opts;
    }

    private static String stringOrNull(Object o) {
        return (o == null) ? null : o.toString();
    }

    private static int intOrDefault(Object o, int def) {
        try {
            if (o instanceof Number) return ((Number) o).intValue();
            if (o instanceof String) return Integer.parseInt((String) o);
        } catch (Exception ignored) {}
        return def;
    }

    private static boolean boolOrDefault(Object o, boolean def) {
        if (o instanceof Boolean) return (Boolean) o;
        if (o instanceof String) return Boolean.parseBoolean((String) o);
        return def;
    }

    private static ItemStack buildItemStackSafely(Object rawItem) {
        if (rawItem == null) return null;
        if (rawItem instanceof ItemStack) return (ItemStack) rawItem;
        if (!(rawItem instanceof Map<?, ?>)) return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> itemMap = (Map<String, Object>) rawItem;

        String typeName = null;
        if (itemMap.containsKey("type")) typeName = stringOrNull(itemMap.get("type"));
        if (typeName == null && itemMap.containsKey("material")) typeName = stringOrNull(itemMap.get("material"));

        Material mat = null;
        if (typeName != null) {
            try {
                mat = Material.valueOf(typeName.toUpperCase(Locale.ENGLISH));
            } catch (Exception ignored) {}
        }

        int amount = intOrDefault(itemMap.get("amount"), intOrDefault(itemMap.get("count"), 1));
        int maxStack = (mat != null) ? mat.getMaxStackSize() : 64;

        if (amount < 1) {
            amount = 1;
            Bukkit.getLogger().warning(LocaleManager.replaceSingleKey("error-message.import.ushops.itemstack_invalid_amount_low", "type", typeName));
        } else if (amount > maxStack) {
            amount = maxStack;
            HashMap<String, Object> placeholders = new HashMap<String, Object>();
            placeholders.put("type", typeName);
            placeholders.put("amount", amount);
            placeholders.put("max", maxStack);
            Bukkit.getLogger().warning(LocaleManager.replaceKey("error-message.import.ushops.itemstack_invalid_amount_high", placeholders));
        }

        if (mat == null) return null;

        ItemStack item = new ItemStack(mat, amount);

        Object metaObj = itemMap.get("meta");
        if (metaObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> metaMap = (Map<String, Object>) metaObj;
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (metaMap.containsKey("display-name")) {
                    String display = stringOrNull(metaMap.get("display-name"));
                    if (display != null) meta.setDisplayName(display);
                }
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    private static boolean containsInvisibleCharacters(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isISOControl(c) || (Character.isWhitespace(c) && c != ' ')) return true;
        }
        return false;
    }
}
