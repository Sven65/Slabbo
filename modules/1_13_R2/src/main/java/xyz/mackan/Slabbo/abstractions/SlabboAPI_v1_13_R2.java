package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftItem;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.MetaKey;
import xyz.mackan.Slabbo.types.SlabType;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.Collection;
import java.util.List;

public class SlabboAPI_v1_13_R2 implements SlabboAPI {
	public SlabboAPI_v1_13_R2 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

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
		List<MetadataValue> metaList = item.getMetadata(MetaKey.NO_PICKUP.getKey());

		if (metaList.size() <= 0) return false;

		int noPickup = metaList.get(0).asInt();

		return noPickup == 1;
	}

	public boolean getNoDespawn (Item item) {
		List<MetadataValue> metaList = item.getMetadata(MetaKey.NO_DESPAWN.getKey());

		if (metaList.size() <= 0) return false;

		int noDespawn = metaList.get(0).asInt();

		return noDespawn == 1;
	}

	public boolean getNoMerge (Item item) {
		List<MetadataValue> metaList = item.getMetadata(MetaKey.NO_MERGE.getKey());

		if (metaList.size() <= 0) return false;

		int noMerge = metaList.get(0).asInt();

		return noMerge == 1;
	}

	public String getShopLocation (Item item) {
		List<MetadataValue> metaList = item.getMetadata(MetaKey.SHOP_LOCATION.getKey());

		if (metaList.size() <= 0) return null;

		return metaList.get(0).asString();
	}

	public void setNoPickup (Item item, int value) {
		item.setMetadata(MetaKey.NO_PICKUP.getKey(), new FixedMetadataValue(Slabbo.getInstance(), value));
	}


	public void setNoDespawn (Item item, int value) {
		item.setMetadata(MetaKey.NO_DESPAWN.getKey(), new FixedMetadataValue(Slabbo.getInstance(), value));
	}

	public void setNoMerge (Item item, int value) {
		item.setMetadata(MetaKey.NO_MERGE.getKey(), new FixedMetadataValue(Slabbo.getInstance(), value));
	}

	public void setShopLocation (Item item, Location location) {
		item.setMetadata(MetaKey.SHOP_LOCATION.getKey(), new FixedMetadataValue(Slabbo.getInstance(), ShopUtil.locationToString(location)));
	}

	public boolean isSlabboItem (Item item) {
		return getNoPickup(item) && getNoDespawn(item);
	}
}
