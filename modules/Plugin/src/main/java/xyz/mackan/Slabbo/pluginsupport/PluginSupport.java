package xyz.mackan.Slabbo.pluginsupport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.Slabbo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static org.bukkit.Bukkit.getServer;


public class PluginSupport {

	public static class CanCreateShopResult {
		public boolean canCreateShop;
		public String errorMessage;

		public CanCreateShopResult(boolean canCreateShop) {
			this.canCreateShop = canCreateShop;
		}

		public CanCreateShopResult(boolean canCreateShop, String errorMessage) {
			this.canCreateShop = canCreateShop;
			this.errorMessage = errorMessage;
		}
	}

	public static Map<String, Boolean> pluginStatus = new HashMap<String, Boolean>();

	public static boolean isPluginEnabled (String pluginName) {
		return pluginStatus.getOrDefault(pluginName, false);
	}

	public static void checkPlugin(String pluginName) {
		Slabbo.log.info(String.format("Checking for plugin %s", pluginName));

		if (getServer().getPluginManager().getPlugin(pluginName) == null) return;

		Slabbo.log.info(String.format("Plugin %s found. Enabling support.", pluginName));

		pluginStatus.put(pluginName, true);
	}

	public static CanCreateShopResult canCreateShop (Location location, Player player) {
		CanCreateShopResult canCreateShopResult = new CanCreateShopResult(true);

		if (isPluginEnabled("WorldGuard")) {
			canCreateShopResult = WorldguardSupport.canCreateShop(location, player);
		}

		if (isPluginEnabled("GriefPrevention")) {
			canCreateShopResult = GriefPreventionSupport.canCreateShop(location, player);
		}

		if (isPluginEnabled("WorldGuard") && isPluginEnabled("GriefPrevention")) {
			CanCreateShopResult canCreateWg = WorldguardSupport.canCreateShop(location, player);
			CanCreateShopResult canCreateGp = GriefPreventionSupport.canCreateShop(location, player);

			String error = null;

			if (!canCreateWg.canCreateShop) error = canCreateWg.errorMessage;
			if (!canCreateGp.canCreateShop) error = canCreateGp.errorMessage;

			canCreateShopResult = new CanCreateShopResult(canCreateWg.canCreateShop && canCreateWg.canCreateShop, error);
		}

		return canCreateShopResult;
	}

	public static boolean canUseShop (Location location, Player player) {
		boolean canUseShop = true;

		if (isPluginEnabled("WorldGuard")) {
			canUseShop = WorldguardSupport.canUseShop(location, player);
		}

		if (isPluginEnabled("GriefPrevention")) {
			canUseShop = GriefPreventionSupport.canUseShop(location, player);
		}

		if (isPluginEnabled("WorldGuard") && isPluginEnabled("GriefPrevention")) {
			canUseShop = WorldguardSupport.canUseShop(location, player) || GriefPreventionSupport.canUseShop(location, player);
		}

		return canUseShop;
	}
}
