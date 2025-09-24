package xyz.mackan.Slabbo.GUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import xyz.mackan.Slabbo.GUI.items.GUIItems;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.types.ChatWaitingType;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.types.ShopLimit;

public class LimitAdminGUI implements Listener {
    private final Shop shop;
    private final Player player;
    private final Inventory inv;
    private int buyStock;
    private int sellStock;
    private int restockTime;
    private ChatWaitingType waitingType = ChatWaitingType.NONE;

    ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();


    public LimitAdminGUI(Shop shop, Player player) {
        this.shop = shop;
        this.player = player;
        ShopLimit limit = shop.shopLimit;
        this.buyStock = (limit != null) ? limit.buyStock : 0;
        this.sellStock = (limit != null) ? limit.sellStock : 0;
        this.restockTime = (limit != null) ? limit.restockTime : 0;
        this.inv = Bukkit.createInventory(null, 9, LocaleManager.getString("general.shop-prefix") + LocaleManager.getString("gui.limit-gui.title"));
        Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());
        initializeItems();
    }


    private void initializeItems() {
        inv.setItem(0, GUIItems.getBuyStockItem(buyStock));
        inv.setItem(1, GUIItems.getSellStockItem(sellStock));
        inv.setItem(2, GUIItems.getRestockTimeItem(restockTime));
        inv.setItem(7, GUIItems.getConfirmItem(shop.getLocationString()));
        inv.setItem(8, GUIItems.getCancelItem());
    }

    public void openInventory(HumanEntity ent) {
        ent.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        e.setCancelled(true);
        int slot = e.getRawSlot();

        switch (slot) {
            case 0:
                waitingType = ChatWaitingType.LIMIT_BUY_STOCK;
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + LocaleManager.getString("gui.limit-gui.enter-buy-stock"));
                player.playSound(this.shop.location == null ? player.getLocation() : this.shop.location, slabboSound.getSoundByKey("QUESTION"), 1, 1);

                break;
            case 1:
                waitingType = ChatWaitingType.LIMIT_SELL_STOCK;
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + LocaleManager.getString("gui.limit-gui.enter-sell-stock"));
                player.playSound(this.shop.location == null ? player.getLocation() : this.shop.location, slabboSound.getSoundByKey("QUESTION"), 1, 1);

                break;
            case 2:
                waitingType = ChatWaitingType.LIMIT_RESTOCK_TIME;
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + LocaleManager.getString("gui.limit-gui.enter-restock-time"));
                player.playSound(this.shop.location == null ? player.getLocation() : this.shop.location, slabboSound.getSoundByKey("QUESTION"), 1, 1);

                break;
            case 7:
                saveAndClose(e.getWhoClicked());
                player.playSound(this.shop.location == null ? player.getLocation() : this.shop.location, slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);

                break;
            case 8:
                e.getWhoClicked().closeInventory();
                player.playSound(this.shop.location == null ? player.getLocation() : this.shop.location, slabboSound.getSoundByKey("CANCEL"), 1, 1);

                break;
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (!e.getPlayer().equals(player) || waitingType == ChatWaitingType.NONE) return;
        e.setCancelled(true);
        String msg = e.getMessage();
        int value;
        try {
            value = Integer.parseInt(msg);
            if (value < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.general.invalid-number"));
            reopen();
            waitingType = ChatWaitingType.NONE;
            return;
        }
        switch (waitingType) {
            case LIMIT_BUY_STOCK:
                buyStock = value;
                break;
            case LIMIT_SELL_STOCK:
                sellStock = value;
                break;
            case LIMIT_RESTOCK_TIME:
                restockTime = value;
                break;
        }
        waitingType = ChatWaitingType.NONE;

        reopen();
    }

    private void reopen() {
        Bukkit.getScheduler().runTask(Slabbo.getInstance(), () -> {
            initializeItems();
            player.openInventory(inv);
            player.playSound(shop.location == null ? player.getLocation() : shop.location, slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);

        });
    }

    private void saveAndClose(HumanEntity ent) {
        if (shop.shopLimit == null) shop.shopLimit = new ShopLimit(0, 0, 0, 0L, true);
        shop.shopLimit.buyStock = buyStock;
        shop.shopLimit.sellStock = sellStock;
        shop.shopLimit.restockTime = restockTime;
        Slabbo.getInstance().getShopManager().updateShop(shop);
        ent.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.general.limited-stock.updated"));
        ent.closeInventory();
    }
}
