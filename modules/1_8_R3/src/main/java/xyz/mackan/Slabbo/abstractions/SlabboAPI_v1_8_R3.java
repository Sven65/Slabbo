package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_8_R3.LocaleLanguage;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class SlabboAPI_v1_8_R3 implements SlabboAPI {
	public SlabboAPI_v1_8_R3 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		return LocaleLanguage.a().a(nmsStack.getItem().getName());
	}
}
