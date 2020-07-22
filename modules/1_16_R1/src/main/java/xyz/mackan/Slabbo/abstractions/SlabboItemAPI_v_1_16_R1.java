package xyz.mackan.Slabbo.abstractions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SlabboItemAPI_v_1_16_R1 implements SlabboItemAPI {
	public SlabboItemAPI_v_1_16_R1 () {}

	public ItemStack getRedStainedGlassPane () {
		return new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
	}

	public ItemStack getYellowStainedGlassPane () {
		return new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);
	}

	public ItemStack getGreenStainedGlassPane () {
		return new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
	}
}
