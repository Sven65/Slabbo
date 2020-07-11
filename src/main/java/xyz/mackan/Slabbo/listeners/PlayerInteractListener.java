package xyz.mackan.Slabbo.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.GUI.ShopCreationGUI;
import xyz.mackan.Slabbo.GUI.ShopUserGUI;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.utils.ShopUtil;

public class PlayerInteractListener implements Listener {
	@EventHandler
	public void onInteract (PlayerInteractEvent e) {
		ItemStack itemInHand = e.getItem();
		Player player = e.getPlayer();

		Action action = e.getAction();

		Block clickedBlock = e.getClickedBlock();

		System.out.println("Action: "+action);

		if (action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		BlockData blockData = clickedBlock.getBlockData();

		boolean isSlab = (blockData instanceof Slab);

		if (!isSlab) {
			return;
		}

		boolean openAdmin = itemInHand != null && itemInHand.getType() == Material.STICK;

		if (openAdmin) {
			ShopCreationGUI gui = new ShopCreationGUI(clickedBlock.getLocation());

			gui.openInventory(e.getPlayer());
		} else {
			String clickedLocation = ShopUtil.locationToString(clickedBlock.getLocation());
			System.out.println("clicked at "+ clickedLocation);

			Slabbo.shopUtil.shops.forEach((k, v) -> {
				System.out.println("k: "+k);
			});

			if (Slabbo.shopUtil.shops.containsKey(clickedLocation)) {
				ShopUserGUI gui = new ShopUserGUI(Slabbo.shopUtil.shops.get(clickedLocation), e.getPlayer());

				gui.openInventory(e.getPlayer());
			}
		}
	}
}
