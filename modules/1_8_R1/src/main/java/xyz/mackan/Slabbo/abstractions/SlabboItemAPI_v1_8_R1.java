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
}
