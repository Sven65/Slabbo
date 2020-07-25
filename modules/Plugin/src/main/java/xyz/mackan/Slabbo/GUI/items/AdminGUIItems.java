package xyz.mackan.Slabbo.GUI.items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboItemAPI;
import xyz.mackan.Slabbo.utils.Misc;

import java.util.Arrays;
import java.util.HashMap;

public class AdminGUIItems {
	private static SlabboItemAPI itemAPI = (SlabboItemAPI) Bukkit.getServicesManager().getRegistration(SlabboItemAPI.class).getProvider();

	public static ItemStack getDepositItem (String itemName, int stock, boolean isAdmin) {
		ItemStack item = itemAPI.getChestMinecart();
		ItemMeta meta = item.getItemMeta();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("item", "'"+itemName+"'");

		if (isAdmin) {
			replacementMap.put("count", "∞");
		} else {
			replacementMap.put("count", stock);
		}


		meta.setDisplayName(ChatColor.GOLD+Slabbo.localeManager.replaceKey("gui.items.admin.deposit-item", replacementMap));

		String shiftForBulk = Slabbo.localeManager.getString("general.general.shift-bulk.deposit");
		String inStock = Slabbo.localeManager.replaceKey("general.general.in-stock", replacementMap);

		String stacks = Slabbo.localeManager.getString("general.general.stacks");

		if (isAdmin) {
			meta.setLore(Arrays.asList("§r"+shiftForBulk, inStock, "(∞ "+stacks+")"));
		} else {
			meta.setLore(Arrays.asList("§r"+shiftForBulk, inStock, "("+Misc.countStacks(stock)+" "+stacks+")"));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getWithdrawItem (String itemName, int stock, boolean isAdmin) {
		ItemStack item = new ItemStack(Material.HOPPER_MINECART, 1);
		ItemMeta meta = item.getItemMeta();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("item", "'"+itemName+"'");

		if (isAdmin) {
			replacementMap.put("count", "∞");
		} else {
			replacementMap.put("count", stock);
		}


		meta.setDisplayName(ChatColor.GOLD+Slabbo.localeManager.replaceKey("gui.items.admin.withdraw-item", replacementMap));

		String shiftForBulk = Slabbo.localeManager.getString("general.general.shift-bulk.withdraw");
		String inStock = Slabbo.localeManager.replaceKey("general.general.in-stock", replacementMap);

		String stacks = Slabbo.localeManager.getString("general.general.stacks");

		if (isAdmin) {
			meta.setLore(Arrays.asList("§r"+shiftForBulk, inStock, "(∞ "+stacks+")"));
		} else {
			meta.setLore(Arrays.asList("§r"+shiftForBulk, inStock, "("+Misc.countStacks(stock)+" "+stacks+")"));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getAmountItem (int amount) {
		ItemStack item = new ItemStack(Material.MINECART, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + Slabbo.localeManager.getString("gui.items.admin.changerate-item"));

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("count", amount);

		meta.setLore(Arrays.asList("§r"+ Slabbo.localeManager.replaceKey("general.general.amount-per-click", replacementMap)));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getModifyItem () {
		ItemStack item = itemAPI.getComparator();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + Slabbo.localeManager.getString("gui.items.admin.modify-shop"));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getViewAsCustomerItem () {
		ItemStack item = itemAPI.getOakSign();
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + Slabbo.localeManager.getString("gui.items.admin.view-as-customer"));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getLinkChestItem () {
		ItemStack item = new ItemStack(Material.CHEST, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD+Slabbo.localeManager.getString("gui.items.admin.link-chest"));

		meta.setLore(Arrays.asList(
				ChatColor.GREEN+Slabbo.localeManager.getString("general.chestlink.link-for-refill")
		));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getUnlinkChestItem () {
		ItemStack item = new ItemStack(Material.ENDER_CHEST, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD+Slabbo.localeManager.getString("general.chestlink.cancel-chest-link"));

		item.setItemMeta(meta);

		return item;
	}
}
