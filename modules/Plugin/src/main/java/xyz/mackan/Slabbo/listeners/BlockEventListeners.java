package xyz.mackan.Slabbo.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.utils.Misc;
import xyz.mackan.Slabbo.manager.ShopManager;

import java.util.Iterator;
import java.util.Set;

public class BlockEventListeners implements Listener {
	public boolean isLookingAtShop (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		if (!Misc.isValidShopBlock(lookingAt)) return false;

		String locationString = ShopManager.locationToString(lookingAt.getLocation());

		if (ShopManager.shops.containsKey(locationString)) return true;

		return false;
	}

	public boolean isLookingAtLinkedChest (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		if (!ChestLinkManager.isChestLinked(lookingAt)) return false;

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
		}

		if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
			if (isLookingAtLinkedChest(e.getPlayer())) {
				e.getPlayer().sendMessage(LocaleManager.getString("error-message.chestlink.no-destroy-linked"));
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Iterator<Block> it = event.blockList().iterator();

		while (it.hasNext()) {
			Block block = it.next();

			String locationString = ShopManager.locationToString(block.getLocation());

			if (!ShopManager.shops.containsKey(locationString)) {
				continue;
			} else {
				it.remove();
			}

			if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
				if (!ChestLinkManager.isChestLinked(block)) continue;
			}

			it.remove();
		}
	}

	@EventHandler
	public void onPistonExtend (BlockPistonExtendEvent event) {
		for (Block block : event.getBlocks()){
			String locationString = ShopManager.locationToString(block.getLocation());

			if (!ShopManager.shops.containsKey(locationString)) {
				continue;
			} else {
				event.setCancelled(true);
			}

			if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
				if (!ChestLinkManager.isChestLinked(block)) continue;
			}

			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPistonRetract (BlockPistonRetractEvent event) {
		for (Block block : event.getBlocks()){
			String locationString = ShopManager.locationToString(block.getLocation());

			if (!ShopManager.shops.containsKey(locationString)) {
				continue;
			} else {
				event.setCancelled(true);
			}

			if (Slabbo.getInstance().getConfig().getBoolean("chestlinks.enabled")) {
				if (!ChestLinkManager.isChestLinked(block)) continue;
			}

			event.setCancelled(true);
		}
	}
}
