package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;

public class NameUtil {
	public static String getName(ItemStack is) {
		if (is == null) return "";
		ItemMeta meta = is.getItemMeta();
		if (meta != null) {
			if (meta.hasDisplayName() && !meta.getDisplayName().isEmpty()) {
				return meta.getDisplayName();
			}
			if (meta.hasLocalizedName() && meta.getLocalizedName() != null && !meta.getLocalizedName().isEmpty()) {
				return meta.getLocalizedName();
			}
		}
		SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();
		return api.getItemName(is);
	}
}