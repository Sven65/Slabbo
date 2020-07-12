package xyz.mackan.Slabbo.GUI.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.Slabbo.utils.Misc;

import java.util.Arrays;

public class AdminGUIItems {
	public static ItemStack getDepositItem (String itemName, int stock, boolean isAdmin) {
		ItemStack item = new ItemStack(Material.CHEST_MINECART, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD+"Deposit '"+itemName+"'");

		if (isAdmin) {
			meta.setLore(Arrays.asList("§r+Shift for bulk deposit", "In stock: ∞", "(∞ stacks)"));
		} else {
			meta.setLore(Arrays.asList("§r+Shift for bulk deposit", "In stock: "+stock, "("+ Misc.countStacks(stock) +" stacks)"));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getWithdrawItem (String itemName, int stock, boolean isAdmin) {
		ItemStack item = new ItemStack(Material.HOPPER_MINECART, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD+"Withdraw '"+itemName+"'");

		if (isAdmin) {
			meta.setLore(Arrays.asList("§r+Shift for bulk withdrawal", "In stock: ∞", "(∞ stacks)"));
		} else {
			meta.setLore(Arrays.asList("§r+Shift for bulk withdrawal", "In stock: "+stock, "("+ Misc.countStacks(stock) +" stacks)"));
		}

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getAmountItem (int amount) {
		ItemStack item = new ItemStack(Material.MINECART, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + "Change rate");

		meta.setLore(Arrays.asList("§rAmount per click: " + amount, "Change rate"));

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getModifyItem () {
		ItemStack item = new ItemStack(Material.COMPARATOR, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + "Modify Shop");

		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getViewAsCustomerItem () {
		ItemStack item = new ItemStack(Material.OAK_SIGN, 1);
		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + "View as customer");

		item.setItemMeta(meta);

		return item;
	}
}
