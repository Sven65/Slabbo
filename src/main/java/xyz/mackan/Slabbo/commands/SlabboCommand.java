package xyz.mackan.Slabbo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.GUI.ShopDeletionGUI;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.importers.ImportResult;
import xyz.mackan.Slabbo.importers.UShopImporter;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.types.SlabboSound;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.io.File;
import java.util.*;

@CommandAlias("slabbo")
@Description("Base command for slabbo")
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
		//sender.sendMessage("=====[ Slabbo ]=====");
		help.showHelp();
	}

	@Subcommand("reload")
	@Description("Reloads Slabbo")
	@CommandPermission("slabbo.reload")
	public void onReload (Player player) {
		player.sendMessage(Slabbo.localeManager.getString("general.general.reloading")+" Slabbo");
		for (String shopKey : Slabbo.shopUtil.shops.keySet()) {
			Shop shop = Slabbo.shopUtil.shops.get(shopKey);

			Item droppedItem = ItemUtil.findShopItem(shop.location);

			if (droppedItem != null) {
				droppedItem.remove();
			}
		}

		Slabbo.chestLinkUtil.links = new HashMap<String, Shop>();
		Slabbo.shopUtil.shops = new HashMap<String, Shop>();
		Slabbo.shopUtil.shopsByOwnerId = new HashMap<UUID, List<Shop>>();

		Slabbo.shopUtil.loadShops();

		for (Map.Entry<String, Shop> shopEntry : Slabbo.shopUtil.shops.entrySet()) {
			String key = shopEntry.getKey();
			Shop shop = shopEntry.getValue();

			ItemUtil.dropShopItem(shop.location, shop.item);

			Slabbo.shopUtil.put(key, shop);
		}


		player.sendMessage("Slabbo "+Slabbo.localeManager.getString("general.general.reloaded")+"!");
	}

	@Subcommand("info")
	@Description("Shows information about Slabbo")
	@CommandPermission("slabbo.info")
	public void onInfo (Player sender) {
		sender.sendMessage("=====[ Slabbo Info ]=====");

		sender.sendMessage("Version: "+ Slabbo.getInstance().getDescription().getVersion());
		sender.sendMessage("Total Shops: "+Slabbo.shopUtil.shops.size());
		sender.sendMessage("Economy Provider: "+Slabbo.getEconomy().getName());

		sender.sendMessage("=====[ Slabbo Info ]=====");
	}

	@Subcommand("toggleadmin")
	@Description("Toggles the shop as being an item shop")
	@CommandPermission("slabbo.admin")
	public void onToggleAdmin (Player player) {
		Shop lookingAtShop = getLookingAtShop(player);
		if (lookingAtShop == null) {
			player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-a-shop"));
			player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);
			return;
		}

		lookingAtShop.admin = !lookingAtShop.admin;

		if (lookingAtShop.admin) {
			player.sendMessage(ChatColor.GREEN+Slabbo.localeManager.getString("success-message.general.admin-create"));
		} else {
			player.sendMessage(ChatColor.GREEN+Slabbo.localeManager.getString("success-message.general.admin-destroy"));
		}

		Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

		DataUtil.saveShops();

		player.playSound(player.getLocation(), SlabboSound.MODIFY_SUCCESS.sound, SoundCategory.BLOCKS, 1, 1);
	}

	@Subcommand("destroy")
	@Description("Destroys a shop")
	@CommandPermission("slabbo.destroy")
	public void onDestroyShop(Player player) {
		Shop lookingAtShop = getLookingAtShop(player);
		if (lookingAtShop == null) {
			player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-a-shop"));
			player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

			return;
		}

		boolean isShopOwner = lookingAtShop.ownerId.equals(player.getUniqueId());
		boolean canDestroyOthers = player.hasPermission("slabbo.destroy.others");

		if (!isShopOwner) {
			if (!canDestroyOthers) {
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-shop-owner"));
				player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

				return;
			}
		}

		ShopDeletionGUI deletionGUI = new ShopDeletionGUI(lookingAtShop);
		deletionGUI.openInventory(player);
	}

	@Subcommand("import")
	@Description("Imports shop from another plugin")
	@CommandPermission("slabbo.importshops")
	@CommandCompletion("ushops @importFiles")
	public void onImportShops(Player player, String type, String file) {
		File importFile = new File(Slabbo.getDataPath()+"/"+file);

		if (!importFile.exists()) {
			player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.import.file-not-found"));
			return;
		}

		ImportResult result;

		switch (type.toLowerCase()) {
			case "ushops":
				player.sendMessage(Slabbo.localeManager.getString("success-message.import.importing"));
				result = UShopImporter.importUShops(importFile);
				break;
			default:
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.import.plugin-not-supported"));
				return;
		}

		if (result == null) {
			player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.import.general-error"));
			return;
		}

		for (Shop shop : result.shops) {
			ItemUtil.dropShopItem(shop.location, shop.item);

			Slabbo.shopUtil.put(shop.getLocationString(), shop);
		}

		DataUtil.saveShops();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("count", result.shops.size());
		replacementMap.put("skipped", result.skippedShops.size());

		player.sendMessage(ChatColor.GREEN+Slabbo.localeManager.replaceKey("success-message.import.success", replacementMap));
	}

	@Subcommand("modify")
	@Description("Modifies the shop")
	@CommandPermission("slabbo.modify.self.buyprice|slabbo.modify.self.sellprice|slabbo.modify.self.quantity|slabbo.modify.others.buyprice|slabbo.modify.others.sellprice|slabbo.modify.others.quantity")
	public class SlabboModifyCommand extends BaseCommand {
		@HelpCommand
		public void onCommand(CommandSender sender, CommandHelp help) {
			//sender.sendMessage("=====[ Slabbo Modification ]=====");
			help.showHelp();
		}

		@Subcommand("buyprice")
		@Description("Sets the buying price for the shop")
		@CommandPermission("slabbo.modify.self.buyprice|slabbo.modify.others.buyprice")
		public void onModifyBuyPrice(Player player, int newBuyingPrice) {
			if (newBuyingPrice < -1) {
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.modify.invalid-buy-price"));
				player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

				return;
			}

			Shop lookingAtShop = getLookingAtShop(player);
			if (lookingAtShop == null) {
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-a-shop"));
				player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

				return;
			}

			boolean isShopOwner = lookingAtShop.ownerId.equals(player.getUniqueId());
			boolean canModifyOthers = player.hasPermission("slabbo.modify.others.buyprice");

			if (!isShopOwner) {
				if (!canModifyOthers) {
					player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-shop-owner"));
					player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

					return;
				}
			}

			lookingAtShop.buyPrice = newBuyingPrice;

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("price", newBuyingPrice);

			player.sendMessage(ChatColor.GREEN+Slabbo.localeManager.replaceKey("success-message.modify.buyprice-set", replacementMap));

			Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), SlabboSound.MODIFY_SUCCESS.sound, SoundCategory.BLOCKS, 1, 1);

		}

		@Subcommand("sellprice")
		@Description("Sets the selling price for the shop")
		@CommandPermission("slabbo.modify.self.sellprice|slabbo.modify.others.sellprice")
		public void onModifySellPrice(Player player, int newSellingPrice) {
			if (newSellingPrice < -1) {
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.modify.invalid-sell-price"));
				player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

				return;
			}

			Shop lookingAtShop = getLookingAtShop(player);
			if (lookingAtShop == null) {
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-a-shop"));
				player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

				return;
			}

			boolean isShopOwner = lookingAtShop.ownerId.equals(player.getUniqueId());
			boolean canModifyOthers = player.hasPermission("slabbo.modify.others.sellprice");

			if (!isShopOwner) {
				if (!canModifyOthers) {
					player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-shop-owner"));
					player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

					return;
				}
			}

			lookingAtShop.sellPrice = newSellingPrice;

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("price", newSellingPrice);

			player.sendMessage(ChatColor.GREEN+Slabbo.localeManager.replaceKey("success-message.modify.sellprice-set", replacementMap));

			Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), SlabboSound.MODIFY_SUCCESS.sound, SoundCategory.BLOCKS, 1, 1);

		}

		@Subcommand("quantity")
		@Description("Sets the quantity for the shop")
		@CommandPermission("slabbo.modify.self.quantity|slabbo.modify.others.quantity")
		public void onModifyQuantity(Player player, int newQuantity) {
			if (newQuantity < 0) {
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.modify.invalid-quantity"));
				player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

				return;
			}

			Shop lookingAtShop = getLookingAtShop(player);
			if (lookingAtShop == null) {
				player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-a-shop"));
				player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

				return;
			}

			boolean isShopOwner = lookingAtShop.ownerId.equals(player.getUniqueId());
			boolean canModifyOthers = player.hasPermission("slabbo.modify.others.quantity");

			if (!isShopOwner) {
				if (!canModifyOthers) {
					player.sendMessage(ChatColor.RED+Slabbo.localeManager.getString("error-message.general.not-shop-owner"));
					player.playSound(player.getLocation(), SlabboSound.BLOCKED.sound, SoundCategory.BLOCKS, 1, 1);

					return;
				}
			}

			lookingAtShop.quantity = newQuantity;

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("quantity", newQuantity);

			player.sendMessage(ChatColor.GREEN+Slabbo.localeManager.replaceKey("success-message.modify.quantity-set", replacementMap));

			Slabbo.shopUtil.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), SlabboSound.MODIFY_SUCCESS.sound, SoundCategory.BLOCKS, 1, 1);

		}
	}
}
