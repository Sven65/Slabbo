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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.GUI.ShopAdminGUI;
import xyz.mackan.Slabbo.GUI.ShopCreationGUI;
import xyz.mackan.Slabbo.GUI.ShopDeletionGUI;
import xyz.mackan.Slabbo.GUI.ShopUserGUI;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.importers.ImportResult;
import xyz.mackan.Slabbo.importers.UShopImporter;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.types.ShopLimit;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.utils.Misc;

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

	@HelpCommand
	@CatchUnknown
	public static void onCommand(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}

	@Subcommand("reload")
	@Description("Reloads Slabbo")
	@CommandPermission("slabbo.reload")
	public void onReload(Player player) {
		boolean shouldSave = false;

		player.sendMessage(LocaleManager.getString("general.general.reloading") + " Slabbo");

		Slabbo.getInstance().reloadConfig();
		ItemUtil.removeShopItems(player.getWorld());
		Slabbo.getInstance().getChestLinkManager().clearLinks();
		Slabbo.getInstance().getShopManager().reloadShops();

		for (Map.Entry<String, Shop> shopEntry : Slabbo.getInstance().getShopManager().getAllShops().entrySet()) {
			Shop shop = shopEntry.getValue();
			if (shop.location == null) {
				Location shopLocation = ShopManager.fromString(shopEntry.getKey());
				shop.location = shopLocation;
				shouldSave = true;
			}
			ItemUtil.dropShopItem(shop.location, shop.item, shop.quantity);
		}

		if (shouldSave) {
			for (Shop shop : Slabbo.getInstance().getShopManager().getAllShops().values()) {
				Slabbo.getInstance().getShopManager().updateShop(shop);
			}
		}

		player.sendMessage("Slabbo " + LocaleManager.getString("general.general.reloaded") + "!");
	}

	@Subcommand("info")
	@Description("Shows information about Slabbo")
	@CommandPermission("slabbo.info")
	public void onInfo (Player sender) {
		sender.sendMessage("=====[ Slabbo Info ]=====");

		String version = Slabbo.getInstance().getDescription().getVersion();
		int totalShops = Slabbo.getInstance().getShopManager().getAllShops().size();
		String economyProvider = Slabbo.getEconomy().getName();

		sender.sendMessage("Slabbo Version: " + version);
		sender.sendMessage("Slabbo Total Shops: " + totalShops);
		sender.sendMessage("Slabbo Economy Provider: " + economyProvider);

		sender.sendMessage("=====[ Slabbo Info ]=====");
	}

	@Subcommand("admin")
	@Description("Admin shop commands")
	public class SlabboAdminCommand extends BaseCommand {

		@HelpCommand
		@CatchUnknown
		public void onCommand(CommandSender sender, CommandHelp help) {
			help.showHelp();
		}

		@Subcommand("toggle")
		@Description("Toggles the shop as being an admin shop")
		@CommandPermission("slabbo.admin.toggle")
		@Conditions("lookingAtShop")
		public void onToggleAdmin(Player player, SlabboContextResolver slabboContextResolver) {
			Shop lookingAtShop = slabboContextResolver.shop;

			lookingAtShop.admin = !lookingAtShop.admin;

			if (lookingAtShop.admin) {
				player.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.general.admin-create"));
			} else {
				player.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.general.admin-destroy"));
			}

			// Use the ShopManager's public API instead of direct map access
			Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

			// Remove DataUtil.saveShops(); - ShopManager handles persistence

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
		}

		@Subcommand("toggle virtual")
		@Description("Toggles a virtual shop as being an admin shop")
		@CommandPermission("slabbo.admin.toggle.virtual")
		@CommandCompletion("@virtualShopNames")
		public void onToggleVirtualAdmin(Player player, String name) {
			String loweredName = name.toLowerCase();

			// Use the ShopManager instance to get the shop
			Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

			if (shop == null) {
				player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.general.shop-does-not-exist"));
				return;
			}

			shop.admin = !shop.admin;

			if (shop.admin) {
				player.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.general.admin-create"));
			} else {
				player.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.general.admin-destroy"));
			}

			// Update the shop using the ShopManager API
			Slabbo.getInstance().getShopManager().updateShop(shop);

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
		}

		@Subcommand("limit")
		@Description("Commands for setting the shop to have a limited stock")
//		@CommandPermission("slabbo.admin.limit|slabbo.admin.limit.toggle|slabbo.admin.limit.time|slabbo.admin.limit.stock|slabbo.admin.limit.stock.buy|slabbo.admin.limit.stock.sell")
		public class SlabboAdminLimitCommand extends BaseCommand {

			@HelpCommand
			@CatchUnknown
			public void onCommand(CommandSender sender, CommandHelp help) {
				help.showHelp();
			}

			@Subcommand("toggle")
			@Description("Toggles the admin shop as having limited stock")
			@CommandPermission("slabbo.admin.limit.toggle")
			@Conditions("lookingAtShop|isAdminShop")
			public void onToggleLimit(Player player, SlabboContextResolver slabboContext) {
				Shop lookingAtShop = slabboContext.shop;

				ShopLimit limit = lookingAtShop.shopLimit;

				if (lookingAtShop.shopLimit == null) {
					limit = new ShopLimit(0, 0, 0, 0L, false);
				}

				limit.enabled = !limit.enabled;
				lookingAtShop.shopLimit = limit;

				if (limit.enabled) {
					limit.restock();
					player.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.general.limited-stock.create"));
				} else {
					player.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.general.limited-stock.destroy"));
				}

				Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

				player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
			}

			@Subcommand("stock")
			@Description("Commands for setting the limited stocks")
//			@CommandPermission("slabbo.admin.limit.stock|slabbo.admin.limit.stock.buy|slabbo.admin.limit.stock.sell")
			public class SlabboAdminLimitStockCommand extends BaseCommand {

				@HelpCommand
				@CatchUnknown
				public void onCommand(CommandSender sender, CommandHelp help) {
					help.showHelp();
				}

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

					Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

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

					Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

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

				Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

				player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
			}
		}

		@Subcommand("limit virtual")
		@Description("Commands for setting virtual shops to have a limited stock")
		public class SlabboAdminLimitVirtualCommand extends BaseCommand {

			@HelpCommand
			@CatchUnknown
			public void onCommand(CommandSender sender, CommandHelp help) {
				help.showHelp();
			}

			@Subcommand("toggle")
			@Description("Toggles the virtual admin shop as having limited stock")
			@CommandCompletion("@virtualAdminShopNames")
			@CommandPermission("slabbo.admin.limit.virtual.toggle")
			public void onToggleVirtualLimit (Player player, String name) {
				String loweredName = name.toLowerCase();

				Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

				if (shop == null) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
					return;
				}

				if (!shop.admin) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.not-admin-shop"));
					return;
				}

				ShopLimit limit = shop.shopLimit;

				if (shop.shopLimit == null) {
					limit = new ShopLimit(0, 0, 0, 0L, false);
				}

				limit.enabled = !limit.enabled;

				shop.shopLimit = limit;

				if (limit.enabled) {
					limit.restock();
					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.limited-stock.create"));
				} else {
					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.limited-stock.destroy"));
				}

				Slabbo.getInstance().getShopManager().updateShop(shop);

				player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
			}

			@Subcommand("stock")
			@Description("Commands for setting the limited stocks")
			public class SlabboAdminLimitVirtualStockCommand extends BaseCommand {

				@HelpCommand
				@CatchUnknown
				public void onCommand(CommandSender sender, CommandHelp help) {
					help.showHelp();
				}

				@Subcommand("buy")
				@Description("Sets the limited buy stock the virtual shop has")
				@CommandCompletion("@virtualAdminShopNames")
				@CommandPermission("slabbo.admin.limit.virtual.stock.buy")
				public void onSetVirtualBuyStock (Player player, String name, int stock) {
					String loweredName = name.toLowerCase();

					Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

					if (shop == null) {
						player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
						return;
					}

					if (!shop.admin) {
						player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.not-admin-shop"));
						return;
					}

					ShopLimit limit = shop.shopLimit;

					if (shop.shopLimit == null) {
						limit = new ShopLimit(0, 0, 0, 0L, false);
					}

					limit.buyStock = stock;

					limit.restock();

					shop.shopLimit = limit;

					player.sendMessage(ChatColor.GREEN + LocaleManager.replaceSingleKey("success-message.general.limited-stock.set-buy-stock", "stock", stock));

					Slabbo.getInstance().getShopManager().updateShop(shop);

					player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
				}

				@Subcommand("sell")
				@Description("Sets the limited sell stock the virtual shop has")
				@CommandCompletion("@virtualAdminShopNames")
				@CommandPermission("slabbo.admin.limit.virtual.stock.sell")
				public void onSetVirtualSellStock (Player player, String name, int stock) {
					String loweredName = name.toLowerCase();

					Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

					if (shop == null) {
						player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
						return;
					}

					if (!shop.admin) {
						player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.not-admin-shop"));
						return;
					}

					ShopLimit limit = shop.shopLimit;

					if (shop.shopLimit == null) {
						limit = new ShopLimit(0, 0, 0, 0L, false);
					}

					limit.sellStock = stock;

					limit.restock();

					shop.shopLimit = limit;

					player.sendMessage(ChatColor.GREEN + LocaleManager.replaceSingleKey("success-message.general.limited-stock.set-sell-stock", "stock", stock));

					Slabbo.getInstance().getShopManager().updateShop(shop);

					player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
				}

			}

			@Subcommand("time")
			@Description("Sets the time before the virtual shop restocks, in seconds")
			@CommandCompletion("@virtualAdminShopNames")
			@CommandPermission("slabbo.admin.limit.virtual.time")
			public void onSetVirtualTime (Player player, String name, int time) {
				String loweredName = name.toLowerCase();

				Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

				if (shop == null) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
					return;
				}

				if (!shop.admin) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.not-admin-shop"));
					return;
				}

				ShopLimit limit = shop.shopLimit;

				if (shop.shopLimit == null) {
					limit = new ShopLimit(0, 0, 0, 0L, false);
				}

				limit.restockTime = time;

				shop.shopLimit = limit;

				player.sendMessage(ChatColor.GREEN+LocaleManager.replaceSingleKey("success-message.general.limited-stock.set-time", "time", time));

				Slabbo.getInstance().getShopManager().updateShop(shop);

				player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
			}
		}

		@Subcommand("set")
		@Description("Sets properties for the admin shop")
		public class SlabboAdminSetCommand extends BaseCommand {
			@HelpCommand
			@CatchUnknown
			public void onCommand(CommandSender sender, CommandHelp help) {
				help.showHelp();
			}

			@Subcommand("owner_name")
			@Description("Sets the name to display as the shop owner")
			@CommandPermission("slabbo.admin.set.owner_name")
			@Conditions("lookingAtShop|isAdminShop")
			public void onSetOwnerName (Player player, SlabboContextResolver slabboContext, @Optional String newName) {
				Shop lookingAtShop = slabboContext.shop;

				if (newName == null || newName.equals("")) {
					lookingAtShop.displayedOwnerName = null;

					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.owner-name-removed"));
				} else {
					lookingAtShop.displayedOwnerName = newName;

					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.owner-name-set"));
				}

				Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

				player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
			}
		}

		@Subcommand("set virtual")
		@Description("Sets properties for the virtual admin shop")
		public class SlabboAdminVirtualSetCommand extends BaseCommand {
			@HelpCommand
			@CatchUnknown
			public void onCommand(CommandSender sender, CommandHelp help) {
				help.showHelp();
			}

			@Subcommand("owner_name")
			@Description("Sets the name to display as the shop owner")
			@CommandCompletion("@virtualAdminShopNames")
			@CommandPermission("slabbo.admin.set.virtual.owner_name")
			public void onSetVirtualOwnerName (Player player, String name, @Optional String newName) {
				String loweredName = name.toLowerCase();

				Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

				if (shop == null) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
					return;
				}

				if (!shop.admin) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.not-admin-shop"));
					return;
				}

				if (newName == null || newName.equals("")) {
					shop.displayedOwnerName = null;

					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.owner-name-removed"));
				} else {
					shop.displayedOwnerName = newName;

					player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.owner-name-set"));
				}

				Slabbo.getInstance().getShopManager().updateShop(shop);

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
		File importFile = new File(Slabbo.getDataPath() + "/" + file);

		if (!importFile.exists()) {
			player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.import.file-not-found"));
			return;
		}

		ImportResult result;

		switch (type.toLowerCase()) {
			case "ushops":
				player.sendMessage(LocaleManager.getString("success-message.import.importing"));
				result = UShopImporter.importUShops(importFile);
				break;
			default:
				player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.import.plugin-not-supported"));
				return;
		}

		if (result == null) {
			player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.import.general-error"));
			return;
		}

		Map<String, Shop> importedShops = new HashMap<>();
		for (Shop shop : result.shops) {
			ItemUtil.dropShopItem(shop.location, shop.item, shop.quantity);
			importedShops.put(shop.getLocationString(), shop);
		}

		// Bulk add all shops at once
		Slabbo.getInstance().getShopManager().addAllShops(importedShops);

		HashMap<String, Object> replacementMap = new HashMap<>();
		replacementMap.put("count", result.shops.size());
		replacementMap.put("skipped", result.skippedShops.size());

		player.sendMessage(ChatColor.GREEN + LocaleManager.replaceKey("success-message.import.success", replacementMap));
	}

	@Subcommand("save")
	@Description("Saves slabbo shops")
	@CommandPermission("slabbo.save")
	public void onSave (Player player) {
		Slabbo.getInstance().getShopManager().saveShopsOnMainThread();

		player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.shops-saved"));
	}

	@Subcommand("modify")
	@Description("Modifies the shop")
	public class SlabboModifyCommand extends BaseCommand {

		@HelpCommand
		@CatchUnknown
		public void onCommand(CommandSender sender, CommandHelp help) {
			help.showHelp();
		}

		@Subcommand("buyprice")
		@Description("Sets the buying price for the shop")
		@CommandPermission("slabbo.modify.self.buyprice")
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

			Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);

		}

		@Subcommand("sellprice")
		@Description("Sets the selling price for the shop")
		@CommandPermission("slabbo.modify.self.sellprice")
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

			Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);

		}

		@Subcommand("quantity")
		@Description("Sets the quantity for the shop")
		@CommandPermission("slabbo.modify.self.quantity")
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

			Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

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

			Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

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

			Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

			HashMap<String, Object> replacementMap = new HashMap<String, Object>();

			replacementMap.put("stock", newStock);

			player.sendMessage(ChatColor.GREEN+LocaleManager.replaceKey("success-message.modify.stock-set", replacementMap));
		}

		@Subcommand("note")
		@Description("Sets the sellers note for the shop")
		@CommandPermission("slabbo.modify.self.note")
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

			Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

			player.playSound(player.getLocation(), slabboSound.getSoundByKey("MODIFY_SUCCESS"), 1, 1);
		}


	}

	@Subcommand("list")
	@Description("Commands for listing Slabbo shops")
	public class SlabboListCommand extends BaseCommand {

		@HelpCommand
		@CatchUnknown
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

			List<String> rows = Slabbo.getInstance().getShopManager().getAllShops().values()
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

			List<String> rows = Slabbo.getInstance().getShopManager().getAllShops().values()
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
		public void onListMineRadius(Player player, double radius, @Optional String page) {
			int listPage = 1;

			if (page != null && !page.equals("")) {
				try { listPage = Integer.parseInt(page); } catch (Exception e) {}
			}

			Location playerLocation = player.getLocation();

			List<String> rows = Slabbo.getInstance().getShopManager().getShopsByOwner(player.getUniqueId())
					.stream()
					.filter(shop -> playerLocation.distance(shop.location) <= radius)
					.map(Shop::getInfoString)
					.collect(Collectors.toList());

			if (rows.isEmpty()) {
				player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.general.no-shops-found"));
				return;
			}

			TextComponent component = new TextComponent("=== [Slabbo Shops] === ");
			component.addExtra(getListComponent(player, rows, listPage, "/slabbo list all mine radius " + radius));
			player.spigot().sendMessage(component);
		}

		@Subcommand("mine")
		@Description("Lists all the Slabbo shops you own")
		@CommandPermission("slabbo.list.self")
		public void onListMine(Player player, @Optional String page) {
			int listPage = 1;

			if (page != null && !page.equals("")) {
				try { listPage = Integer.parseInt(page); } catch (Exception e) {}
			}

			List<String> rows = Slabbo.getInstance().getShopManager().getShopsByOwner(player.getUniqueId())
					.stream()
					.map(Shop::getInfoString)
					.collect(Collectors.toList());

			if (rows.isEmpty()) {
				player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.general.no-shops-found"));
				return;
			}

			TextComponent component = new TextComponent("=== [Slabbo Shops] === ");
			component.addExtra(getListComponent(player, rows, listPage, "/slabbo list mine"));
			player.spigot().sendMessage(component);
		}
	}

	@Subcommand("shopcommands")
	@Description("For adding commands to Slabbo shops")
	public class SlabboShopCommandsCommand extends BaseCommand {

		@HelpCommand
		@CatchUnknown
		public void onCommand(CommandSender sender, CommandHelp help) {
			help.showHelp();
		}

		@Subcommand("add")
		@Description("Adds commands to the shop")
		public class SlabboShopCommandsAddCommand extends BaseCommand {

			@HelpCommand
			@CatchUnknown
			public void onCommand(CommandSender sender, CommandHelp help) {
				help.showHelp();
			}

			@Subcommand("buy")
			@Description("Adds a command to the shop which gets ran on buying")
			@CommandPermission("slabbo.shopcommands.edit.self.buy")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.edit.others.buy")
			public void onAddBuyCommand(Player player, SlabboContextResolver slabboContextResolver, String command) {
				if (command == null) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-command-entered"));
				}

				Shop lookingAtShop = slabboContextResolver.shop;

				if (lookingAtShop.commandList == null) {
					lookingAtShop.commandList = new Shop.CommandList();
				}

				player.sendMessage(ChatColor.GREEN+LocaleManager.replaceSingleKey("success-message.general.shop-commands.added-command", "command", command));

				lookingAtShop.commandList.buyCommands.add(command);

				Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);
			}

			@Subcommand("sell")
			@Description("Adds a command to the shop which gets ran on selling")
			@CommandPermission("slabbo.shopcommands.edit.self.sell")
			@Conditions("lookingAtShop|canExecuteOnShop:othersPerm=slabbo.shopcommands.edit.others.sell")
			public void onAddSellCommand(Player player, SlabboContextResolver slabboContextResolver, String command) {
				if (command == null) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.no-command-entered"));
				}

				Shop lookingAtShop = slabboContextResolver.shop;

				player.sendMessage(ChatColor.GREEN+LocaleManager.replaceSingleKey("success-message.general.shop-commands.removed-command", "command", command));

				lookingAtShop.commandList.sellCommands.add(command);

				Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);
			}
		}

		@Subcommand("remove")
		@Description("Removes commands from the shop")
		public class SlabboShopCommandsRemoveCommand extends BaseCommand {

			@HelpCommand
			@CatchUnknown
			public void onCommand(CommandSender sender, CommandHelp help) {
				help.showHelp();
			}

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

				if (lookingAtShop.commandList.buyCommands.size() <= newIndex) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.invalid-index"));
					return;
				}

				lookingAtShop.commandList.buyCommands.remove(newIndex);

				Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

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

				if (lookingAtShop.commandList.sellCommands.size() <= newIndex) {
					player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.invalid-index"));
					return;
				}

				lookingAtShop.commandList.sellCommands.remove(newIndex);

				Slabbo.getInstance().getShopManager().updateShop(lookingAtShop);

				player.sendMessage(ChatColor.GREEN+LocaleManager.getString("success-message.general.shop-commands.removed-command"));
			}
		}

		@Subcommand("list")
		@Description("Lists the commands the shop has")
		public class SlabboShopCommandsListCommand extends BaseCommand {

			@HelpCommand
			@CatchUnknown
			public void onCommand(CommandSender sender, CommandHelp help) {
				help.showHelp();
			}

			@Subcommand("buy")
			@Description("Lists the commands that gets ran when someone buys from the shop")
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

	@Subcommand("unlink")
	@Description("Unlinks the chest you're looking at.")
	@CommandPermission("slabbo.unlink.self")
	@Conditions("lookingAtLinkedChest|canExecuteOnLinkedChest:othersPerm=slabbo.unlink.others")
	public void SlabboUnlinkCommand(Player player, LCContextResolver lcCtx) {
		Block lookingAtBlock = lcCtx.lookingAtBlock;

		Shop shop = Slabbo.getInstance().getChestLinkManager().getShopByChestLocation(lookingAtBlock.getLocation());

		Slabbo.getInstance().getChestLinkManager().removeShopLink(shop);
		shop.linkedChestLocation = null;

		Slabbo.getInstance().getShopManager().updateShop(shop);


		player.sendMessage(ChatColor.GREEN + LocaleManager.getString("success-message.chestlink.linking-removed"));
	}

	@Subcommand("shop")
	@Description("Slabbo shop commands")
	public class SlabboShopCommand extends BaseCommand {
		@HelpCommand
		@CatchUnknown
		public void onCommand(CommandSender sender, CommandHelp help) {
			help.showHelp();
		}

		@Subcommand("open")
		@Description("Open a slabbo shop by location")
		@CommandPermission("slabbo.shop.commandopen")
		public void openShopCommand(Player player, int x, int y, int z, @Optional String world) {
			String shopWorld = (world != null) ? world : "world";

			String locationString = String.format(
					"%s,%d,%d,%d",
					shopWorld,
					x,
					y,
					z
			);

			Shop shop = Slabbo.getInstance().getShopManager().getShop(locationString);

			if (shop == null) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
				return;
			}

			ShopUserGUI gui = new ShopUserGUI(shop, player);
			gui.openInventory(player);
		}

		@Subcommand("open virtual")
		@Description("Open a virtual slabbo shop")
		@CommandCompletion("@virtualShopNames")
		@CommandPermission("slabbo.shop.virtual.open")
		public void openVirtualShopCommand(Player player, String name) {
			String loweredName = name.toLowerCase();

			Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

			if (shop == null) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
				return;
			}

			ShopUserGUI gui = new ShopUserGUI(shop, player);
			gui.openInventory(player);
		}

		@Subcommand("create virtual")
		@Description("Creates a virtual shop")
		@CommandPermission("slabbo.shop.virtual.create")
		public void createVirtualShopCommand(Player player, String shopName) {
			if (Slabbo.getInstance().getShopManager().getShop(shopName.toLowerCase()) != null) {
				player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.general.named-shop-already-exists"));
				return;
			}

			ShopCreationGUI gui = new ShopCreationGUI(shopName, true);
			gui.openInventory(player);
		}

		@Subcommand("edit virtual")
		@Description("Edits a virtual slabbo shop")
		@CommandCompletion("@virtualShopNames")
		@CommandPermission("slabbo.shop.virtual.edit")
		public void editVirtualShopCommand(Player player, String name) {
			String loweredName = name.toLowerCase();

			Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

			if (shop == null) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
				return;
			}

			if (!shop.ownerId.equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.general.not-shop-owner"));
				return;
			}

			ShopAdminGUI gui = new ShopAdminGUI(shop, player);
			gui.openInventory(player);
		}

		@Subcommand("delete virtual")
		@Description("Deletes a virtual slabbo shop")
		@CommandCompletion("@virtualShopNames")
		@CommandPermission("slabbo.shop.virtual.delete")
		public void deleteVirtualShopCommand(Player player, String name) {
			String loweredName = name.toLowerCase();

			Shop shop = Slabbo.getInstance().getShopManager().getShop(loweredName);

			if (shop == null) {
				player.sendMessage(ChatColor.RED+LocaleManager.getString("error-message.general.shop-does-not-exist"));
				return;
			}

			if (!shop.ownerId.equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + LocaleManager.getString("error-message.general.not-shop-owner"));
				return;
			}

			ShopDeletionGUI gui = new ShopDeletionGUI(shop);
			gui.openInventory(player);
		}
	}

	@Subcommand("migrate")
	@Description("Migrates shops between sqlite and file storage")
	@CommandPermission("slabbo.migrate")
	@CommandCompletion("sqlite|file")
	public void onMigrate(CommandSender sender, String target) {
		if (!(sender instanceof org.bukkit.command.ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + LocaleManager.replaceSingleKey("migrate.only-console", "target", target));
			return;
		}
		if (!target.equalsIgnoreCase("sqlite") && !target.equalsIgnoreCase("file")) {
			sender.sendMessage(ChatColor.RED + LocaleManager.replaceSingleKey("migrate.invalid-target", "target", target));
			return;
		}
		String configEngine = Slabbo.getInstance().getConfig().getString("storageEngine", "file").toLowerCase();
		if (!configEngine.equals(target.toLowerCase())) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("current", configEngine);
			map.put("target", target.toLowerCase());
			sender.sendMessage(ChatColor.RED + LocaleManager.replaceKey("migrate.config-mismatch", map));
			return;
		}
		String currentType = Slabbo.getInstance().getConfig().getString("storage.type", "file").toLowerCase();
		if (currentType.equals(target.toLowerCase())) {
			sender.sendMessage(ChatColor.YELLOW + LocaleManager.replaceSingleKey("migrate.already-using", "target", target));
			return;
		}
		ShopManager shopManager = Slabbo.getInstance().getShopManager();
		if (shopManager.isMigrationInProgress()) {
			sender.sendMessage(ChatColor.YELLOW + LocaleManager.getString("migrate.in-progress"));
			return;
		}
		boolean success = shopManager.migrateStorage(target.toLowerCase());
		if (success) {
			Slabbo.getInstance().getConfig().set("storage.type", target.toLowerCase());
			Slabbo.getInstance().saveConfig();
			sender.sendMessage(ChatColor.GREEN + LocaleManager.replaceSingleKey("migrate.success", "target", target));
		} else {
			sender.sendMessage(ChatColor.RED + LocaleManager.getString("migrate.failed"));
		}
	}
}
