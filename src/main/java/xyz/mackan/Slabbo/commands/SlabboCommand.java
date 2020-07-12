package xyz.mackan.Slabbo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.GUI.ShopDeletionGUI;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.Set;

@CommandAlias("slabbo")
@Description("Base command for Slabbo")
public class SlabboCommand extends BaseCommand {
	public Shop getLookingAtShop (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		String locationString = ShopUtil.locationToString(lookingAt.getLocation());

		if (Slabbo.shopUtil.shops.containsKey(locationString)) {
			return Slabbo.shopUtil.shops.get(locationString);
		}

		return null;
	}

	@HelpCommand
	public static void onCommand(CommandSender sender, CommandHelp help) {
		sender.sendMessage("=====[ Slabbo ]=====");
		help.showHelp();
	}

	@Subcommand("toggleadmin")
	@Description("Toggles the shop as being an item shop")
	@CommandPermission("slabbo.admin")
	public void onToggleAdmin (Player player) {
		Shop lookingAtShop = getLookingAtShop(player);
		if (lookingAtShop == null) {
			player.sendMessage(ChatColor.RED+"That's not a shop.");
			return;
		}

		lookingAtShop.admin = !lookingAtShop.admin;

		if (lookingAtShop.admin) {
			player.sendMessage(ChatColor.GREEN+"The shop is now an admin shop!");
		} else {
			player.sendMessage(ChatColor.GREEN+"The shop is no longer an admin shop!");
		}

		Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

		DataUtil.saveShops();
	}

	@Subcommand("destroy")
	@Description("Destroys a shop")
	@CommandPermission("slabbo.destroy")
	public void onModifyQuantity(Player player) {
		Shop lookingAtShop = getLookingAtShop(player);
		if (lookingAtShop == null) {
			player.sendMessage(ChatColor.RED+"That's not a shop.");
			return;
		}

		if (!lookingAtShop.ownerId.equals(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED+"That's not your shop.");
			return;
		}

		ShopDeletionGUI deletionGUI = new ShopDeletionGUI(lookingAtShop);
		deletionGUI.openInventory(player);
	}

	@Subcommand("modify")
	@Description("Modifies the shop")
	@CommandPermission("slabbo.modify")
	public class SlabboModifyCommand extends BaseCommand {
		@HelpCommand
		public void onCommand(CommandSender sender, CommandHelp help) {
			sender.sendMessage("=====[ Slabbo Modification ]=====");
			help.showHelp();
		}

		@Subcommand("buyprice")
		@Description("Sets the buying price for the shop")
		@CommandPermission("slabbo.modify.buyprice")
		public void onModifyBuyPrice(Player player, int newBuyingPrice) {
			if (newBuyingPrice < 0) {
				player.sendMessage(ChatColor.RED+"Please provide a positive buy price.");
				return;
			}

			Shop lookingAtShop = getLookingAtShop(player);
			if (lookingAtShop == null) {
				player.sendMessage(ChatColor.RED+"That's not a shop.");
				return;
			}

			if (!lookingAtShop.ownerId.equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED+"That's not your shop.");
				return;
			}

			lookingAtShop.buyPrice = newBuyingPrice;

			player.sendMessage(ChatColor.GREEN+"Buy price set to "+newBuyingPrice);

			Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();
		}

		@Subcommand("sellprice")
		@Description("Sets the selling price for the shop")
		@CommandPermission("slabbo.modify.sellprice")
		public void onModifySellPrice(Player player, int newSellingPrice) {
			if (newSellingPrice < 0) {
				player.sendMessage(ChatColor.RED+"Please provide a positive sell price.");
				return;
			}

			Shop lookingAtShop = getLookingAtShop(player);
			if (lookingAtShop == null) {
				player.sendMessage(ChatColor.RED+"That's not a shop.");
				return;
			}

			if (!lookingAtShop.ownerId.equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED+"That's not your shop.");
				return;
			}

			lookingAtShop.sellPrice = newSellingPrice;

			player.sendMessage(ChatColor.GREEN+"Sell price set to "+newSellingPrice);

			Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();
		}

		@Subcommand("quantity")
		@Description("Sets the quantity for the shop")
		@CommandPermission("slabbo.modify.quantity")
		public void onModifyQuantity(Player player, int newQuantity) {
			if (newQuantity < 0) {
				player.sendMessage(ChatColor.RED+"Please provide a positive quantity.");
				return;
			}

			Shop lookingAtShop = getLookingAtShop(player);
			if (lookingAtShop == null) {
				player.sendMessage(ChatColor.RED+"That's not a shop.");
				return;
			}

			if (!lookingAtShop.ownerId.equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED+"That's not your shop.");
				return;
			}

			lookingAtShop.quantity = newQuantity;

			player.sendMessage(ChatColor.GREEN+"Quantity set to "+newQuantity);

			Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();
		}
	}
}
