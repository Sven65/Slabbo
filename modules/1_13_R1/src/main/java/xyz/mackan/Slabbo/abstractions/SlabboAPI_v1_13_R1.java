package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

public class SlabboAPI_v1_13_R1 implements SlabboAPI {
	public SlabboAPI_v1_13_R1 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_13_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

		return LocaleLanguage.a().a(nmsStack.getItem().getName());
	}

	public ItemStack getInteractionItemInHand (PlayerInteractEvent e) {
		return e.getItem();
	}

	public boolean isSlab (Block block) {
		BlockData blockData = block.getBlockData();

		return (blockData instanceof Slab);
	}

	@Override
	public SlabType getSlabType (Block block) {
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
