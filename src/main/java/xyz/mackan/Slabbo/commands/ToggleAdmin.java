package xyz.mackan.Slabbo.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.Set;

public class ToggleAdmin implements CommandExecutor {
	@Override
	public boolean onCommand (CommandSender commandSender, Command command, String s, String[] strings) {
		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage(ChatColor.RED+"You can only execute this command as a player.");
			return true;
		}

		if (!Slabbo.getPermissions().has(commandSender, "slabbo.admin")) {
			commandSender.sendMessage(ChatColor.RED+"You don't have permission to use this command.");
			return false;
		}

		Player player = (Player) commandSender;

		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		BlockData blockData = lookingAt.getBlockData();

		boolean isSlab = (blockData instanceof Slab);

		if (!isSlab) {
			player.sendMessage(ChatColor.RED+"That's not a slab.");
			return true;
		}

		String locationString = ShopUtil.locationToString(lookingAt.getLocation());

		if (!Slabbo.shopUtil.shops.containsKey(locationString)) {
			player.sendMessage(ChatColor.RED+"That's not a shop.");
			return true;
		}

		Shop shop = Slabbo.shopUtil.shops.get(locationString);

		shop.admin = !shop.admin;

		if (shop.admin) {
			player.sendMessage(ChatColor.GREEN+"The shop is now an admin shop!");
		} else {
			player.sendMessage(ChatColor.GREEN+"The shop is no longer an admin shop!");
		}

		Slabbo.shopUtil.shops.put(locationString, shop);

		DataUtil.saveShops();

		return true;
	}
}
