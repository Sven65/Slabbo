package xyz.mackan.Slabbo;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.mackan.Slabbo.listeners.PlayerInteractListener;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Slabbo extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");

	private static String dataPath = null;

	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;

	@Override
	public void onEnable () {
		dataPath = this.getDataFolder().getPath();
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		setupPermissions();
		setupCommands();
		setupListeners();
		setupChat();

		new File(getDataPath()).mkdirs();

		getLogger().info("Slabbo enabled.");

	}

	@Override
	public void onDisable () { log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion())); }

	private void setupListeners () {
		getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
	}

	private void setupCommands () {

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

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
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

	public static Chat getChat () { return chat; }
}
