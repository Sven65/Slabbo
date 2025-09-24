package xyz.mackan.Slabbo.abstractions;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SlabboItemAPI_v1_8_R2 implements SlabboItemAPI {
	public SlabboItemAPI_v1_8_R2 () {}

	public ItemStack getRedStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData());
	}

	public ItemStack getGreenStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GREEN.getData());
	}

	public ItemStack getYellowStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.YELLOW.getData());
	}

	public ItemStack getLimeStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getData());
	}

	public ItemStack getCommandBlock () {
		return new ItemStack(Material.COMMAND, 1);
	}

	public ItemStack getChestMinecart () {
		return new ItemStack(Material.STORAGE_MINECART, 1);
	}

	public ItemStack getComparator () {
		return new ItemStack(Material.REDSTONE_COMPARATOR, 1);
	}

	public ItemStack getOakSign () {
		return new ItemStack(Material.SIGN, 1);
	}

	public ItemStack getDefaultSlab () { return new ItemStack(Material.STEP, 1); }

	public ItemStack getHopperMinecart() { return new ItemStack(Material.HOPPER_MINECART, 1); }

	public ItemStack getMinecart() { return new ItemStack(Material.MINECART, 1); }

	public ItemStack getChest() { return new ItemStack(Material.CHEST, 1); }

	public ItemStack getEnderChest() { return new ItemStack(Material.ENDER_CHEST, 1); }

	public ItemStack getBarrier() { return new ItemStack(Material.BARRIER, 1); }

	public ItemStack getNetherStar() { return new ItemStack(Material.NETHER_STAR, 1); }

	public ItemStack getGoldIngot() { return new ItemStack(Material.GOLD_INGOT, 1); }

	public ItemStack getIronIngot() { return new ItemStack(Material.IRON_INGOT, 1); }

	public ItemStack getPaper() { return new ItemStack(Material.PAPER, 1); }

	public ItemStack getNameTag() { return new ItemStack(Material.NAME_TAG, 1); }

	public ItemStack getClock() { return new ItemStack(Material.WATCH, 1); }

}
