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

	public ItemStack getLimeStainedGlassPane () {
		return new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
	}

	public ItemStack getCommandBlock () {
		return new ItemStack(Material.COMMAND_BLOCK, 1);
	}
	public ItemStack getChestMinecart () {
		return new ItemStack(Material.CHEST_MINECART, 1);
	}

	public ItemStack getComparator () {
		return new ItemStack(Material.COMPARATOR, 1);
	}

	public ItemStack getOakSign () {
		return new ItemStack(Material.OAK_SIGN, 1);
	}

}
