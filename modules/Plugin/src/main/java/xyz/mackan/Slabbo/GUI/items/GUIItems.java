package xyz.mackan.Slabbo.GUI.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboItemAPI;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.Misc;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class GUIItems {
	private static SlabboItemAPI itemAPI = Bukkit.getServicesManager().getRegistration(SlabboItemAPI.class).getProvider();


	public static ItemStack getBuyPriceItem (int buyPrice) {
		ItemStack item = itemAPI.getGreenStainedGlassPane();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN+ Slabbo.localeManager.getString("general.general.buy-price"));

		String clickToSet = Slabbo.localeManager.getString("general.general.click-to-set");
		String explainer = Slabbo.localeManager.getString("general.general.not-for-sale-explain");

		String currencyString = Slabbo.localeManager.getCurrencyString(buyPrice);

		meta.setLore(Arrays.asList("§r"+currencyString, clickToSet, "§r"+explainer));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getSellPriceItem (int sellPrice) {
		ItemStack item = itemAPI.getRedStainedGlassPane();

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.RED+ Slabbo.localeManager.getString("general.general.sell-price"));

		String clickToSet = Slabbo.localeManager.getString("general.general.click-to-set");
		String explainer = Slabbo.localeManager.getString("general.general.not-buying-explain");

		String currencyString = Slabbo.localeManager.getCurrencyString(sellPrice);


		meta.setLore(Arrays.asList("§r"+currencyString, clickToSet, "§r"+explainer));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getAmountItem (int quantity) {
		ItemStack item = itemAPI.getYellowStainedGlassPane();

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.YELLOW+ Slabbo.localeManager.getString("general.general.quantity"));

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("count", quantity);

		String amountPerTransaction = Slabbo.localeManager.replaceKey("general.general.amount-per-transaction", replacementMap);
		String clickToSet = Slabbo.localeManager.getString("general.general.click-to-set");
		String explainer = Slabbo.localeManager.getString("general.general.quantity-explain");

		meta.setLore(Arrays.asList("§r"+amountPerTransaction, clickToSet, "§r"+explainer));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getCancelItem () {
		ItemStack item = new ItemStack(Material.BARRIER, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.RED + Slabbo.localeManager.getString("general.general.cancel"));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getConfirmItem (String locationString) {
		ItemStack item = new ItemStack(Material.NETHER_STAR, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN+Slabbo.localeManager.getString("general.general.confirm"));

		meta.setLore(Arrays.asList(Slabbo.localeManager.getString("general.general.new-shop"), locationString));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUserBuyItem (String itemName, int quantity, int price, int stock, boolean isAdmin, boolean isLimited) {
		ItemStack item = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta meta = item.getItemMeta();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("item", "'"+itemName+"'");
		replacementMap.put("quantity", quantity);
		replacementMap.put("price", Slabbo.localeManager.getCurrencyString(price));

		if (isAdmin && !isLimited) {
			replacementMap.put("count", "∞");
		} else {
			replacementMap.put("count", stock);
		}

		String inStock = "";

		if (!isLimited) {
			inStock = Slabbo.localeManager.replaceKey("general.general.in-stock", replacementMap);
		} else {
			inStock = Slabbo.localeManager.replaceKey("general.general.limited-stock.buy-stock-left", replacementMap);
		}

		String stacks = Slabbo.localeManager.getString("general.general.stacks");

		String buyFor = Slabbo.localeManager.replaceKey("general.general.buy-for", replacementMap);

		meta.setDisplayName(ChatColor.GOLD+Slabbo.localeManager.replaceKey("gui.items.user.buy-item", replacementMap));

		if (isAdmin && !isLimited) {
			meta.setLore(Arrays.asList("§r"+buyFor, inStock, "(∞ "+stacks+")"));
		} else {
			meta.setLore(Arrays.asList("§r"+buyFor, inStock, "("+Misc.countStacks(stock)+" "+stacks+")"));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUserSellItem (String itemName, int quantity, int price, int stock, boolean isAdmin, boolean isLimited) {
		ItemStack item = new ItemStack(Material.IRON_INGOT, 1);
		ItemMeta meta = item.getItemMeta();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("item", "'"+itemName+"'");
		replacementMap.put("quantity", quantity);
		replacementMap.put("price", Slabbo.localeManager.getCurrencyString(price));

		if (isAdmin && !isLimited) {
			replacementMap.put("count", "∞");
		} else {
			replacementMap.put("count", stock);
		}

		String inStock = "";

		if (!isLimited) {
			inStock = Slabbo.localeManager.replaceKey("general.general.in-stock", replacementMap);
		} else {
			inStock = Slabbo.localeManager.replaceKey("general.general.limited-stock.sell-stock-left", replacementMap);
		}


		String stacks = Slabbo.localeManager.getString("general.general.stacks");

		String sellFor = Slabbo.localeManager.replaceKey("general.general.sell-for", replacementMap);

		meta.setDisplayName(ChatColor.GOLD+Slabbo.localeManager.replaceKey("gui.items.user.sell-item", replacementMap));

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

		ItemStack item = new ItemStack(Material.PAPER, 1);
		ItemMeta meta = item.getItemMeta();

		// TODO: Think over whether we need to show what's essentially "current funds" twice

		meta.setDisplayName(ChatColor.GOLD+Slabbo.localeManager.getString("general.general.current-funds"));

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("funds", "§a"+Slabbo.localeManager.getCurrencyString(formatter.format(funds)));

		meta.setLore(Arrays.asList(Slabbo.localeManager.replaceKey("general.general.funds-message", replacementMap)));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUserInfoItem (Shop shop) {
		ItemStack item = itemAPI.getCommandBlock();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD+"Slabbo "+Slabbo.localeManager.getString("general.general.shop"));

		double buyPerItem = 0;
		double sellPerItem = 0;

		NumberFormat formatter = new DecimalFormat("#0.00");

		// TODO: Actually check if these are zero
		try { buyPerItem = shop.quantity / shop.buyPrice; } catch (Exception e) {}
		try { sellPerItem = shop.quantity / shop.sellPrice; } catch (Exception e) {}

		OfflinePlayer owner = Bukkit.getOfflinePlayer(shop.ownerId);

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("owner", owner.getName());
		replacementMap.put("item", shop.item.getType());
		replacementMap.put("quantity", shop.quantity);
		replacementMap.put("buyPrice", shop.buyPrice);
		replacementMap.put("sellPrice", shop.sellPrice);
		replacementMap.put("buyPerItem", Slabbo.localeManager.getCurrencyString(formatter.format(buyPerItem)));
		replacementMap.put("sellPerItem", Slabbo.localeManager.getCurrencyString(formatter.format(sellPerItem)));

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

		String ownerString = Slabbo.localeManager.replaceKey("gui.items.info.owned-by", replacementMap);
		String sellingString = Slabbo.localeManager.replaceKey("gui.items.info.selling-item", replacementMap);
		String buyEachString = Slabbo.localeManager.replaceKey("gui.items.info.buy-each", replacementMap);
		String sellEachString = Slabbo.localeManager.replaceKey("gui.items.info.sell-each", replacementMap);

		String sellStockString = Slabbo.localeManager.replaceKey("gui.items.info.limit.sell-stock", replacementMap);
		String buyStockString = Slabbo.localeManager.replaceKey("gui.items.info.limit.buy-stock", replacementMap);
		String restockTimeString = Slabbo.localeManager.replaceKey("gui.items.info.limit.restock-time", replacementMap);
		String nextRestockString = Slabbo.localeManager.replaceKey("gui.items.info.limit.next-restock", replacementMap);

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

		meta.setDisplayName(ChatColor.GREEN+Slabbo.localeManager.getString("general.general.destroy-shop"));

		meta.setLore(Arrays.asList(
				ChatColor.RED+Slabbo.localeManager.getString("general.general.destroy-shop-item-warning")
		));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getSellersNoteItem (String sellerNote) {
		if (sellerNote == null || sellerNote.equalsIgnoreCase("")) {
			sellerNote = Slabbo.localeManager.getString("general.general.default-shop-note");
		}

		ItemStack item = new ItemStack(Material.NAME_TAG, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN+Slabbo.localeManager.getString("gui.items.user.sellers-note"));

		meta.setLore(Arrays.asList(
				sellerNote
		));

		item.setItemMeta(meta);

		return item;
	}
}
