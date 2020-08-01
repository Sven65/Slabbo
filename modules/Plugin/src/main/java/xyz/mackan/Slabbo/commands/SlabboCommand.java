package xyz.mackan.Slabbo.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Optional;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.GUI.ShopDeletionGUI;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.importers.ImportResult;
import xyz.mackan.Slabbo.importers.UShopImporter;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.types.ShopLimit;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.utils.Misc;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@CommandAlias("slabbo")
@Description("Base command for slabbo")
public class SlabboCommand extends BaseCommand {
	public BaseComponent getListComponent (Player player, List<String> rows, int page, String command) {
		int perPage = 10;

		int pageCount = (int) Math.ceil((double)rows.size() / (double)perPage);

		List<String> subList = Misc.getPage(rows, page, perPage);

		TextComponent component = new TextComponent("");

		for (String row : subList) {
			component.addExtra("\n"+row);
		}

		TextComponent previousPage = new TextComponent("<<<");
		TextComponent nextPage = new TextComponent(">>>");

		String previousPageCommand = String.format("%s %s", command, page - 1);
		String nextPageCommand = String.format("%s %s", command, page + 1);

		if (page + 1 <= pageCount) {
			nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, nextPageCommand));

			BaseComponent[] hoverEventComponents = new BaseComponent[] {
					new TextComponent(LocaleManager.getString("general.general.next-page"))
			};

			nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents));
		} else {
			nextPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
		}

		if (page > Math.max(pageCount - 1, 1)) {
			previousPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, previousPageCommand));

			BaseComponent[] hoverEventComponents = new BaseComponent[] {
					new TextComponent(LocaleManager.getString("general.general.previous-page"))
			};

			previousPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents));
		} else {
			previousPage.setColor(net.md_5.bungee.api.ChatColor.GRAY);
		}

		TextComponent pager = new TextComponent("\n");

		pager.addExtra(previousPage);

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("page", page);
		replacementMap.put("pageCount", pageCount);

		String pagerString = LocaleManager.replaceKey("general.general.pager", replacementMap);

		pager.addExtra(" "+pagerString+" ");

		pager.addExtra(nextPage);

		component.addExtra(pager);

		return component;

		//player.spigot().sendMessage(component);
	}

	ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();

	boolean shouldReturn () {
		return true;
	}

	public Shop getLookingAtShop (Player player) {
		Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);

		String locationString = ShopManager.locationToString(lookingAt.getLocation());

		if (ShopManager.shops.containsKey(locationString)) {
			return ShopManager.shops.get(locationString);
		}

		return null;
	}

	@HelpCommand
	@CatchUnknown
	public static void onCommand(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("reload")
	@Description("Reloads Slabbo")
	@CommandPermission("slabbo.reload")
	public void onReload (Player player) {
		player.sendMessage(LocaleManager.getString("general.general.reloading")+" Slabbo");

		Slabbo.getInstance().reloadConfig();

		ItemUtil.removeShopItems(player.getWorld());

		ChestLinkManager.links.clear();

		ShopManager.clearShops();

		ShopManager.loadShops();

		for (Map.Entry<String, Shop> shopEntry : ShopManager.shops.entrySet()) {
			Shop shop = shopEntry.getValue();

			ItemUtil.dropShopItem(shop.location, shop.item, shop.quantity);
		}


		player.sendMessage("Slabbo "+LocaleManager.getString("general.general.reloaded")+"!");
	}

	@Subcommand("info")
	@Description("Shows information about Slabbo")
	@CommandPermission("slabbo.info")
	public void onInfo (Player sender) {
		sender.sendMessage("=====[ Slabbo Info ]=====");

		sender.sendMessage("Version: "+ Slabbo.getInstance().getDescription().getVersion());
		sender.sendMessage("Total Shops: "+ShopManager.shops.size());
		sender.sendMessage("Economy Provider: "+Slabbo.getEconomy().getName());

		sender.sendMessage("=====[ Slabbo Info ]=====");
	}

	@Subcommand("admin")
	@Description("Admin shop commands")
//	@CommandPermission("slabbo.admin.help")
	public class SlabboAdminCommand extends BaseCommand {

		@Subcommand("toggle")
		@Description("Toggles the shop as being an admin shop")
		@CommandPermission("slabbo.admin.toggle")
		@Conditions("lookingAtShop")
		public void onToggleAdmin (Player player, SlabboContextResolver slabboContextResolver) {
			Shop lookingAtShop = slabboContextResolver.shop;

			lookingAtShop.admin = !lookingAtShop.admin;

			if (lookingAtShop.admin) {
				player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.admin-create"));
			} else {
				player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.admin-destroy"));
			}

			ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
		}

		@Subcommand("limit")
		@Description("Commands for setting the shop to have a limited stock")
		@CommandPermission("slabbo.admin.limit|slabbo.admin.limit.toggle|slabbo.admin.limit.time|slabbo.admin.limit.stock|slabbo.admin.limit.stock.buy|slabbo.admin.limit.stock.sell")
		public class SlabboAdminLimitCommand extends BaseCommand {

			@Subcommand("toggle")
			@Description("Toggles the admin shop as having limited stock")
			@CommandPermission("slabbo.admin.limit.toggle")
			@Conditions("lookingAtShop|isAdminShop")
			public void onToggleLimit (Player player, SlabboContextResolver slabboContext) {
				Shop lookingAtShop = slabboContext.shop;

				ShopLimit limit = lookingAtShop.shopLimit;

				if (lookingAtShop.shopLimit == null) {
					limit = new ShopLimit(0, 0, 0, 0L, false);
				}

				limit.enabled = !limit.enabled;

				lookingAtShop.shopLimit = limit;

				if (limit.enabled) {
					limit.restock();
					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.limited-stock.create"));
				} else {
					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.limited-stock.destroy"));
				}

				ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

				DataUtil.saveShops();

				player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
			}

			@Subcommand("stock")
			@Description("Commands for setting the limited stocks")
			@CommandPermission("slabbo.admin.limit.stock|slabbo.admin.limit.stock.buy|slabbo.admin.limit.stock.sell")
			public class SlabboAdminLimitStockCommand extends BaseCommand {

				@Subcommand("buy")
				@Description("Sets the limited buy stock the shop has")
				@CommandPermission("slabbo.admin.limit.stock.buy")
				@Conditions("lookingAtShop|isAdminShop")
				public void onSetBuyStock (Player player, SlabboContextResolver slabboContextResolver, int stock) {
					Shop lookingAtShop = slabboContextResolver.shop;

					ShopLimit limit = lookingAtShop.shopLimit;

					if (lookingAtShop.shopLimit == null) {
						limit = new ShopLimit(0, 0, 0, 0L, false);
					}

					limit.buyStock = stock;

					limit.restock();

					lookingAtShop.shopLimit = limit;

					player.sendMessage(ChatColor.GREEN + LocaleManager.replaceSingleKey("success-message.general.limited-stock.set-buy-stock", "stock", stock));

					ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

					DataUtil.saveShops();

					player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
				}

				@Subcommand("sell")
				@Description("Sets the limited sell stock the shop has")
				@CommandPermission("slabbo.admin.limit.stock.sell")
				@Conditions("lookingAtShop|isAdminShop")
				public void onSetSellStock (Player player, SlabboContextResolver slabboContextResolver, int stock) {
					Shop lookingAtShop = slabboContextResolver.shop;

					ShopLimit limit = lookingAtShop.shopLimit;

					if (lookingAtShop.shopLimit == null) {
						limit = new ShopLimit(0, 0, 0, 0L, false);
					}

					limit.sellStock = stock;

					limit.restock();

					lookingAtShop.shopLimit = limit;

					player.sendMessage(ChatColor.GREEN + LocaleManager.replaceSingleKey("success-message.general.limited-stock.set-sell-stock", "stock", stock));

					ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

					DataUtil.saveShops();

					player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
				}

			}

			@Subcommand("time")
			@Description("Sets the time before the shop restocks, in seconds")
			@CommandPermission("slabbo.admin.limit.time")
			@Conditions("lookingAtShop|isAdminShop")
			public void onSetTime (Player player, SlabboContextResolver slabboContextResolver, int time) {
				Shop lookingAtShop = slabboContextResolver.shop;

				ShopLimit limit = lookingAtShop.shopLimit;

				if (lookingAtShop.shopLimit == null) {
					limit = new ShopLimit(0, 0, 0, 0L, false);
				}

				limit.restockTime = time;

				lookingAtShop.shopLimit = limit;

				player.sendMessage(ChatColor.GREEN+LocaleManager.replaceSingleKey("success-message.general.limited-stock.set-time", "time", time));

				ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

				DataUtil.saveShops();

				player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
			}
		}

	}

	@Subcommand("destroy")
	@Description("Destroys a shop")
	@CommandPermission("slabbo.destroy.self")
	@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.destroy.others")
	public void onDestroyShop(Player player, SlabboContextResolver slabboContextResolver) {
		Shop lookingAtShop = slabboContextResolver.shop;

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
			player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.import.file-not-found"));
			return;
		}

		ImportResult result;

		switch (type.toLowerCase()) {
			case "ushops":
				player.sendMessage(LocaleManager.getString("success-message.import.importing"));
				result = UShopImporter.importUShops(importFile);
				break;
			default:
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.import.plugin-not-supported"));
				return;
		}

		if (result == null) {
			player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.import.general-error"));
			return;
		}

		for (Shop shop : result.shops) {
			ItemUtil.dropShopItem(shop.location, shop.item, shop.quantity);

			ShopManager.put(shop.getLocationString(), shop);
		}

		DataUtil.saveShops();

		HashMap<String, Object> replacementMap = new HashMap<String, Object>();

		replacementMap.put("count", result.shops.size());
		replacementMap.put("skipped", result.skippedShops.size());

		player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.import.success", replacementMap));
	}

	@Subcommand("save")
	@Description("Saves slabbo shops")
	@CommandPermission("slabbo.save")
	public void onSave (Player player) {
		DataUtil.saveShops();

		player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.shops-saved"));
	}

	@Subcommand("modify")
	@Description("Modifies the shop")
	@CommandPermission("slabbo.modify.help")
	public class SlabboModifyCommand extends BaseCommand {

		@HelpCommand
		public void onCommand(CommandSender sender, CommandHelp help) {
			help.showHelp();
		}

		@Subcommand("buyprice")
		@Description("Sets the buying price for the shop")
		@CommandPermission("slabbo.modify.self.buyprice|slabbo.modify.others.buyprice")
		@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.modify.others.buyprice")
		public void onModifyBuyPrice(Player player, SlabboContextResolver slabboContextResolver, int newBuyingPrice) {
			if (newBuyingPrice < -1) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.modify.invalid-buy-price"));
				player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);

				return;
			}

			Shop lookingAtShop = slabboContextResolver.shop;

			lookingAtShop.buyPrice = newBuyingPrice;

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("price", LocaleManager.getCurrencyString(newBuyingPrice));

			player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.modify.buyprice-set", replacementMap));

			ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);

		}

		@Subcommand("sellprice")
		@Description("Sets the selling price for the shop")
		@CommandPermission("slabbo.modify.self.sellprice|slabbo.modify.others.sellprice")
		@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.modify.others.sellprice")
		public void onModifySellPrice(Player player, SlabboContextResolver slabboContextResolver, int newSellingPrice) {
			if (newSellingPrice < -1) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.modify.invalid-sell-price"));
				player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);

				return;
			}

			Shop lookingAtShop = slabboContextResolver.shop;

			lookingAtShop.sellPrice = newSellingPrice;

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("price", LocaleManager.getCurrencyString(newSellingPrice));

			player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.modify.sellprice-set", replacementMap));

			ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);

		}

		@Subcommand("quantity")
		@Description("Sets the quantity for the shop")
		@CommandPermission("slabbo.modify.self.quantity|slabbo.modify.others.quantity")
		@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.modify.others.quantity")
		public void onModifyQuantity(Player player, SlabboContextResolver slabboContextResolver, int newQuantity) {
			if (newQuantity < 0) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.modify.invalid-quantity"));
				player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);

				return;
			}

			Shop lookingAtShop = slabboContextResolver.shop;

			lookingAtShop.quantity = newQuantity;

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("quantity", newQuantity);

			player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.modify.quantity-set", replacementMap));

			ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
		}

		@Subcommand("owner")
		@Description("Sets the owner for the shop")
		@CommandPermission("slabbo.modify.admin.owner")
		@CommandCompletion("@players")
		@Conditions("lookingAtShop")
		public void onChangeOwner (Player player, SlabboContextResolver slabboContextResolver, OfflinePlayer newOwner) {

			Shop lookingAtShop = slabboContextResolver.shop;

			UUID newOwnerID = newOwner.getUniqueId();

			lookingAtShop.ownerId = newOwnerID;

			ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("owner", newOwner.getName());

			player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.modify.owner-set", replacementMap));
		}

		@Subcommand("stock")
		@Description("Sets the stock for the shop")
		@CommandPermission("slabbo.modify.admin.stock")
		@Conditions("lookingAtShop")
		public void onSetStock (Player player, SlabboContextResolver slabboContextResolver, int newStock) {

			Shop lookingAtShop = slabboContextResolver.shop;

			lookingAtShop.stock = newStock;

			ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("stock", newStock);

			player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.modify.stock-set", replacementMap));
		}

		@Subcommand("stock")
		@Description("Sets the sellers note for the shop")
		@CommandPermission("slabbo.modify.self.note|slabbo.modify.others.note")
		@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.modify.others.note")
		public void onSetNote (Player player, SlabboContextResolver slabboContextResolver, String note) {
			if (note.equals("")) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.modify.invalid-note"));
				player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);

				return;
			}

			Shop lookingAtShop = slabboContextResolver.shop;

			lookingAtShop.note = note;

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("note", note);

			player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.modify.note-set", replacementMap));

			ShopManager.shops.put(lookingAtShop.getLocationString(), lookingAtShop);

			DataUtil.saveShops();

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
		}

	}

	@Subcommand("list")
	@Description("Commands for listing Slabbo shops")
	@CommandPermission("slabbo.list.all|slabbo.list.self")
	public class SlabboListCommand extends BaseCommand {

		@HelpCommand
		public void onCommand(CommandSender sender, CommandHelp help) {
			help.showHelp();
		}

		@Subcommand("all radius")
		@Description("Lists all the Slabbo shops in a radius")
		@CommandPermission("slabbo.list.all")
		public void onListAllRadius (Player player, double radius, @Optional String page) {
			int listPage = 1;

			if (page == null || page.equals("")) {
				try { listPage = Integer.parseInt(page); } catch (Exception e) {}
			}

			Location playerLocation = player.getLocation();

			List<String> rows = ShopManager.shops.values()
					.stream()
					.filter(shop -> playerLocation.distance(shop.location) <= radius)
					.map(shop -> shop.getInfoString())
					.collect(Collectors.toList());

			if (rows.size() <= 0) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-shops-found"));
				return;
			}

			TextComponent component = new TextComponent("=== [Slabbo Shops] === ");

			component.addExtra(getListComponent(player, rows, listPage, "/slabbo list all radius "+radius));

			player.spigot().sendMessage(component);
		}

		@Subcommand("all")
		@Description("Lists all the Slabbo shops")
		@CommandPermission("slabbo.list.all")
		public void onListAll (Player player, @Optional String page) {
			int listPage = 1;

			if (page != null && !page.equals("")) {
				try { listPage = Integer.parseInt(page); } catch (Exception e) {}
			}

			List<String> rows = ShopManager.shops.values()
					.stream()
					.map(shop -> shop.getInfoString())
					.collect(Collectors.toList());

			if (rows.size() <= 0) {
				player.sendMessage(net.md_5.bungee.api.ChatColor.RED+LocaleManager.getString("error-message.general.no-shops-found"));
				return;
			}

			TextComponent component = new TextComponent("=== [Slabbo Shops] === ");

			component.addExtra(getListComponent(player, rows, listPage, "/slabbo list all"));

			player.spigot().sendMessage(component);
		}

		@Subcommand("mine radius")
		@Description("Lists all the Slabbo shops you own in a radius")
		@CommandPermission("slabbo.list.self")
		public void onListMineRadius (Player player, double radius, @Optional String page) {
			int listPage = 1;

			if (page == null || page.equals("")) {
				try { listPage = Integer.parseInt(page); } catch (Exception e) {}
			}

			Location playerLocation = player.getLocation();

			List<String> rows = ShopManager.shops.values()
					.stream()
					.filter(shop -> playerLocation.distance(shop.location) <= radius && shop.ownerId.equals(player.getUniqueId()))
					.map(shop -> shop.getInfoString())
					.collect(Collectors.toList());

			if (rows.size() <= 0) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-shops-found"));
				return;
			}

			TextComponent component = new TextComponent("=== [Slabbo Shops] === ");

			component.addExtra(getListComponent(player, rows, listPage, "/slabbo list all mine radius "+radius));

			player.spigot().sendMessage(component);
		}

		@Subcommand("mine")
		@Description("Lists all the Slabbo shops you own")
		@CommandPermission("slabbo.list.self")
		public void onListMine (Player player, @Optional String page) {
			int listPage = 1;

			if (page != null && !page.equals("")) {
				try { listPage = Integer.parseInt(page); } catch (Exception e) {}
			}

			List<String> rows = ShopManager.shops.values()
					.stream()
					.filter(shop -> shop.ownerId.equals(player.getUniqueId()))
					.map(shop -> shop.getInfoString())
					.collect(Collectors.toList());

			if (rows.size() <= 0) {
				player.sendMessage(net.md_5.bungee.api.ChatColor.RED+LocaleManager.getString("error-message.general.no-shops-found"));
				return;
			}

			TextComponent component = new TextComponent("=== [Slabbo Shops] === ");

			component.addExtra(getListComponent(player, rows, listPage, "/slabbo list mine"));

			player.spigot().sendMessage(component);
		}
	}

	@Subcommand("shopcommands")
	@Description("For adding commands to Slabbo shops")
//	@CommandPermission("slabbo.shopcommands")
	public class SlabboShopCommandsCommand extends BaseCommand {
		@Subcommand("add")
		@Description("Adds commands to the shop")
//		@CommandPermission("slabbo.shopcommands.edit.self.buy|slabbo.shopcommands.edit.self.buyslabbo.shopcommands.edit.others.buy")
		public class SlabboShopCommandsAddCommand extends BaseCommand {
			@Subcommand("buy")
			@Description("Adds a command to the shop which gets ran on buying")
			@CommandPermission("slabbo.shopcommands.edit.self.buy|slabbo.shopcommands.edit.others.buy")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.edit.others.buy")
			public void onAddBuyCommand(Player player, SlabboContextResolver slabboContextResolver, String command) {
				if (command == null) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-command-entered"));
				}

				Shop lookingAtShop = slabboContextResolver.shop;

				if (lookingAtShop.commandList == null) {
					lookingAtShop.commandList = new Shop.CommandList();
				}

				player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.shop-commands.added-command"));

				lookingAtShop.commandList.buyCommands.add(command);

				ShopManager.updateShop(lookingAtShop);

				DataUtil.saveShops();
			}

			@Subcommand("sell")
			@Description("Adds a command to the shop which gets ran on selling")
			@CommandPermission("slabbo.shopcommands.edit.self.sell|slabbo.shopcommands.edit.others.sell")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.edit.others.sell")
			public void onAddSellCommand(Player player, SlabboContextResolver slabboContextResolver, String command) {
				if (command == null) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-command-entered"));
				}

				Shop lookingAtShop = slabboContextResolver.shop;

				player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.shop-commands.added-command"));

				lookingAtShop.commandList.sellCommands.add(command);

				ShopManager.updateShop(lookingAtShop);

				DataUtil.saveShops();
			}
		}

		@Subcommand("remove")
		@Description("Removes commands from the shop")
//		@CommandPermission("slabbo.commands.edit")
		public class SlabboShopCommandsRemoveCommand extends BaseCommand {
			@Subcommand("buy")
			@Description("Removes a command from the shop which got ran on buying")
			@CommandPermission("slabbo.shopcommands.edit.self.buy|slabbo.shopcommands.edit.others.buy")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.edit.others.buy")
			public void onRemoveBuyCommand(Player player, SlabboContextResolver slabboContextResolver, int index) {
				int newIndex = index - 1;

				if (newIndex < 0) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.invalid-index"));
					return;
				}

				Shop lookingAtShop = slabboContextResolver.shop;

				if (lookingAtShop.commandList == null || lookingAtShop.commandList.buyCommands.size() <= 0) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-buy-commands-in-shop"));
					player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
					return;
				}

				if (lookingAtShop.commandList.buyCommands.size() < newIndex) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.invalid-index"));
					return;
				}

				lookingAtShop.commandList.buyCommands.remove(newIndex);

				ShopManager.updateShop(lookingAtShop);
				DataUtil.saveShops();

				player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.shop-commands.removed-command"));
			}

			@Subcommand("sell")
			@Description("Removes a command from the shop which got ran on selling")
			@CommandPermission("slabbo.shopcommands.edit.self.sell|slabbo.shopcommands.edit.others.sell")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.edit.others.sell")
			public void onRemoveSellCommand(Player player, SlabboContextResolver slabboContextResolver, int index) {
				int newIndex = index - 1;

				if (newIndex < 0) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.invalid-index"));
					return;
				}

				Shop lookingAtShop = slabboContextResolver.shop;

				if (lookingAtShop.commandList == null || lookingAtShop.commandList.sellCommands.size() <= 0) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-sell-commands-in-shop"));
					player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
					return;
				}

				if (lookingAtShop.commandList.sellCommands.size() < newIndex) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.invalid-index"));
					return;
				}

				lookingAtShop.commandList.sellCommands.remove(newIndex);

				ShopManager.updateShop(lookingAtShop);
				DataUtil.saveShops();

				player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.shop-commands.removed-command"));
			}
		}

		@Subcommand("list")
		@Description("Lists the commands the shop has")
//		@CommandPermission("slabbo.commands.list")
		public class SlabboShopCommandsListCommand extends BaseCommand {
			@Subcommand("buy")
			@Description("Lists the commands that gets ran when someone buys from the shop")
			//@Conditions("hasEitherPermission:permissions=slabbo.shopcommands.list.self.buy|slabbo.shopcommands.list.others.buy")
			@CommandPermission("slabbo.shopcommands.list.self.buy")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.list.others.buy")
			public void onListBuyCommands (Player player, SlabboContextResolver slabboContextResolver, @Optional String page) {
				Shop lookingAtShop = slabboContextResolver.shop;

				if (lookingAtShop.commandList == null || lookingAtShop.commandList.buyCommands.size() <= 0) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-buy-commands-in-shop"));
					player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
					return;
				}

				int listPage = 1;

				if (page != null && !page.equals("")) {
					try { listPage = Integer.parseInt(page); } catch (Exception e) {}
				}

				AtomicInteger i = new AtomicInteger();
				List<String> rows = lookingAtShop.commandList.buyCommands
						.stream()
						.map(command -> {
							i.getAndIncrement();
							return String.format("§8- §7[%s] §b%s", i.toString(), command);
						})
						.collect(Collectors.toList());

				if (rows.size() <= 0) {
					player.sendMessage(net.md_5.bungee.api.ChatColor.RED+LocaleManager.getString("error-message.general.no-shops-found"));
					return;
				}

				TextComponent component = new TextComponent("=== [Slabbo Shop Commands] === ");

				component.addExtra(getListComponent(player, rows, listPage, "/slabbo shopcommands list buy"));

				player.spigot().sendMessage(component);
			}

			@Subcommand("sell")
			@Description("Lists the commands that gets ran when someone sells to the shop")
			@CommandPermission("slabbo.shopcommands.list.self.sell,slabbo.shopcommands.list.others.sell")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.list.others.sell")
			public void onListSellCommands (Player player, SlabboContextResolver slabboContextResolver, @Optional String page) {
				Shop lookingAtShop = slabboContextResolver.shop;

				if (lookingAtShop.commandList == null || lookingAtShop.commandList.sellCommands.size() <= 0) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-buy-commands-in-shop"));
					player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);
					return;
				}

				int listPage = 1;

				if (page != null && !page.equals("")) {
					try { listPage = Integer.parseInt(page); } catch (Exception e) {}
				}

				AtomicInteger i = new AtomicInteger();
				List<String> rows = lookingAtShop.commandList.sellCommands
						.stream()
						.map(command -> {
							i.getAndIncrement();
							return String.format("§8- §7[%s] §b%s", i.toString(), command);
						})
						.collect(Collectors.toList());

				if (rows.size() <= 0) {
					player.sendMessage(net.md_5.bungee.api.ChatColor.RED+LocaleManager.getString("error-message.general.no-shops-found"));
					return;
				}

				TextComponent component = new TextComponent("=== [Slabbo Shop Commands] === ");

				component.addExtra(getListComponent(player, rows, listPage, "/slabbo shopcommands list sell"));

				player.spigot().sendMessage(component);
			}
		}
	}

}
