package xyz.mackan.Slabbo.abstractions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;

import org.bukkit.craftbukkit.v1_20_R4.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftItem;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;

import org.bukkit.entity.Item;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.types.AttributeKey;
import xyz.mackan.Slabbo.types.MetaKey;
import xyz.mackan.Slabbo.types.SlabType;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.manager.ShopManager;

import java.util.Collection;

public class SlabboAPI_v1_20_R4 implements SlabboAPI {
	public SlabboAPI_v1_20_R4() {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

		return nmsStack.getDisplayName().getString();
	}

	public int getMaxStack (ItemStack itemStack) {
		net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

		return nmsStack.getMaxStackSize();
	}

	public ItemStack getInteractionItemInHand (PlayerInteractEvent e) {
		EquipmentSlot hand = e.getHand();

		if (hand == null || hand != EquipmentSlot.HAND) return null;

		return e.getItem();
	}

	public ItemStack getInteractionItemInOffHand (PlayerInteractEvent e) {
		return e.getPlayer().getInventory().getItemInOffHand();
	}

	public ItemStack getItemInOffHand (org.bukkit.inventory.PlayerInventory inv) { return inv.getItemInOffHand(); }


	@Override
	public boolean isSlab (Block block) {
		BlockData blockData = block.getBlockData();

		return (blockData instanceof Slab);
	}

	@Override
	public SlabType getSlabType (Block block) {
		if (!isSlab(block)) return SlabType.NONE;

		BlockData blockData = block.getBlockData();

		Slab slab = (Slab) blockData;

		Slab.Type slabType = slab.getType();

		switch (slabType) {
			case TOP:
				return SlabType.TOP;
			case BOTTOM:
				return SlabType.BOTTOM;
			case DOUBLE:
				return SlabType.DOUBLE;
		}

		return null;
	}

	@Override
	public void setGravity (Item item, boolean gravity) {
		item.setGravity(gravity);

	}

	public Collection<Entity> getNearbyEntities (Location location, double x, double y, double z) {
		return location.getWorld().getNearbyEntities(location, x, y, z);
	}

	public boolean isItem (Entity entity) {
		return (entity instanceof Item) || (entity instanceof CraftItem);
	}

	public void setChestName (Block chestBlock, String name) {
		Chest chest = (Chest) chestBlock.getState();

		chest.setCustomName(name);

		chest.update();
	}


	public boolean getNoPickup (ItemStack itemStack) {
		int noPickup = ItemUtil.getContainerIntValue(itemStack, AttributeKey.NO_PICKUP.getKey());

		if (noPickup <= -1) {
			String value = ItemUtil.getLoreValue(itemStack, MetaKey.NO_PICKUP.getKey());

			return value.equals("1");
		}

		return noPickup == 1;
	}

	public boolean getNoPickup (Item item) {
		ItemStack itemStack = item.getItemStack();

		return getNoPickup(itemStack);
	}

	public boolean getNoDespawn (ItemStack itemStack) {
		int noDespawn = ItemUtil.getContainerIntValue(itemStack, AttributeKey.NO_DESPAWN.getKey());

		return noDespawn == 1;
	}

	public boolean getNoDespawn (Item item) {
		ItemStack itemStack = item.getItemStack();

		return getNoDespawn(itemStack);
	}

	public boolean getNoMerge (Item item) {
		ItemStack itemStack = item.getItemStack();

		int noMerge = ItemUtil.getContainerIntValue(itemStack, AttributeKey.NO_MERGE.getKey());

		return noMerge == 1;
	}

	public String getShopLocation (Item item) {
		ItemStack itemStack = item.getItemStack();

		String shopLocation = ItemUtil.getContainerStringValue(itemStack, AttributeKey.SHOP_LOCATION.getKey());

		return shopLocation;
	}

	public void setNoPickup (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		item.setItemStack(ItemUtil.setContainerIntValue(itemStack, AttributeKey.NO_PICKUP.getKey(), value));
	}


	public void setNoDespawn (Item item, int value) {
		ItemStack itemStack = item.getItemStack();
		item.setItemStack(ItemUtil.setContainerIntValue(itemStack, AttributeKey.NO_DESPAWN.getKey(), value));
	}

	public void setNoMerge (Item item, int value) {
		ItemStack itemStack = item.getItemStack();
		item.setItemStack(ItemUtil.setContainerIntValue(itemStack, AttributeKey.NO_MERGE.getKey(), value));
	}

	public void setShopLocation (Item item, Location location) {
		ItemStack itemStack = item.getItemStack();

		item.setItemStack(ItemUtil.setContainerStringValue(itemStack, AttributeKey.SHOP_LOCATION.getKey(), ShopManager.locationToString(location)));
	}

	public boolean isSlabboItem (Item item) {
		return getNoPickup(item) && getNoDespawn(item);
	}

	public boolean isSlabboItem (ItemStack itemStack) { return getNoPickup(itemStack) && getNoDespawn(itemStack); }

	public boolean isStair (Block block) {
		BlockData blockData = block.getBlockData();

		return (blockData instanceof Stairs);
	}

	public boolean isUpsideDownStair (Block block) {
		if (!isStair(block)) return false;

		Stairs stairs = (Stairs) block.getBlockData();

		return stairs.getHalf() == Bisected.Half.TOP;
	}

	public boolean isInteractionOffHand(PlayerInteractEvent e) {
		return e.getHand() == EquipmentSlot.OFF_HAND;
	}

	public boolean isBarrier (Block block) {
		return block.getType() == Material.BARRIER;
	}
}
