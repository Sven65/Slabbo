package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;

public class NameUtil {

	public static String getName (ItemStack is) {
		ItemMeta meta = is.getItemMeta();
		boolean hasDisplayName = meta != null && meta.hasDisplayName();

		// return displayname if item has one
		if(is.hasItemMeta() && hasDisplayName) return is.getItemMeta().getDisplayName();

		SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

		return api.getItemName(is);
	}

}