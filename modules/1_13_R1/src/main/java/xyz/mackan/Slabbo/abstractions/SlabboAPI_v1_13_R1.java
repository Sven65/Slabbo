package xyz.mackan.Slabbo.abstractions;

import org.bukkit.inventory.ItemStack;
import net.minecraft.server.v1_13_R1.LocaleLanguage;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;

public class SlabboAPI_v1_13_R1 implements SlabboAPI {
	public SlabboAPI_v1_13_R1 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_13_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		return LocaleLanguage.a().a(nmsStack.getItem().getName());
	}
}
