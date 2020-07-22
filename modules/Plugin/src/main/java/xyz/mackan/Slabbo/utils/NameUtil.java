package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;

import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;

public class NameUtil {

	public static String getName (ItemStack is) {
		// return displayname if item has one
		if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()) return is.getItemMeta().getDisplayName();

		SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

		return api.getItemName(is);
	}

}