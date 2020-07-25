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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.MetaKey;
import xyz.mackan.Slabbo.types.SlabType;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.ArrayList;
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

		if (!isSlab(block)) return null;

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

	public boolean getNoPickup (Item item) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		if (!meta.hasLore()) return false;

		List<String> lore = meta.getLore();

		boolean noPickup = false;

		for (String line : lore) {
			if (!line.startsWith(MetaKey.NO_PICKUP.getKey())) continue;

			String value = line.replace(MetaKey.NO_PICKUP.getKey()+"=", "");

			noPickup = value.equals("1");
		}

		return noPickup;
	}

	public boolean getNoDespawn (Item item) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		if (!meta.hasLore()) return false;

		List<String> lore = meta.getLore();

		boolean noDespawn = false;

		for (String line : lore) {
			if (!line.startsWith(MetaKey.NO_DESPAWN.getKey())) continue;

			String value = line.replace(MetaKey.NO_DESPAWN.getKey()+"=", "");

			noDespawn = value.equals("1");
		}

		return noDespawn;
	}

	public boolean getNoMerge (Item item) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		if (!meta.hasLore()) return false;

		List<String> lore = meta.getLore();

		boolean noMerge = true;

		for (String line : lore) {
			if (!line.startsWith(MetaKey.NO_MERGE.getKey())) continue;

			String value = line.replace(MetaKey.NO_MERGE.getKey()+"=", "");

			noMerge = value.equals("1");
		}

		return noMerge;
	}

	public String getShopLocation (Item item) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		if (!meta.hasLore()) return null;

		List<String> lore = meta.getLore();

		String shopLocation = null;

		for (String line : lore) {
			if (!line.startsWith(MetaKey.SHOP_LOCATION.getKey())) continue;

			shopLocation = line.replace(MetaKey.SHOP_LOCATION.getKey()+"=", "");
		}

		return shopLocation;
	}

	public void setNoPickup (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		List<String> currentLore = new ArrayList<String>();

		if (meta.hasLore()) {
			currentLore = meta.getLore();
		}

		currentLore.add(MetaKey.NO_PICKUP.getKey()+"="+value);

		meta.setLore(currentLore);

		itemStack.setItemMeta(meta);

		item.setItemStack(itemStack);
	}


	public void setNoDespawn (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		List<String> currentLore = new ArrayList<String>();

		if (meta.hasLore()) {
			currentLore = meta.getLore();
		}

		currentLore.add(MetaKey.NO_DESPAWN.getKey()+"="+value);

		meta.setLore(currentLore);

		itemStack.setItemMeta(meta);

		item.setItemStack(itemStack);
	}

	public void setNoMerge (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		List<String> currentLore = new ArrayList<String>();

		if (meta.hasLore()) {
			currentLore = meta.getLore();
		}

		currentLore.add(MetaKey.NO_MERGE.getKey()+"="+value);

		meta.setLore(currentLore);

		itemStack.setItemMeta(meta);

		item.setItemStack(itemStack);
	}

	public void setShopLocation (Item item, Location location) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta meta = itemStack.getItemMeta();

		List<String> currentLore = new ArrayList<String>();

		if (meta.hasLore()) {
			currentLore = meta.getLore();
		}

		currentLore.add(MetaKey.SHOP_LOCATION.getKey()+"="+ShopUtil.locationToString(location));

		meta.setLore(currentLore);

		itemStack.setItemMeta(meta);

		item.setItemStack(itemStack);
	}

	public boolean isSlabboItem (Item item) {
		return getNoPickup(item) && getNoDespawn(item);
	}
}
