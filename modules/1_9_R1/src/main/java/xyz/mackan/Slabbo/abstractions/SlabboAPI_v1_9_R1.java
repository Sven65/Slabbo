package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_9_R1.LocaleLanguage;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class SlabboAPI_v1_9_R1 implements SlabboAPI {
	public SlabboAPI_v1_9_R1 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_9_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		return LocaleLanguage.a().a(nmsStack.getItem().getName());
	}
}
