package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_8_R1.BlockStepAbstract;
import net.minecraft.server.v1_8_R1.LocaleLanguage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SlabboAPI_v1_8_R1 implements SlabboAPI {
	public SlabboAPI_v1_8_R1 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_8_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

		LocaleLanguage lang = new LocaleLanguage();

		return lang.a(nmsStack.getItem().getName());
	}

	public ItemStack getInteractionItemInHand (PlayerInteractEvent e) {
		return e.getItem();
	}

	public boolean isSlab (Block block) {
		return (block.getType() == Material.STEP || block.getType() == Material.WOOD_STEP);
	}
}
