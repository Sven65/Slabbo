package xyz.mackan.Slabbo.data;

import org.bukkit.Bukkit;
import xyz.mackan.Slabbo.types.Shop;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.mackan.Slabbo.Slabbo;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;
import java.util.*;

public class SQLiteStore implements DataStore {
    private static final String DB_URL = "jdbc:sqlite:" + Slabbo.getDataPath() + "/shops.db";

    public SQLiteStore() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS shops (" +
                "id TEXT PRIMARY KEY, " +
                "owner TEXT, " +
                "admin INTEGER, " +
                "virtual INTEGER, " +
                "shop_name TEXT, " +
                "location TEXT, " +
                "price_buy REAL, " +
                "price_sell REAL, " +
                "quantity INTEGER, " +
                "stock INTEGER, " +
                "note TEXT, " +
                "linked_chest TEXT, " +
                "displayed_owner TEXT, " +
                "item_data TEXT, " +
                "shop_limit TEXT, " +
                "command_list TEXT, " +
                "custom_item_display_name TEXT, " +
                "item_display_name_toggle INTEGER, " +
                "shop_tax_rate TEXT, " +
                "shop_tax_mode TEXT" +
                ")"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private String serializeYaml(Object obj) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("data", obj);
        StringWriter writer = new StringWriter();
        try {
            writer.write(yaml.saveToString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }


    private <T> T deserializeYaml(String yamlString, Class<T> clazz) {
        if (yamlString == null) return null;
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(new StringReader(yamlString));
            Object obj = yaml.get("data");
            if (clazz.isInstance(obj)) {
                return clazz.cast(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Shop> loadShops() {
        Map<String, Shop> shops = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM shops");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString("id");
                double buyPrice = rs.getDouble("price_buy");
                double sellPrice = rs.getDouble("price_sell");
                int quantity = rs.getInt("quantity");
                int stock = rs.getInt("stock");
                String ownerStr = rs.getString("owner");
                UUID ownerId = ownerStr != null ? UUID.fromString(ownerStr) : null;
                boolean admin = rs.getInt("admin") == 1;
                boolean virtual = rs.getInt("virtual") == 1;
                String shopName = rs.getString("shop_name");
                String note = rs.getString("note");
                String linkedChest = rs.getString("linked_chest");
                String displayedOwner = rs.getString("displayed_owner");

                // Complex fields
                String itemData = rs.getString("item_data");
                String shopLimitData = rs.getString("shop_limit");
                String commandListData = rs.getString("command_list");
                String locationData = rs.getString("location");

                org.bukkit.Location location = deserializeYaml(locationData, org.bukkit.Location.class);
                org.bukkit.inventory.ItemStack item = deserializeYaml(itemData, org.bukkit.inventory.ItemStack.class);

                xyz.mackan.Slabbo.types.ShopLimit shopLimit = deserializeYaml(shopLimitData, xyz.mackan.Slabbo.types.ShopLimit.class);
                xyz.mackan.Slabbo.types.Shop.CommandList commandList = deserializeYaml(commandListData, xyz.mackan.Slabbo.types.Shop.CommandList.class);

                String shopTaxRate = rs.getString("shop_tax_rate");
                String shopTaxMode = rs.getString("shop_tax_mode");

                Shop shop = new Shop(buyPrice, sellPrice, quantity, location, item, stock, ownerId, admin, linkedChest, virtual, shopName);
                shop.note = note;
                shop.displayedOwnerName = displayedOwner;
                shop.shopLimit = shopLimit;
                shop.commandList = commandList;
                shop.shopTaxRate = shopTaxRate;
                shop.shopTaxMode = shopTaxMode;

                shop.customItemDisplayName = rs.getString("custom_item_display_name");
                shop.itemDisplayNameToggle = rs.getInt("item_display_name_toggle") == 1;

                shops.put(id, shop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shops;
    }

    @Override
    public void saveShops(Map<String, Shop> shops) {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            stmt.executeUpdate("DELETE FROM shops");
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO shops (id, owner, admin, virtual, shop_name, location, price_buy, price_sell, quantity, stock, note, linked_chest, displayed_owner, item_data, shop_limit, command_list, custom_item_display_name, item_display_name_toggle, shop_tax_rate, shop_tax_mode) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
            ) {
                for (Map.Entry<String, Shop> entry : shops.entrySet()) {
                    Shop shop = entry.getValue();
                    ps.setString(1, entry.getKey());
                    ps.setString(2, shop.ownerId != null ? shop.ownerId.toString() : null);
                    ps.setInt(3, shop.admin ? 1 : 0);
                    ps.setInt(4, shop.virtual ? 1 : 0);
                    ps.setString(5, shop.shopName);
                    ps.setString(6, serializeYaml(shop.location));
                    ps.setDouble(7, shop.buyPrice);
                    ps.setDouble(8, shop.sellPrice);
                    ps.setInt(9, shop.quantity);
                    ps.setInt(10, shop.stock);
                    ps.setString(11, shop.note);
                    ps.setString(12, shop.linkedChestLocation);
                    ps.setString(13, shop.displayedOwnerName);
                    ps.setString(14, serializeYaml(shop.item));
                    ps.setString(15, serializeYaml(shop.shopLimit));
                    ps.setString(16, serializeYaml(shop.commandList));
                    ps.setString(17, shop.customItemDisplayName);
                    ps.setInt(18, shop.itemDisplayNameToggle ? 1 : 0);
                    ps.setString(19, shop.shopTaxRate);
                    ps.setString(20, shop.shopTaxMode);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addShop(Shop shop) {
        try (Connection conn = getConnection();

             PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR REPLACE INTO shops (id, owner, admin, virtual, shop_name, location, price_buy, price_sell, quantity, stock, note, linked_chest, displayed_owner, item_data, shop_limit, command_list, custom_item_display_name, item_display_name_toggle, shop_tax_rate, shop_tax_mode) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
        ) {
            ps.setString(1, shop.getLocationString());
            ps.setString(2, shop.ownerId != null ? shop.ownerId.toString() : null);
            ps.setInt(3, shop.admin ? 1 : 0);
            ps.setInt(4, shop.virtual ? 1 : 0);
            ps.setString(5, shop.shopName);
            ps.setString(6, serializeYaml(shop.location));
            ps.setDouble(7, shop.buyPrice);
            ps.setDouble(8, shop.sellPrice);
            ps.setInt(9, shop.quantity);
            ps.setInt(10, shop.stock);
            ps.setString(11, shop.note);
            ps.setString(12, shop.linkedChestLocation);
            ps.setString(13, shop.displayedOwnerName);
            ps.setString(14, serializeYaml(shop.item));
            ps.setString(15, serializeYaml(shop.shopLimit));
            ps.setString(16, serializeYaml(shop.commandList));
            ps.setString(17, shop.customItemDisplayName);
            ps.setInt(18, shop.itemDisplayNameToggle ? 1 : 0);
            ps.setString(19, shop.shopTaxRate);
            ps.setString(20, shop.shopTaxMode);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding shop at " + shop.getLocationString() + ": " + e.getMessage());
        }
    }

    @Override
    public void updateShop(Shop shop) {
        addShop(shop);
    }

    @Override
    public void removeShop(String shopId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM shops WHERE id = ?")) {
            ps.setString(1, shopId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Shop getShop(String shopId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM shops WHERE id = ?")) {
            ps.setString(1, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double buyPrice = rs.getDouble("price_buy");
                    double sellPrice = rs.getDouble("price_sell");
                    int quantity = rs.getInt("quantity");
                    int stock = rs.getInt("stock");
                    String ownerStr = rs.getString("owner");
                    UUID ownerId = ownerStr != null ? UUID.fromString(ownerStr) : null;
                    boolean admin = rs.getInt("admin") == 1;
                    boolean virtual = rs.getInt("virtual") == 1;
                    String shopName = rs.getString("shop_name");
                    String note = rs.getString("note");
                    String linkedChest = rs.getString("linked_chest");
                    String displayedOwner = rs.getString("displayed_owner");

                    String itemData = rs.getString("item_data");
                    String shopLimitData = rs.getString("shop_limit");
                    String commandListData = rs.getString("command_list");
                    String locationData = rs.getString("location");

                    org.bukkit.Location location = deserializeYaml(locationData, org.bukkit.Location.class);
                    org.bukkit.inventory.ItemStack item = deserializeYaml(itemData, org.bukkit.inventory.ItemStack.class);
                    xyz.mackan.Slabbo.types.ShopLimit shopLimit = deserializeYaml(shopLimitData, xyz.mackan.Slabbo.types.ShopLimit.class);
                    xyz.mackan.Slabbo.types.Shop.CommandList commandList = deserializeYaml(commandListData, xyz.mackan.Slabbo.types.Shop.CommandList.class);

                    Shop shop = new Shop(buyPrice, sellPrice, quantity, location, item, stock, ownerId, admin, linkedChest, virtual, shopName);
                    shop.note = note;
                    shop.displayedOwnerName = displayedOwner;
                    shop.shopLimit = shopLimit;
                    shop.commandList = commandList;

                    // New fields
                    shop.customItemDisplayName = rs.getString("custom_item_display_name");
                    shop.itemDisplayNameToggle = rs.getInt("item_display_name_toggle") == 1;
                    shop.shopTaxRate = rs.getString("shop_tax_rate");
                    shop.shopTaxMode = rs.getString("shop_tax_mode");

                    return shop;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Shop> getShopsByOwner(UUID owner) {
        List<Shop> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM shops WHERE owner = ?")) {
            ps.setString(1, owner.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double buyPrice = rs.getDouble("price_buy");
                    double sellPrice = rs.getDouble("price_sell");
                    int quantity = rs.getInt("quantity");
                    int stock = rs.getInt("stock");
                    String ownerStr = rs.getString("owner");
                    UUID ownerId = ownerStr != null ? UUID.fromString(ownerStr) : null;
                    boolean admin = rs.getInt("admin") == 1;
                    boolean virtual = rs.getInt("virtual") == 1;
                    String shopName = rs.getString("shop_name");
                    String note = rs.getString("note");
                    String linkedChest = rs.getString("linked_chest");
                    String displayedOwner = rs.getString("displayed_owner");

                    String itemData = rs.getString("item_data");
                    String shopLimitData = rs.getString("shop_limit");
                    String commandListData = rs.getString("command_list");
                    String locationData = rs.getString("location");

                    org.bukkit.Location location = deserializeYaml(locationData, org.bukkit.Location.class);
                    org.bukkit.inventory.ItemStack item = deserializeYaml(itemData, org.bukkit.inventory.ItemStack.class);
                    xyz.mackan.Slabbo.types.ShopLimit shopLimit = deserializeYaml(shopLimitData, xyz.mackan.Slabbo.types.ShopLimit.class);
                    xyz.mackan.Slabbo.types.Shop.CommandList commandList = deserializeYaml(commandListData, xyz.mackan.Slabbo.types.Shop.CommandList.class);

                    Shop shop = new Shop(buyPrice, sellPrice, quantity, location, item, stock, ownerId, admin, linkedChest, virtual, shopName);
                    shop.note = note;
                    shop.displayedOwnerName = displayedOwner;
                    shop.shopLimit = shopLimit;
                    shop.commandList = commandList;

                    shop.customItemDisplayName = rs.getString("custom_item_display_name");
                    shop.itemDisplayNameToggle = rs.getInt("item_display_name_toggle") == 1;
                    shop.shopTaxRate = rs.getString("shop_tax_rate");
                    shop.shopTaxMode = rs.getString("shop_tax_mode");

                    result.add(shop);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void saveShopsOnMainThread(Map<String, Shop> shops) {
        saveShops(shops);
    }

    @Override
    public void close() {
        // No resources to close for SQLite with per-operation connections
    }

    @Override
    public boolean requiresCache() {
        return false;
    }

    @Override
    public String getStorageType() {
        return "sqlite";
    }
}
