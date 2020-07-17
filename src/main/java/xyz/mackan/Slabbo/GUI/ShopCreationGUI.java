package xyz.mackan.Slabbo.GUI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.mackan.Slabbo.GUI.items.GUIItems;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.Arrays;
import java.util.UUID;

public class ShopCreationGUI implements Listener {
	private Inventory inv;
	private Location slabLocation;

	private ItemStack shopItem = null;

	private ChatWaitingType waitingType;
	private UUID waitingPlayerId;

	private int buyPrice = 0;
	private int sellPrice = 0;
	private int quantity = 0;
	private int stock = 0;

	private boolean isModifying = false;
	private boolean isAdmin = false;

	public ShopCreationGUI (Location slabLocation, Shop shop) {
		isModifying = true;
		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] "+Slabbo.localeManager.getString("gui.editing-shop"));

		this.slabLocation = slabLocation;

		shopItem = shop.item;

		buyPrice = shop.buyPrice;
		sellPrice = shop.sellPrice;
		quantity = shop.quantity;

		stock = shop.stock;

		isAdmin = shop.admin;

		initializeStage2();
	}

	public ShopCreationGUI (Location slabLocation) {
		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] "+Slabbo.localeManager.getString("general.general.new-shop"));

		this.slabLocation = slabLocation;

		initializeItems();
	}

	public void resetGUI () {
		inv = null;
		slabLocation = null;
		shopItem = null;
		waitingType = ChatWaitingType.NONE;
		buyPrice = 0;
		sellPrice = 0;
		quantity = 0;
		stock = 0;
	}

	public boolean getIsStage2 () {
		return shopItem != null;
	}

	public void clearShopInv () {
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, null);
		}
	}

	public void initializeItems() {
		ItemStack creationItem = createGuiItem();

		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, creationItem);
		}
	}

	public void initializeStage2 () {
		clearShopInv();

		inv.setItem(0, shopItem);


		inv.setItem(3, GUIItems.getBuyPriceItem(buyPrice));
		inv.setItem(4, GUIItems.getSellPriceItem(sellPrice));
		inv.setItem(5, GUIItems.getAmountItem(quantity));

		inv.setItem(7, GUIItems.getConfirmItem(ShopUtil.locationToString(slabLocation)));
		inv.setItem(8, GUIItems.getCancelItem());
	}



	protected ItemStack createGuiItem () {
		ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.RED+Slabbo.localeManager.getString("gui.items.new-shop.click-item-below"));

		meta.setLore(Arrays.asList(Slabbo.localeManager.getString("general.general.new-shop"), ShopUtil.locationToString(slabLocation)));

		item.setItemMeta(meta);

		return item;
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if (e.getInventory() != inv) return;
		e.setCancelled(true);

		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

		Player p = (Player) e.getWhoClicked();

		int slot = e.getRawSlot();

		if (getIsStage2()) {
			if (slot <= 8) {
				// User's clicked shop GUI

				// TODO: Move this to a switch
				if (slot == 3) {
					// Buy Price
					waitingType = ChatWaitingType.BUY_PRICE;
					waitingPlayerId = e.getWhoClicked().getUniqueId();
					p.sendMessage(Slabbo.localeManager.getString("general.general.type-new-buy-price"));
					e.getWhoClicked().closeInventory();

				} else if (slot == 4) {
					//Sell Price
					waitingType = ChatWaitingType.SELL_PRICE;
					waitingPlayerId = e.getWhoClicked().getUniqueId();
					p.sendMessage(Slabbo.localeManager.getString("general.general.type-new-sell-price"));
					e.getWhoClicked().closeInventory();

				} else if (slot == 5) {
					// Amount
					waitingType = ChatWaitingType.QUANTITY;
					waitingPlayerId = e.getWhoClicked().getUniqueId();
					p.sendMessage(Slabbo.localeManager.getString("general.general.type-new-quantity"));
					e.getWhoClicked().closeInventory();
				} else if (slot == 7) {
					// Confirm
					Shop shop = new Shop(buyPrice, sellPrice, quantity, slabLocation, shopItem);

					shop.ownerId = e.getWhoClicked().getUniqueId();

					shop.admin = isAdmin;
					shop.stock = stock;


					Slabbo.shopUtil.put(ShopUtil.locationToString(slabLocation), shop);

					DataUtil.saveShops();

					e.getWhoClicked().closeInventory();

					if (isModifying) {
						Item itemEnt = ItemUtil.findShopItem(slabLocation);

						if (itemEnt != null) {
							itemEnt.remove();
						}


						ItemUtil.dropShopItem(slabLocation, shopItem);
					} else {
						ItemUtil.dropShopItem(slabLocation, shopItem);
					}


					resetGUI();

				} else if (slot == 8) {
					// Cancel

					resetGUI();
					e.getWhoClicked().closeInventory();
				}

				return;
			}
		}

		if (slot <= 8) return; // User clicked shop GUI

		shopItem = clickedItem.clone();

		shopItem.setAmount(1);

		initializeStage2();
	}

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat (final AsyncPlayerChatEvent e) {
		if (!e.getPlayer().getUniqueId().equals(waitingPlayerId)) return;

		if (waitingType == ChatWaitingType.NONE) return;

		e.setCancelled(true);

		int value = Integer.parseInt(e.getMessage());

		if (value < -1) { value = -1; }

		switch (waitingType) {
			case SELL_PRICE:
				sellPrice = value;
				break;
			case BUY_PRICE:
				buyPrice = value;
				break;
			case QUANTITY:
				if (value <= -1) {
					value = 0;
				}

				quantity = value;
				break;
		}

		waitingType = ChatWaitingType.NONE;
		waitingPlayerId = null;

		new BukkitRunnable() {
			public void run () {
				openInventory(e.getPlayer());
				initializeStage2();
			}
		}.runTask(Slabbo.getInstance());
	}
}
