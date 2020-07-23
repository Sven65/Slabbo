package xyz.mackan.Slabbo.abstractions;

import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.block.CraftChest;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.mackan.Slabbo.listeners.PlayerPickupItemListener;
import xyz.mackan.Slabbo.types.SlabType;

import java.util.Collection;

public class SlabboAPI_v1_8_R1 implements SlabboAPI {
	public SlabboAPI_v1_8_R1 () {}

	public String getItemName (ItemStack itemStack) {
		net.minecraft.server.v1_8_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

		return nmsStack.getName();

//		LocaleLanguage lang = new LocaleLanguage();
//
//		return lang.a(nmsStack.getItem().getName());
	}

	public ItemStack getInteractionItemInHand (PlayerInteractEvent e) {
		return e.getItem();
	}

	public boolean isSlab (Block block) {
		return (block.getType() == Material.STEP || block.getType() == Material.WOOD_STEP || block.getType() == Material.DOUBLE_STEP || block.getType() == Material.WOOD_DOUBLE_STEP);
	}

	@Override
	public SlabType getSlabType (Block block) {

		if (!isSlab(block)) return null;

		Material blockType = block.getType();

		switch (blockType) {
			case STEP:
			case WOOD_STEP:
				return SlabType.BOTTOM;
			case DOUBLE_STEP:
			case WOOD_DOUBLE_STEP:
				return SlabType.DOUBLE;
		}

		return null;
	}

	public void setGravity (Item item, boolean gravity) {
		CraftEntity nmsEntity = (CraftEntity) item;
	}

	public Collection<Entity> getNearbyEntities (Location location, double x, double y, double z) {
		Entity tempEnt = location.getWorld().spawnArrow(location, new Vector(0, 0, 0), 0, 0);

		Collection<Entity> nearby = tempEnt.getNearbyEntities(x, y, z);

		tempEnt.remove();

		return nearby;
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
}
