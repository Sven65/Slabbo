package xyz.mackan.Slabbo.abstractions;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface SlabboAPI {
	String getItemName (ItemStack itemStack);

	ItemStack getInteractionItemInHand (PlayerInteractEvent e);

	boolean isSlab (Block block);
}
