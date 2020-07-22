// TODO: Delete shop
// TODO: Admin shops
// TODO: Docs
package xyz.mackan.Slabbo;

import co.aikar.commands.PaperCommandManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.commands.SlabboCommand;
import xyz.mackan.Slabbo.commands.SlabboCommandCompletions;
import xyz.mackan.Slabbo.listeners.*;
import xyz.mackan.Slabbo.pluginsupport.EnabledPlugins;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.utils.ChestLinkUtil;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;
import xyz.mackan.Slabbo.utils.UpdateChecker;
import xyz.mackan.Slabbo.utils.locale.LocaleManager;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Slabbo extends JavaPlugin {
	static {
		ConfigurationSerialization.registerClass(Shop.class, "Shop");
	}

	private static final Logger log = Logger.getLogger("Minecraft");

	private static String dataPath = null;

	private static Economy econ = null;
	private static Permission perms = null;
//	private static Chat chat = null;

	private static Slabbo instance;

	public static ShopUtil shopUtil = new ShopUtil();
	public static ChestLinkUtil chestLinkUtil = new ChestLinkUtil();

	public static LocaleManager localeManager;

	public static boolean hasUpdate = false;

	public static EnabledPlugins enabledPlugins = new EnabledPlugins();

	@Override
	public void onEnable () {
		dataPath = this.getDataFolder().getPath();
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		loadAPI();
		setupPluginSupport();

		new File(getDataPath()).mkdirs();
//		new File(getDataPath()+"/backups/").mkdirs();


		this.saveDefaultConfig();

		saveResource("lang.yml", false);

		instance = this;

		localeManager = new LocaleManager();

		setupPermissions();
		setupCommands();
		setupListeners();

//		setupBackup();

		checkUpdates();

		getLogger().info("Slabbo enabled.");

		shopUtil.loadShops();
	}

	@Override
	public void onDisable () {
		log.info("Saving shops before disabling Slabbo");

		DataUtil.saveShopsOnMainThread();

		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	private void loadAPI () {
		SlabboAPI api = null;

		String packageName = Slabbo.class.getPackage().getName();
		String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

		try {
			api = (SlabboAPI) Class.forName(packageName + ".abstractions.SlabboAPI_v" + internalsName).newInstance();
			Bukkit.getServicesManager().register(SlabboAPI.class, api, this, ServicePriority.Highest);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException exception) {
			Bukkit.getLogger().log(Level.SEVERE, "Slabbo could not find a valid implementation for this server version.");
		}
	}


	private void setupPluginSupport () {
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			enabledPlugins.worldguard = true;
		}

		if (getServer().getPluginManager().getPlugin("GriefPrevention") != null) {
			enabledPlugins.griefprevention = true;
		}
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
		getServer().getPluginManager().registerEvents(new EntityPickupItemListener(), this);
		getServer().getPluginManager().registerEvents(new ItemDespawnListener(), this);
		getServer().getPluginManager().registerEvents(new ItemMergeListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new BlockEventListeners(), this);
		getServer().getPluginManager().registerEvents(new InventoryMoveListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

		if (getServer().getPluginManager().getPlugin("ClearLag") != null) {
			getServer().getPluginManager().registerEvents(new ClearlagItemRemoveListener(), this);
		}
	}


	private void setupCommands () {
		PaperCommandManager manager = new PaperCommandManager(this);

		manager.enableUnstableAPI("help");

		manager.getCommandCompletions().registerCompletion("importFiles", c -> {
			return SlabboCommandCompletions.getImportFiles();
		});

		manager.registerCommand(new SlabboCommand());
	}

	private boolean setupEconomy () {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public static String getDataPath () {
		return dataPath;
	}

	public static Economy getEconomy() {
		return econ;
	}

	public static Permission getPermissions() {
		return perms;
	}

	public static Slabbo getInstance() { return instance; }
}
