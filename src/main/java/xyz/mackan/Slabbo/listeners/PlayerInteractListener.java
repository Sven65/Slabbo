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

public class PlayerInteractListener implements Listener {
	@EventHandler
	public void onInteract (PlayerInteractEvent e) {
		ItemStack itemInHand = e.getItem();
		Player player = e.getPlayer();

		if (itemInHand == null || itemInHand.getType() == Material.AIR) {
			return;
		}

		if (itemInHand.getType() == Material.STICK) {
			Action action = e.getAction();

			Block clickedBlock = e.getClickedBlock();

			if (action != Action.RIGHT_CLICK_BLOCK) {
				return;
			}

			BlockData blockData = clickedBlock.getBlockData();

			boolean isSlab = (blockData instanceof Slab);

			if (!isSlab) {
				return;
			}

			ShopCreationGUI gui = new ShopCreationGUI(clickedBlock.getLocation());

			gui.openInventory(e.getPlayer());

			//player.sendMessage("Material: "+clickedBlock.getBlockData().getMaterial());
		}
	}
}
