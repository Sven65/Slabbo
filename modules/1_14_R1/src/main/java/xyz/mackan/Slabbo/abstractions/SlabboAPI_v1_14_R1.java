package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.AttributeKey;
import xyz.mackan.Slabbo.types.MetaKey;
import xyz.mackan.Slabbo.types.SlabType;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.Collection;
import java.util.List;

public class SlabboAPI_v1_14_R1 implements SlabboAPI {
	public SlabboAPI_v1_14_R1 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

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
		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		if (container == null || !container.has(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER)) {
			List<String> lore = itemMeta.getLore();

			boolean noPickup = false;

			for (String line : lore) {
				if (!line.startsWith(MetaKey.NO_PICKUP.getKey())) continue;

				String value = line.replace(MetaKey.NO_PICKUP.getKey()+"=", "");

				noPickup = value.equals("1");
			}

			return noPickup;
		}

		int noPickup = container.get(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER);

		return noPickup == 1;
	}

	public boolean getNoDespawn (Item item) {
		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		int noDespawn = container.get(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER);
		return noDespawn == 1;
	}

	public boolean getNoMerge (Item item) {
		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		int noMerge = container.get(AttributeKey.NO_MERGE.getKey(), PersistentDataType.INTEGER);

		return noMerge == 1;
	}

	public String getShopLocation (Item item) {
		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		String shopLocation = container.get(AttributeKey.SHOP_LOCATION.getKey(), PersistentDataType.STRING);

		return shopLocation;
	}

	public void setNoPickup (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		container.set(AttributeKey.NO_PICKUP.getKey(), PersistentDataType.INTEGER, value);

		itemStack.setItemMeta(itemMeta);

		item.setItemStack(itemStack);
	}


	public void setNoDespawn (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		container.set(AttributeKey.NO_DESPAWN.getKey(), PersistentDataType.INTEGER, value);

		itemStack.setItemMeta(itemMeta);

		item.setItemStack(itemStack);
	}

	public void setNoMerge (Item item, int value) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		container.set(AttributeKey.NO_MERGE.getKey(), PersistentDataType.INTEGER, value);

		itemStack.setItemMeta(itemMeta);

		item.setItemStack(itemStack);
	}

	public void setShopLocation (Item item, Location location) {
		ItemStack itemStack = item.getItemStack();

		ItemMeta itemMeta = item.getItemStack().getItemMeta();

		PersistentDataContainer container = itemMeta.getPersistentDataContainer();

		container.set(AttributeKey.SHOP_LOCATION.getKey(), PersistentDataType.STRING, ShopUtil.locationToString(location));

		itemStack.setItemMeta(itemMeta);

		item.setItemStack(itemStack);
	}

	public boolean isSlabboItem (Item item) {
		return getNoPickup(item) && getNoDespawn(item);
	}
}
