// TODO: Docs
package xyz.mackan.Slabbo;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.abstractions.SlabboItemAPI;
import xyz.mackan.Slabbo.commands.*;
import xyz.mackan.Slabbo.listeners.*;
import xyz.mackan.Slabbo.pluginsupport.PluginSupport;
import xyz.mackan.Slabbo.pluginsupport.WorldguardSupport;
import xyz.mackan.Slabbo.types.BukkitVersion;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.types.ShopLimit;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.utils.UpdateChecker;
import xyz.mackan.Slabbo.manager.LocaleManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Slabbo extends JavaPlugin {
	static {
		ConfigurationSerialization.registerClass(Shop.class, "Shop");
		ConfigurationSerialization.registerClass(ShopLimit.class, "ShopLimit");
		ConfigurationSerialization.registerClass(Shop.CommandList.class, "Shop.CommandList");
	}

	public static final Logger log = Logger.getLogger("Minecraft");

	private static String dataPath = null;

	private static Economy econ = null;

	private static Slabbo instance;

	public static boolean hasUpdate = false;
	private static boolean isEnabled = false;


	@Override
	public void onLoad () {
		setupPluginSupport();

		if (PluginSupport.isPluginEnabled("WorldGuard")) {
			WorldguardSupport.registerFlags();
		}
	}

	@Override
	public void onEnable () {
		dataPath = this.getDataFolder().getPath();
		if (!setupEconomy()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		loadAPI();

		new File(getDataPath()).mkdirs();


		this.saveDefaultConfig();

		saveResource("lang.yml", false);
		saveResource("acf_lang.yml", false);

		instance = this;

		LocaleManager.loadFile(this, "lang.yml");

		setupCommands();
		setupListeners();

		checkUpdates();

		getLogger().info("Slabbo fully enabled.");

		ShopManager.loadShops();

		isEnabled = true;
	}

	@Override
	public void onDisable () {

		if (isEnabled) {
			log.info("Saving shops before disabling Slabbo");
			DataUtil.saveShopsOnMainThread();
		}

		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	private void loadAPI () {
		SlabboAPI api = null;
		SlabboItemAPI itemApi = null;
		ISlabboSound slabboSound = null;

		String packageName = Slabbo.class.getPackage().getName();
		String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
		try {
			api = (SlabboAPI) Class.forName(packageName + ".abstractions.SlabboAPI_v" + internalsName).newInstance();
			itemApi = (SlabboItemAPI) Class.forName(packageName + ".abstractions.SlabboItemAPI_v" + internalsName).newInstance();
			slabboSound = (ISlabboSound) Class.forName(packageName + ".abstractions.SlabboSound_v" + internalsName).newInstance();

			Bukkit.getServicesManager().register(SlabboAPI.class, api, this, ServicePriority.Highest);
			Bukkit.getServicesManager().register(SlabboItemAPI.class, itemApi, this, ServicePriority.Highest);
			Bukkit.getServicesManager().register(ISlabboSound.class, slabboSound, this, ServicePriority.Highest);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException exception) {
			exception.printStackTrace();
			Bukkit.getLogger().log(Level.SEVERE, "Slabbo could not find a valid implementation for this server version.");
		}
	}


	private void setupPluginSupport () {
		PluginSupport.checkPlugin("WorldGuard");
		PluginSupport.checkPlugin("GriefPrevention");
		PluginSupport.checkPlugin("HoloDropsX");
		PluginSupport.checkPlugin("Magic");
		PluginSupport.checkPlugin("AdvancedRegionMarket");
		PluginSupport.checkPlugin("ClearLag");
	}

	private void checkUpdates () {
		boolean doCheck = getConfig().getBoolean("checkupdates", true);

		if (!doCheck) return;

		UpdateChecker.getVersion(version -> {
			if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
				hasUpdate = true;
			}
		});
	}

	private void setupListeners () {
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
		getServer().getPluginManager().registerEvents(new ItemDespawnListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new BlockEventListeners(), this);
		getServer().getPluginManager().registerEvents(new InventoryMoveListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryPickupItemListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerFishEventListener(), this);

		BukkitVersion version = BukkitVersion.getCurrentVersion();

		if (version.isSameOrLater(BukkitVersion.v1_8_R3)) {
			// 1.8.8
			getServer().getPluginManager().registerEvents(new ItemMergeListener(), this);
		}

		if (version.isSameOrLater(BukkitVersion.v1_12_R1)) {
			getServer().getPluginManager().registerEvents(new EntityPickupItemListener(), this);
		} else {
			getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(), this);
		}

		if (PluginSupport.isPluginEnabled("ClearLag")) {
			getServer().getPluginManager().registerEvents(new ClearlagItemRemoveListener(), this);
		}

		if (PluginSupport.isPluginEnabled("AdvancedRegionMarket")) {
			this.getLogger().info("[Slabbo] ARM Found, enabling listeners");

			getServer().getPluginManager().registerEvents(new ARMListener(), this);
		}
	}


	private void setupCommands () {
		PaperCommandManager manager = new PaperCommandManager(this);

		manager.enableUnstableAPI("help");

		try {
			manager.getLocales().loadYamlLanguageFile("acf_lang.yml", Locale.ENGLISH);
		} catch (IOException | InvalidConfigurationException e) {
			getLogger().severe("Slabbo couldn't load acf_lang.yml");
		}

		manager.getCommandCompletions().registerCompletion("importFiles", c -> {
			return SlabboCommandCompletions.getImportFiles();
		});

		manager.getCommandCompletions().registerCompletion("virtualShopNames", c -> {
			return SlabboCommandCompletions.getVirtualShopNames();
		});

		manager.getCommandCompletions().registerCompletion("virtualAdminShopNames", c -> {
			return SlabboCommandCompletions.getVirtualAdminShopNames();
		});

		Conditions.registerConditions(manager);

		manager.getCommandContexts().registerIssuerOnlyContext(SlabboContextResolver.class, SlabboContextResolver.getContextResolver());
		manager.getCommandContexts().registerIssuerOnlyContext(LCContextResolver.class, LCContextResolver.getContextResolver());

		manager.registerCommand(new SlabboCommand());
	}

	private boolean setupEconomy () {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			log.severe(String.format("[%s] - Disabled as Vault dependency wasn't found!", getDescription().getName()));

			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			log.severe(String.format("[%s] - Disabled as no economy provider was found!", getDescription().getName()));

			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public static String getDataPath () {
		return dataPath;
	}

	public static Economy getEconomy() {
		return econ;
	}

	public static Slabbo getInstance() { return instance; }
}
