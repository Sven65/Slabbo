package xyz.mackan.Slabbo.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.Iterator;
import java.util.Set;

public class BlockEventListeners implements Listener {
	public boolean isLookingAtShop (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		BlockData blockData = lookingAt.getBlockData();

		boolean isSlab = (blockData instanceof Slab);

		if (!isSlab) return false;

		String locationString = ShopUtil.locationToString(lookingAt.getLocation());

		if (Slabbo.shopUtil.shops.containsKey(locationString)) return true;

		return false;
	}

	public boolean isLookingAtLinkedChest (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		if (!Slabbo.chestLinkUtil.isChestLinked(lookingAt)) return false;

		return true;
	}

	@EventHandler
	public void onPlace (BlockPlaceEvent e) {
		if (isLookingAtShop(e.getPlayer())) {
			e.setBuild(false);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak (BlockBreakEvent e) {
		if (isLookingAtShop(e.getPlayer())) {
			e.setCancelled(true);
			e.setDropItems(false);
		}

		if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
			if (isLookingAtLinkedChest(e.getPlayer())) {
				e.getPlayer().sendMessage("You can't destroy that chest because it's linked to a chest.");
				e.setCancelled(true);
				e.setDropItems(false);
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> it = event.blockList().iterator();

		while (it.hasNext()) {
			Block block = it.next();

			String locationString = ShopUtil.locationToString(block.getLocation());

			if (!Slabbo.shopUtil.shops.containsKey(ShopUtil.locationToString(block.getLocation()))) continue;

			if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
				if (!Slabbo.chestLinkUtil.isChestLinked(block)) continue;
			}

			it.remove();
		}
	}

	@EventHandler
	public void onPistonExtend (BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()){
			String locationString = ShopUtil.locationToString(block.getLocation());

			if (!Slabbo.shopUtil.shops.containsKey(locationString)) continue;

			if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
				if (!Slabbo.chestLinkUtil.isChestLinked(block)) continue;
			}

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPistonRetract (BlockPistonRetractEvent event) {
		for (Block block : event.getBlocks()){
			String locationString = ShopUtil.locationToString(block.getLocation());

			if (!Slabbo.shopUtil.shops.containsKey(locationString)) continue;

			if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
				if (!Slabbo.chestLinkUtil.isChestLinked(block)) continue;
			}

			event.setCancelled(true);
		}
	}
}
