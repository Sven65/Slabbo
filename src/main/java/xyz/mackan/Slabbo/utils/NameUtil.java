package xyz.mackan.Slabbo.utils;

import net.minecraft.server.v1_16_R1.LocaleLanguage;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NameUtil {

	public static String getName (ItemStack is) {
		// return displayname if item has one
		if(is.hasItemMeta() && is.getItemMeta().hasDisplayName()) return is.getItemMeta().getDisplayName();

		net.minecraft.server.v1_16_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);
		return LocaleLanguage.a().a(nmsStack.getItem().getName());
	}

}