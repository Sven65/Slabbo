package xyz.mackan.Slabbo.abstractions;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.mackan.Slabbo.Slabbo;

public class SlabboItemAPI_v1_19_R2 implements SlabboItemAPI {
	public SlabboItemAPI_v1_19_R2() {}

	private ItemStack addCustomModelData(ItemStack stack, String name) {
		ItemMeta meta = stack.getItemMeta();
		meta.setCustomModelData(Slabbo.getInstance().getConfig().getInt("items.customModelData." + name, 0));
		stack.setItemMeta(meta);

		return stack;
	}

	public ItemStack getRedStainedGlassPane () {
		return this.addCustomModelData(new ItemStack(Material.RED_STAINED_GLASS_PANE, 1), "redStainedPane");
	}

	public ItemStack getYellowStainedGlassPane () {
		return this.addCustomModelData(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1), "yellowStainedPane");
	}

	public ItemStack getGreenStainedGlassPane () {
		return this.addCustomModelData(new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1), "greenStainedPane");
	}

	public ItemStack getLimeStainedGlassPane () {
		return this.addCustomModelData(new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1), "limeStainedPane");
	}

	public ItemStack getCommandBlock () {
		return this.addCustomModelData(new ItemStack(Material.COMMAND_BLOCK, 1), "commandBlock");
	}
	public ItemStack getChestMinecart () {
		return this.addCustomModelData(new ItemStack(Material.CHEST_MINECART, 1), "chestMinecart");
	}

	public ItemStack getComparator () {
		return this.addCustomModelData(new ItemStack(Material.COMPARATOR, 1), "comparator");
	}

	public ItemStack getOakSign () {
		return this.addCustomModelData(new ItemStack(Material.OAK_SIGN, 1), "oakSign");
	}

	public ItemStack getDefaultSlab () { return new ItemStack(Material.STONE_SLAB, 1); }

	public ItemStack getHopperMinecart() { return this.addCustomModelData(new ItemStack(Material.HOPPER_MINECART, 1), "hopperMinecart"); }

	public ItemStack getMinecart() { return this.addCustomModelData(new ItemStack(Material.MINECART, 1), "minecart"); }

	public ItemStack getChest() { return this.addCustomModelData(new ItemStack(Material.CHEST, 1), "chest"); }

	public ItemStack getEnderChest() { return this.addCustomModelData(new ItemStack(Material.ENDER_CHEST, 1), "enderChest"); }

	public ItemStack getBarrier() { return this.addCustomModelData(new ItemStack(Material.BARRIER, 1), "barrier"); }

	public ItemStack getNetherStar() { return this.addCustomModelData(new ItemStack(Material.NETHER_STAR, 1), "netherStar"); }

	public ItemStack getGoldIngot() { return this.addCustomModelData(new ItemStack(Material.GOLD_INGOT, 1), "goldIngot"); }

	public ItemStack getIronIngot() { return this.addCustomModelData(new ItemStack(Material.IRON_INGOT, 1), "ironIngot"); }

	public ItemStack getPaper() { return this.addCustomModelData(new ItemStack(Material.PAPER, 1), "paper"); }

	public ItemStack getNameTag() { return this.addCustomModelData(new ItemStack(Material.NAME_TAG, 1), "nametag"); }

	public ItemStack getClock() {
		return this.addCustomModelData(new ItemStack(Material.CLOCK, 1), "clock");
	}
}
