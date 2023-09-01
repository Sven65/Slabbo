package xyz.mackan.Slabbo.types;


import jline.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.manager.ShopManager;

import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

@SerializableAs("Shop")
public class Shop implements Cloneable, ConfigurationSerializable {
	@SerializableAs("Shop.CommandList")
	public static class CommandList implements Cloneable, ConfigurationSerializable {
		private static final long serialVersionUID = 123L;

		public List<String> buyCommands;
		public List<String> sellCommands;

		public CommandList (List<String> buyCommands, List<String> sellCommands) {
			this.buyCommands = buyCommands;
			this.sellCommands = sellCommands;
		}

		public CommandList () {
			buyCommands = new ArrayList<String>();
			sellCommands = new ArrayList<String>();
		}

		@Override
		public Map<String, Object> serialize () {
			LinkedHashMap result = new LinkedHashMap();

			result.put("buyCommands", buyCommands);
			result.put("sellCommands", sellCommands);

			return result;
		}

		public static CommandList deserialize (Map<String, Object> args) {
			List<String> buyCommands = (List<String>) args.get("buyCommands");
			List<String> sellCommands = (List<String>) args.get("sellCommands");

			return new CommandList(buyCommands, sellCommands);
		}

		public void executeBuyCommands (HashMap<String, Object> replacementMap) {
			if (buyCommands == null) return;

			buyCommands.forEach(command -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LocaleManager.replaceString(command, replacementMap));
			});
		}

		public void executeSellCommands (HashMap<String, Object> replacementMap) {
			if (sellCommands == null) return;

			sellCommands.forEach(command -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), LocaleManager.replaceString(command, replacementMap));
			});
		}
	}


	private static final long serialVersionUID = -1358999872552913871L;

	public double buyPrice;
	public double sellPrice;

	public int quantity;

	public @Nullable Location location;

	public ItemStack item;

	public int stock = 0;

	public UUID ownerId;

	public boolean admin;

	public String note = "";

	//public UUID droppedItemId;

	public String linkedChestLocation = "";

	public String displayedOwnerName = null;

	public ShopLimit shopLimit = null;

	public CommandList commandList = null;

	/**
	 * Describes if the shop is virtual, i.e. has no physical location
	 */
	public boolean virtual = false;

	/**
	 * The name of the shop. Only set if the shop is virtual.
	 */
	public String shopName = "";

	// <editor-fold desc="Shop constructors with double prices">

	public Shop (double buyPrice, double sellPrice, int quantity, Location location, ItemStack item, int stock, UUID ownerId, boolean admin, String linkedChestLocation, boolean virtual, String shopName) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
		this.stock = stock;
		this.ownerId = ownerId;
		this.admin = admin;
		this.linkedChestLocation = linkedChestLocation;
		this.virtual = virtual;
		this.shopName = shopName;
	}

	public Shop (double buyPrice, double sellPrice, int quantity, Location location, ItemStack item, int stock, UUID ownerId, boolean admin, String linkedChestLocation) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
		this.stock = stock;
		this.ownerId = ownerId;
		this.admin = admin;
		this.linkedChestLocation = linkedChestLocation;
	}

	public Shop (double buyPrice, double sellPrice, int quantity, Location location, ItemStack item, int stock) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
		this.stock = stock;
	}

	public Shop (double buyPrice, double sellPrice, int quantity, Location location, ItemStack item) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
	}

	// </editor-fold>


	// <editor-fold desc="Shop constructors with int prices">

	public Shop (int buyPrice, int sellPrice, int quantity, Location location, ItemStack item, int stock, UUID ownerId, boolean admin, String linkedChestLocation) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
		this.stock = stock;
		this.ownerId = ownerId;
		this.admin = admin;
		this.linkedChestLocation = linkedChestLocation;
	}

	public Shop (int buyPrice, int sellPrice, int quantity, Location location, ItemStack item, int stock) {

		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
		this.stock = stock;
	}

	public Shop (int buyPrice, int sellPrice, int quantity, Location location, ItemStack item) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.quantity = quantity;
		this.location = location;
		this.item = item;
	}

	// </editor-fold>

	@Override
	public Map<String, Object> serialize () {
		LinkedHashMap result = new LinkedHashMap();

		result.put("buyPrice", buyPrice);
		result.put("sellPrice", sellPrice);
		result.put("quantity", quantity);
		result.put("location", location);
		result.put("item", item);
		result.put("stock", stock);
		result.put("admin", admin);
		result.put("ownerId", ownerId.toString());
		result.put("shopLimit", shopLimit);

		result.put("linkedChestLocation", linkedChestLocation);

		result.put("note", note);

		result.put("commandList", commandList);
		result.put("displayedOwnerName", displayedOwnerName);

		result.put("virtual", virtual);
		result.put("shopName", shopName);

		return result;
	}

	public static Shop deserialize (Map<String, Object> args) {
		double buyPrice = (Double) args.get("buyPrice");
		double sellPrice = (Double) args.get("sellPrice");
		int quantity = (Integer) args.get("quantity");
		int stock = (Integer) args.get("stock");

		Location location = (Location) args.get("location");

		ItemStack item = (ItemStack) args.get("item");

		String loadedOwnerId = (String) args.get("ownerId");
		String linkedChestLocation = (String) args.get("linkedChestLocation");
		String note = (String) args.getOrDefault("note", "Let's trade!");
		String displayedOwnerName = (String) args.get("displayedOwnerName");

		UUID ownerId = UUID.fromString(loadedOwnerId);

		boolean admin = (boolean) args.get("admin");

		ShopLimit shopLimit = (ShopLimit) args.get("shopLimit");
		CommandList commandList = (CommandList) args.get("commandList");

		boolean virtual = (boolean) args.getOrDefault("virtual", false);
		String shopName = (String) args.getOrDefault("shopName", "");


		Shop newShop = new Shop(buyPrice, sellPrice, quantity, location, item, stock, ownerId, admin, linkedChestLocation);

		newShop.note = note;
		newShop.shopLimit = shopLimit;
		newShop.commandList = commandList;
		newShop.displayedOwnerName = displayedOwnerName;

		newShop.virtual = virtual;
		newShop.shopName = shopName;

		return newShop;
	}

	public void doLimitRestock () {
		this.shopLimit.restock();
	}

	public boolean shouldRestock () {
		if (admin && shopLimit != null) {
			if (shopLimit.enabled) {
				long currentTime = Instant.now().getEpochSecond();

				long nextRestock = shopLimit.lastRestock + (shopLimit.restockTime);

				return currentTime >= nextRestock;
			}
		}

		return false;
	}

	public String getLocationString () {
		return ShopManager.locationToString(this.location, this.shopName);
	}

	public String getInfoString () {
		SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

		String ownerName = displayedOwnerName != null ? displayedOwnerName : Bukkit.getOfflinePlayer(ownerId).getName();

		return String.format(
			"§d[%s]§r §7| §d%s: §6%s §7| §d%s: §6%s",
			getLocationString(),
			LocaleManager.getString("general.general.item"),
			api.getItemName(item),
			LocaleManager.getString("gui.owner-title"),
			ownerName
		);
	}
}
