package xyz.mackan.Slabbo.GUI.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboItemAPI;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.Misc;
import xyz.mackan.Slabbo.utils.NameUtil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

public class GUIItems {
	private static SlabboItemAPI itemAPI = Bukkit.getServicesManager().getRegistration(SlabboItemAPI.class).getProvider();


	public static ItemStack getBuyPriceItem (double buyPrice) {
		ItemStack item = itemAPI.getGreenStainedGlassPane();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN+ LocaleManager.getString("general.general.buy-price"));

		String clickToSet = LocaleManager.getString("general.general.click-to-set");
		String explainer = LocaleManager.getString("general.general.not-for-sale-explain");

		String currencyString = LocaleManager.getCurrencyString(buyPrice);

		meta.setLore(Arrays.asList("§r"+currencyString, clickToSet, "§r"+explainer));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getSellPriceItem (double sellPrice) {
		ItemStack item = itemAPI.getRedStainedGlassPane();

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.RED+ LocaleManager.getString("general.general.sell-price"));

		String clickToSet = LocaleManager.getString("general.general.click-to-set");
		String explainer = LocaleManager.getString("general.general.not-buying-explain");

		String currencyString = LocaleManager.getCurrencyString(sellPrice);

		meta.setLore(Arrays.asList("§r"+currencyString, clickToSet, "§r"+explainer));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getAmountItem (int quantity) {
		ItemStack item = itemAPI.getYellowStainedGlassPane();

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.YELLOW+ LocaleManager.getString("general.general.quantity"));

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("count", quantity);

		String amountPerTransaction = LocaleManager.replaceKey("general.general.amount-per-transaction", replacementMap);
		String clickToSet = LocaleManager.getString("general.general.click-to-set");
		String explainer = LocaleManager.getString("general.general.quantity-explain");

		meta.setLore(Arrays.asList("§r"+amountPerTransaction, clickToSet, "§r"+explainer));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getCancelItem () {
		ItemStack item = itemAPI.getBarrier();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.RED + LocaleManager.getString("general.general.cancel"));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getConfirmItem (String locationString) {
		ItemStack item = itemAPI.getNetherStar();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN+LocaleManager.getString("general.general.confirm"));

		meta.setLore(Arrays.asList(LocaleManager.getString("general.general.new-shop"), locationString));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUserBuyItem (String itemName, int quantity, double price, int stock, boolean isAdmin, boolean isLimited) {
		ItemStack item = itemAPI.getGoldIngot();
		ItemMeta meta = item.getItemMeta();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("item", "'"+itemName+"'");
		replacementMap.put("quantity", quantity);

		String currencyString = LocaleManager.getCurrencyString(price);

		replacementMap.put("price", currencyString);

		if (isAdmin && !isLimited) {
			replacementMap.put("count", "∞");
		} else {
			replacementMap.put("count", stock);
		}

		String inStock = "";

		if (!isLimited) {
			inStock = LocaleManager.replaceKey("general.general.in-stock", replacementMap);
		} else {
			inStock = LocaleManager.replaceKey("general.general.limited-stock.buy-stock-left", replacementMap);
		}

		String stacks = LocaleManager.getString("general.general.stacks");

		String buyFor = LocaleManager.replaceKey("general.general.buy-for", replacementMap);

		meta.setDisplayName(ChatColor.GOLD+LocaleManager.replaceKey("gui.items.user.buy-item", replacementMap));

		if (isAdmin && !isLimited) {
			meta.setLore(Arrays.asList("§r"+buyFor, inStock, "(∞ "+stacks+")"));
		} else {
			meta.setLore(Arrays.asList("§r"+buyFor, inStock, "("+Misc.countStacks(stock)+" "+stacks+")"));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUserSellItem (String itemName, int quantity, double price, int stock, boolean isAdmin, boolean isLimited) {
		ItemStack item = itemAPI.getIronIngot();
		ItemMeta meta = item.getItemMeta();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("item", "'"+itemName+"'");
		replacementMap.put("quantity", quantity);

		String currencyString = LocaleManager.getCurrencyString(price);

		replacementMap.put("price", currencyString);

		if (isAdmin && !isLimited) {
			replacementMap.put("count", "∞");
		} else {
			replacementMap.put("count", stock);
		}

		String inStock = "";

		if (!isLimited) {
			inStock = LocaleManager.replaceKey("general.general.in-stock", replacementMap);
		} else {
			inStock = LocaleManager.replaceKey("general.general.limited-stock.sell-stock-left", replacementMap);
		}


		String stacks = LocaleManager.getString("general.general.stacks");

		String sellFor = LocaleManager.replaceKey("general.general.sell-for", replacementMap);

		meta.setDisplayName(ChatColor.GOLD+LocaleManager.replaceKey("gui.items.user.sell-item", replacementMap));

		if (isAdmin && !isLimited) {
			meta.setLore(Arrays.asList("§r"+sellFor, inStock, "(∞ "+stacks+")"));
		} else {
			meta.setLore(Arrays.asList("§r"+sellFor, inStock, "("+Misc.countStacks(stock)+" "+stacks+")"));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUserFundsItem (double funds) {
		NumberFormat formatter = new DecimalFormat("#0.00");

		ItemStack item = itemAPI.getPaper();
		ItemMeta meta = item.getItemMeta();

		// TODO: Think over whether we need to show what's essentially "current funds" twice

		meta.setDisplayName(ChatColor.GOLD+LocaleManager.getString("general.general.current-funds"));

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		//replacementMap.put("funds", "§a"+LocaleManager.getCurrencyString(formatter.format(funds)));

		replacementMap.put("funds", "§a"+LocaleManager.getCurrencyString(funds));


		meta.setLore(Arrays.asList(LocaleManager.replaceKey("general.general.funds-message", replacementMap)));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUserInfoItem (Shop shop) {
		ItemStack item = itemAPI.getCommandBlock();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD+LocaleManager.getString("general.user-info-item-name"));

		double buyPerItem = 0;
		double sellPerItem = 0;

		// TODO: Actually check if these are zero
		try {
			buyPerItem = shop.quantity / shop.buyPrice;
			if (Double.isInfinite(buyPerItem) || Double.isNaN(buyPerItem)) buyPerItem = 0;
		} catch (Exception e) {}

		try {
			sellPerItem = shop.quantity / shop.sellPrice;
			if (Double.isInfinite(sellPerItem) || Double.isNaN(sellPerItem)) sellPerItem = 0;
		} catch (Exception e) {}


		OfflinePlayer owner = Bukkit.getOfflinePlayer(shop.ownerId);

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		String ownerName = shop.displayedOwnerName != null ? shop.displayedOwnerName : Misc.getValueOrDefault(owner.getName(), LocaleManager.getString("general.general.unknown-user"));


		replacementMap.put("owner", ownerName);
		replacementMap.put("item", NameUtil.getName(shop.item));
		replacementMap.put("quantity", shop.quantity);

		replacementMap.put("buyPrice", LocaleManager.getCurrencyString(shop.buyPrice));
		replacementMap.put("sellPrice", LocaleManager.getCurrencyString(shop.sellPrice));


		replacementMap.put("buyPerItem", LocaleManager.getCurrencyString(buyPerItem));
		replacementMap.put("sellPerItem", LocaleManager.getCurrencyString(sellPerItem));

		if (shop.shopLimit != null && shop.shopLimit.enabled) {
			replacementMap.put("restockTime", shop.shopLimit.restockTime);
			replacementMap.put("sellStock", shop.shopLimit.sellStock);
			replacementMap.put("buyStock", shop.shopLimit.buyStock);

			String pattern = "MM-dd-yyyy HH:mm:ss";
			DateFormat df = new SimpleDateFormat(pattern);

			long nextRestock = shop.shopLimit.lastRestock + shop.shopLimit.restockTime;

			if (shop.shopLimit.lastRestock == 0) {
				long currentTime = Instant.now().getEpochSecond();

				nextRestock = currentTime + shop.shopLimit.restockTime;
			}

			Date nextRestockDate = new Date(nextRestock * 1000);

			replacementMap.put("time", df.format(nextRestockDate));
		}

		String ownerString = LocaleManager.replaceKey("gui.items.info.owned-by", replacementMap);
		String sellingString = LocaleManager.replaceKey("gui.items.info.selling-item", replacementMap);
		String buyEachString = LocaleManager.replaceKey("gui.items.info.buy-each", replacementMap);
		String sellEachString = LocaleManager.replaceKey("gui.items.info.sell-each", replacementMap);

		String sellStockString = LocaleManager.replaceKey("gui.items.info.limit.sell-stock", replacementMap);
		String buyStockString = LocaleManager.replaceKey("gui.items.info.limit.buy-stock", replacementMap);
		String restockTimeString = LocaleManager.replaceKey("gui.items.info.limit.restock-time", replacementMap);
		String nextRestockString = LocaleManager.replaceKey("gui.items.info.limit.next-restock", replacementMap);

		List<String> lore = new ArrayList<String>();

		lore.add("§r"+ownerString);
		lore.add("");
		lore.add("§r"+sellingString);
		lore.add(buyEachString);
		lore.add(sellEachString);

		if (shop.shopLimit != null && shop.shopLimit.enabled) {
			lore.add("§r§7-------------");
			lore.add(sellStockString);
			lore.add(buyStockString);
			lore.add(restockTimeString);
			lore.add(nextRestockString);
		}

		meta.setLore(lore);

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getDestroyConfirmItem () {
		ItemStack item = itemAPI.getLimeStainedGlassPane();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN+LocaleManager.getString("general.general.destroy-shop"));

		meta.setLore(Arrays.asList(
				ChatColor.RED+LocaleManager.getString("general.general.destroy-shop-item-warning")
		));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getSellersNoteItem (String sellerNote) {
		if (sellerNote == null || sellerNote.equalsIgnoreCase("")) {
			sellerNote = LocaleManager.getString("general.general.default-shop-note");
		}

		ItemStack item = itemAPI.getNameTag();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN+LocaleManager.getString("gui.items.user.sellers-note"));

		meta.setLore(Arrays.asList(
				sellerNote
		));

		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Returns a styled item for editing the buy stock in the limit admin GUI.
	 */
	public static ItemStack getBuyStockItem(int buyStock) {
		ItemStack item = itemAPI.getGreenStainedGlassPane();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + LocaleManager.getString("gui.limit-gui.buy-stock"));
		HashMap<String, Object> replacementMap = new HashMap<>();
		replacementMap.put("stock", buyStock);
		String stockString = LocaleManager.replaceKey("gui.limit-gui.stock-value", replacementMap);
		String clickToSet = LocaleManager.getString("general.general.click-to-set");
		meta.setLore(Arrays.asList("§r"+stockString, clickToSet));
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Returns a styled item for editing the sell stock in the limit admin GUI.
	 */
	public static ItemStack getSellStockItem(int sellStock) {
		ItemStack item = itemAPI.getRedStainedGlassPane();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + LocaleManager.getString("gui.limit-gui.sell-stock"));
		HashMap<String, Object> replacementMap = new HashMap<>();
		replacementMap.put("stock", sellStock);
		String stockString = LocaleManager.replaceKey("gui.limit-gui.stock-value", replacementMap);
		String clickToSet = LocaleManager.getString("general.general.click-to-set");
		meta.setLore(Arrays.asList("§r"+stockString, clickToSet));
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Returns a styled item for editing the restock time in the limit admin GUI.
	 */
	public static ItemStack getRestockTimeItem(int restockTime) {
		ItemStack item = itemAPI.getClock();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + LocaleManager.getString("gui.limit-gui.restock-time"));
		HashMap<String, Object> replacementMap = new HashMap<>();
		replacementMap.put("restockTime", restockTime);
		String timeString = LocaleManager.replaceKey("gui.limit-gui.time-value", replacementMap);
		String clickToSet = LocaleManager.getString("general.general.click-to-set");
		meta.setLore(Arrays.asList("§r"+timeString, clickToSet));
		item.setItemMeta(meta);
		return item;
	}
}
