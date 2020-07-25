package xyz.mackan.Slabbo.abstractions;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SlabboItemAPI_v1_11_R1 implements SlabboItemAPI {
	public SlabboItemAPI_v1_11_R1 () {}

	public ItemStack getRedStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 14);
	}

	public ItemStack getGreenStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 13);
	}

	public ItemStack getYellowStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 4);
	}

	public ItemStack getLimeStainedGlassPane () {
		return new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 5);
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
