package xyz.mackan.Slabbo.abstractions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.mackan.Slabbo.types.SlabType;

import java.util.Collection;

public interface SlabboAPI {
	/**
	 * Gets the friendly name of an item
	 * @param itemStack
	 * @return Name of the item
	 */
	String getItemName (ItemStack itemStack);

	/**
	 * Gets the item a user is holding in their main hand when interacting
	 * @param e
	 * @return The ItemStack the user's holding
	 */
	ItemStack getInteractionItemInHand (PlayerInteractEvent e);

	/**
	 * Gets the item in the players offhand
	 * @param inv
	 * @return The ItemStack the user's holding in their offhand
	 */
	ItemStack getItemInOffHand (PlayerInventory inv);

	/**
	 * Gets if the block is a slab
	 * @param block
	 * @return If the block is a slab
	 */
	boolean isSlab (Block block);

	/**
	 * Gets the type of the slab
	 * @param block
	 * @return The slab type
	 */
	SlabType getSlabType (Block block);

	/**
	 * Sets the gravity of an item
	 * @param item
	 * @param gravity
	 */
	void setGravity (Item item, boolean gravity);

	/**
	 * Returns a list of entities within a bounding box centered around the location
	 * @param location
	 * @param x 1/2 the size of the box along x axis
	 * @param y 1/2 the size of the box along y axis
	 * @param z 1/2 the size of the box along z axis
	 * @return Collection of nearby entities
	 */
	Collection<Entity> getNearbyEntities (Location location, double x, double y, double z);

	/**
	 * Checks if the entity is an Item entity
	 * @param entity
	 * @return If the entity is an Item entity
	 */
	boolean isItem (Entity entity);

	/**
	 * Sets the name of a chest
	 * @param chestBlock
	 * @param name
	 */
	void setChestName (Block chestBlock, String name);

	/**
	 * Checks if the ItemStack has tags for Slabbo
	 * @param itemStack
	 * @return If the itemstack is a slabbo item
	 */
	boolean isSlabboItem (ItemStack itemStack);

	/**
	 * Checks if the Item entity has tags for Slabbo
	 * @param item
	 * @return If the entity is a slabbo item
	 */
	boolean isSlabboItem (Item item);

	/**
	 * Gets if the item stack has the Slabbo tag to prevent it from being picked up
	 * @param item
	 * @return
	 */
	boolean getNoPickup (ItemStack item);

	/**
	 * Gets if the item has the Slabbo tag to prevent it from being picked up
	 * @param item
	 * @return
	 */
	boolean getNoPickup (Item item);

	/**
	 * Gets if the item stack has the Slabbo tag to prevent it from being despawned
	 * @param item
	 * @return
	 */
	boolean getNoDespawn (ItemStack item);

	/**
	 * Gets if the item has the Slabbo tag to prevent it from being despawned
	 * @param item
	 * @return
	 */
	boolean getNoDespawn (Item item);

	/**
	 * Gets if the item has the Slabbo tag to prevent it from being merged with other items
	 * @param item
	 * @return
	 */
	boolean getNoMerge (Item item);

	/**
	 * Gets the shop location string of an item
	 * @param item
	 * @return The shop location
	 */
	String getShopLocation (Item item);

	/**
	 * Sets the items tag for not being picked up
	 * @param item
	 * @param value
	 */
	void setNoPickup (Item item, int value);

	/**
	 * Sets the items tag for not being despawned
	 * @param item
	 * @param value
	 */
	void setNoDespawn (Item item, int value);

	/**
	 * Sets the items tag for not being merged
	 * @param item
	 * @param value
	 */
	void setNoMerge (Item item, int value);

	/**
	 * Sets the items shop location tag
	 * @param item
	 * @param location
	 */
	void setShopLocation (Item item, Location location);

	/**
	 * Gets if the block is a stair
	 * @param block
	 * @return
	 */
	boolean isStair (Block block);

	/**
	 * Gets if the block is an upside down stair
	 * @param block
	 * @return
	 */
	boolean isUpsideDownStair (Block block);

	/**
	 * Gets if the interaction was done with the off hand
	 * @param e
	 * @return
	 */
	boolean isInteractionOffHand(PlayerInteractEvent e);

	/**
	 * Gets if the block is a barrier block
	 * @param block
	 * @return
	 */
	boolean isBarrier (Block block);
}
