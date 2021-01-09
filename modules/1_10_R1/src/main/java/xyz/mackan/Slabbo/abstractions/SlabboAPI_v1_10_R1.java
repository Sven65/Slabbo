package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import xyz.mackan.Slabbo.types.MetaKey;
import xyz.mackan.Slabbo.types.SlabType;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.manager.ShopManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SlabboAPI_v1_10_R1 implements SlabboAPI {
	public SlabboAPI_v1_10_R1 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_10_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

		return nmsStack.getName();
	}

	public ItemStack getInteractionItemInHand (PlayerInteractEvent e) {
		return e.getItem();
	}

	public ItemStack getItemInOffHand (org.bukkit.inventory.PlayerInventory inv) { return inv.getItemInOffHand(); }

	public boolean isSlab (Block block) {
		List<Material> slabMaterials = Arrays.asList(
				Material.STEP,
				Material.WOOD_STEP,
				Material.PURPUR_SLAB,
				Material.DOUBLE_STEP,
				Material.WOOD_DOUBLE_STEP,
				Material.STONE_SLAB2,
				Material.DOUBLE_STONE_SLAB2,
				Material.PURPUR_DOUBLE_SLAB
		);

		return slabMaterials.contains(block.getType());
	}

	public SlabType getSlabType (Block block) {

		if (!isSlab(block)) return SlabType.NONE;

		Material blockType = block.getType();

		MaterialData blockData = block.getState().getData();

		if (blockData instanceof Step) {
			Step step = (Step) blockData;

			switch (blockType) {
				case STEP:
				case WOOD_STEP:
				case PURPUR_SLAB:
					if (step.isInverted()) return SlabType.TOP;
					return SlabType.BOTTOM;
				case DOUBLE_STEP:
				case WOOD_DOUBLE_STEP:
				case PURPUR_DOUBLE_SLAB:
					return SlabType.DOUBLE;
			}
		} else if (blockData instanceof WoodenStep) {
			WoodenStep step = (WoodenStep) blockData;

			switch (blockType) {
				case WOOD_STEP:
					if (step.isInverted()) return SlabType.TOP;
					return SlabType.BOTTOM;
				case WOOD_DOUBLE_STEP:
					return SlabType.DOUBLE;
			}
		}

		return null;
	}

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

		World nmsWorld = ((CraftWorld) chestBlock.getWorld()).getHandle();

		TileEntity tileEntity = nmsWorld.getTileEntity(new BlockPosition(chestBlock.getX(), chestBlock.getY(), chestBlock.getZ()));

		if (!(tileEntity instanceof TileEntityChest)) return;

		((TileEntityChest) tileEntity).a(name);

		chest.update();
	}

	public boolean getNoPickup (ItemStack itemStack) {
		return ItemUtil.getLoreValue(itemStack, MetaKey.NO_PICKUP.getKey()).equals("1");
	}

	public boolean getNoPickup (Item item) {
		ItemStack itemStack = item.getItemStack();

		return getNoPickup(itemStack);
	}


	public boolean getNoDespawn (ItemStack itemStack) {
		return ItemUtil.getLoreValue(itemStack, MetaKey.NO_DESPAWN.getKey()).equals("1");
	}

	public boolean getNoDespawn (Item item) {
		ItemStack itemStack = item.getItemStack();

		return getNoDespawn(itemStack);
	}

	public boolean getNoMerge (Item item) {
		ItemStack itemStack = item.getItemStack();

		return ItemUtil.getLoreValue(itemStack, MetaKey.NO_MERGE.getKey()).equals("1");
	}

	public String getShopLocation (Item item) {
		ItemStack itemStack = item.getItemStack();

		String value = ItemUtil.getLoreValue(itemStack, MetaKey.SHOP_LOCATION.getKey());

		if (value.equals("")) return null;

		return value;
	}

	public void setNoPickup (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		item.setItemStack(ItemUtil.setLoreValue(itemStack, MetaKey.NO_PICKUP.getKey(), ""+value));
	}


	public void setNoDespawn (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		item.setItemStack(ItemUtil.setLoreValue(itemStack, MetaKey.NO_DESPAWN.getKey(), ""+value));
	}

	public void setNoMerge (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		item.setItemStack(ItemUtil.setLoreValue(itemStack, MetaKey.NO_MERGE.getKey(), ""+value));
	}

	public void setShopLocation (Item item, Location location) {
		ItemStack itemStack = item.getItemStack();

		item.setItemStack(ItemUtil.setLoreValue(itemStack, MetaKey.SHOP_LOCATION.getKey(), ShopManager.locationToString(location)));
	}

	public boolean isSlabboItem (ItemStack itemStack) { return getNoPickup(itemStack) && getNoDespawn(itemStack); }

	public boolean isSlabboItem (Item item) {
		return getNoPickup(item) && getNoDespawn(item);
	}

	public boolean isStair (Block block) {
		return (block.getState().getData() instanceof Stairs);
	}

	public boolean isUpsideDownStair (Block block) {
		if (!isStair(block)) return false;

		Stairs stairs = (Stairs) block.getState().getData();

		return stairs.isInverted();
	}

	public boolean isInteractionOffHand(PlayerInteractEvent e) {
		return e.getHand() == EquipmentSlot.OFF_HAND;
	}

	public boolean isBarrier (Block block) {
		return block.getType() == Material.BARRIER;
	}

}
