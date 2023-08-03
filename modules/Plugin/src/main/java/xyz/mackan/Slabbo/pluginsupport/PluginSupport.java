package xyz.mackan.Slabbo.pluginsupport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.Slabbo;


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

	public static class EnabledPlugins {
		public static boolean worldguard = false;
		public static boolean griefprevention = false;
		public static boolean holoDropsX = false;
		public static boolean magic = false;
	}

	public static CanCreateShopResult canCreateShop (Location location, Player player) {
		CanCreateShopResult canCreateShopResult = new CanCreateShopResult(true);

		if (EnabledPlugins.worldguard) {
			canCreateShopResult = WorldguardSupport.canCreateShop(location, player);
		}

		if (EnabledPlugins.griefprevention) {
			canCreateShopResult = GriefPreventionSupport.canCreateShop(location, player);
		}

		if (EnabledPlugins.worldguard && EnabledPlugins.griefprevention) {
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

		if (EnabledPlugins.worldguard) {
			canUseShop = WorldguardSupport.canUseShop(location, player);
		}

		if (EnabledPlugins.griefprevention) {
			canUseShop = GriefPreventionSupport.canUseShop(location, player);
		}

		if (EnabledPlugins.worldguard && EnabledPlugins.griefprevention) {
			canUseShop = WorldguardSupport.canUseShop(location, player) || GriefPreventionSupport.canUseShop(location, player);
		}

		return canUseShop;
	}
}
