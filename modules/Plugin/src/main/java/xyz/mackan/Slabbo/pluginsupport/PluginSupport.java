package xyz.mackan.Slabbo.pluginsupport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.Slabbo;

public class PluginSupport {

	public static class EnabledPlugins {
		public static boolean worldguard = false;
		public static boolean griefprevention = false;
		public static boolean holoDropsX = false;
	}

	public static boolean canCreateShop (Location location, Player player) {
		boolean canCreateShop = true;

		if (EnabledPlugins.worldguard) {
			canCreateShop = WorldguardSupport.canCreateShop(location, player);
		}

		if (EnabledPlugins.griefprevention) {
			canCreateShop = GriefPreventionSupport.canCreateShop(location, player);
		}

		if (EnabledPlugins.worldguard && EnabledPlugins.griefprevention) {
			canCreateShop = WorldguardSupport.canCreateShop(location, player) && GriefPreventionSupport.canCreateShop(location, player);
		}

		return canCreateShop;
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
