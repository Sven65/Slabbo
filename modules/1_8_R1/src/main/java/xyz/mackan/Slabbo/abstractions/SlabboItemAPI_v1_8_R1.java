package xyz.mackan.Slabbo.abstractions;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SlabboItemAPI_v1_8_R1 implements SlabboItemAPI {
	public SlabboItemAPI_v1_8_R1 () {}

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
}
