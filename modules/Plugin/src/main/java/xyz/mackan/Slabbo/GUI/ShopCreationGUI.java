package xyz.mackan.Slabbo.GUI;

import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
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
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.abstractions.SlabboItemAPI;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.types.ChatWaitingType;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.manager.ShopManager;

import java.util.Arrays;
import java.util.UUID;

public class ShopCreationGUI implements Listener {
	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();

	private Inventory inv;
	private Location slabLocation;

	private ItemStack shopItem = null;

	private ChatWaitingType waitingType;
	private UUID waitingPlayerId;

	private int buyPrice = 0;
	private int sellPrice = 0;
	private int quantity = 0;
	private int stock = 0;

	private String sellersNote;

	private boolean isModifying = false;
	private boolean isAdmin = false;

	public ShopCreationGUI (Location slabLocation, Shop shop) {
		isModifying = true;
		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] "+ LocaleManager.getString("gui.editing-shop"));

		this.slabLocation = slabLocation;

		shopItem = shop.item;

		buyPrice = shop.buyPrice;
		sellPrice = shop.sellPrice;
		quantity = shop.quantity;

		stock = shop.stock;

		isAdmin = shop.admin;

		sellersNote = shop.note;

		initializeStage2();
	}

	public ShopCreationGUI (Location slabLocation) {
		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] "+LocaleManager.getString("general.general.new-shop"));

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

		sellersNote = "";
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
		inv.setItem(1, GUIItems.getSellersNoteItem(sellersNote));


		inv.setItem(3, GUIItems.getBuyPriceItem(buyPrice));
		inv.setItem(4, GUIItems.getSellPriceItem(sellPrice));
		inv.setItem(5, GUIItems.getAmountItem(quantity));

		inv.setItem(7, GUIItems.getConfirmItem(ShopManager.locationToString(slabLocation)));
		inv.setItem(8, GUIItems.getCancelItem());
	}



	protected ItemStack createGuiItem () {
		SlabboItemAPI api = Bukkit.getServicesManager().getRegistration(SlabboItemAPI.class).getProvider();
		ItemStack item = api.getRedStainedGlassPane();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.RED+LocaleManager.getString("gui.items.new-shop.click-item-below"));

		meta.setLore(Arrays.asList(LocaleManager.getString("general.general.new-shop"), ShopManager.locationToString(slabLocation)));

		item.setItemMeta(meta);

		return item;
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if (!e.getInventory().equals(inv)) return;
		e.setCancelled(true);

		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

		Player p = (Player) e.getWhoClicked();

		int slot = e.getRawSlot();

		if (getIsStage2()) {
			if (slot <= 8) {
				// User's clicked shop GUI

				// TODO: Move this to a switch
				if (slot == 1) {
					// Sellers note
					waitingType = ChatWaitingType.SELLERS_NOTE;

					waitingPlayerId = e.getWhoClicked().getUniqueId();

					p.sendMessage(LocaleManager.getString("general.general.type-new-note"));
					e.getWhoClicked().closeInventory();

					p.playSound(this.slabLocation, slabboSound.getSoundByKey("QUESTION"), 1, 1);
				} else if (slot == 3) {
					// Buy Price
					waitingType = ChatWaitingType.BUY_PRICE;
					waitingPlayerId = e.getWhoClicked().getUniqueId();
					p.sendMessage(LocaleManager.getString("general.general.type-new-buy-price"));
					e.getWhoClicked().closeInventory();

					p.playSound(this.slabLocation, slabboSound.getSoundByKey("QUESTION"), 1, 1);
				} else if (slot == 4) {
					//Sell Price
					waitingType = ChatWaitingType.SELL_PRICE;
					waitingPlayerId = e.getWhoClicked().getUniqueId();
					p.sendMessage(LocaleManager.getString("general.general.type-new-sell-price"));
					e.getWhoClicked().closeInventory();

					p.playSound(this.slabLocation, slabboSound.getSoundByKey("QUESTION"), 1, 1);
				} else if (slot == 5) {
					// Amount
					waitingType = ChatWaitingType.QUANTITY;
					waitingPlayerId = e.getWhoClicked().getUniqueId();
					p.sendMessage(LocaleManager.getString("general.general.type-new-quantity"));
					e.getWhoClicked().closeInventory();

					p.playSound(this.slabLocation, slabboSound.getSoundByKey("QUESTION"), 1, 1);
				} else if (slot == 7) {
					// Confirm
					Shop shop = new Shop(buyPrice, sellPrice, quantity, slabLocation, shopItem);

					shop.ownerId = e.getWhoClicked().getUniqueId();

					shop.admin = isAdmin;
					shop.stock = stock;

					shop.note = sellersNote;


					ShopManager.put(ShopManager.locationToString(slabLocation), shop);

					DataUtil.saveShops();

					e.getWhoClicked().closeInventory();

					if (isModifying) {
						ItemUtil.removeShopItemsAtLocation(slabLocation);
					}

					ItemUtil.dropShopItem(slabLocation, shopItem, quantity);

					p.playSound(this.slabLocation, slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);

					resetGUI();

				} else if (slot == 8) {
					// Cancel

					p.playSound(this.slabLocation, slabboSound.getSoundByKey("CANCEL"), 1, 1);

					resetGUI();
					e.getWhoClicked().closeInventory();
				}

				return;
			}
		}


		if (slot <= 8) return; // User clicked shop GUI

		p.playSound(this.slabLocation, slabboSound.getSoundByKey("NAVIGATION"), 1, 1);

		//p.playSound(this.slabLocation, SlabboSound.NAVIGATION.sound, SoundCategory.BLOCKS, 1, 1);

		shopItem = clickedItem.clone();

		shopItem.setAmount(1);

		initializeStage2();
	}

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory().equals(inv)) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat (final AsyncPlayerChatEvent e) {
		if (!e.getPlayer().getUniqueId().equals(waitingPlayerId)) return;

		if (waitingType == ChatWaitingType.NONE) return;

		e.setCancelled(true);

		int value = 0;

		String shopNoteInput = "";

		if (waitingType != ChatWaitingType.SELLERS_NOTE) {
			try {
				value = Integer.parseInt(e.getMessage());
			} catch (NumberFormatException error) {
				e.getPlayer().sendMessage(ChatColor.RED + LocaleManager.getString("error-message.modify.not-a-valid-number"));
			}

			if (value < -1) {
				value = -1;
			}
		} else {
			shopNoteInput = e.getMessage();
		}

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
			case SELLERS_NOTE:
				if (shopNoteInput.equalsIgnoreCase("#")) {
					if (sellersNote != null && !sellersNote.equalsIgnoreCase("")) {
						shopNoteInput = sellersNote;
					} else {
						shopNoteInput = null;
					}
				}

				sellersNote = shopNoteInput;
				break;
		}

		waitingType = ChatWaitingType.NONE;
		waitingPlayerId = null;

		new BukkitRunnable() {
			public void run () {
				e.getPlayer().playSound(slabLocation, slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
				openInventory(e.getPlayer());
				initializeStage2();
			}
		}.runTask(Slabbo.getInstance());
	}
}
