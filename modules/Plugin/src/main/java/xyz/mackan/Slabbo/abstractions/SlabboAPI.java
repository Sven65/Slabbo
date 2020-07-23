package xyz.mackan.Slabbo.abstractions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.listeners.PlayerPickupItemListener;
import xyz.mackan.Slabbo.types.SlabType;

import java.util.Collection;

public interface SlabboAPI {
	String getItemName (ItemStack itemStack);

	ItemStack getInteractionItemInHand (PlayerInteractEvent e);

	boolean isSlab (Block block);

	SlabType getSlabType (Block block);

	void setGravity (Item item, boolean gravity);

	Collection<Entity> getNearbyEntities (Location location, double x, double y, double z);

	boolean isItem (Entity entity);

	void setChestName (Block chestBlock, String name);

	boolean isSlabboItem (Item item);

	boolean getNoPickup (Item item);

	boolean getNoDespawn (Item item);

	boolean getNoMerge (Item item);

	String getShopLocation (Item item);
}
