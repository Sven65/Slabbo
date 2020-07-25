package xyz.mackan.Slabbo.pluginsupport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.Slabbo;

public class PluginSupport {
	public static boolean canCreateShop (Location location, Player player) {
		boolean canCreateShop = true;

		if (Slabbo.enabledPlugins.worldguard) {
			canCreateShop = WorldguardSupport.canCreateShop(location, player);
		}

		if (Slabbo.enabledPlugins.griefprevention) {
			canCreateShop = GriefPreventionSupport.canCreateShop(location, player);
		}

		if (Slabbo.enabledPlugins.worldguard && Slabbo.enabledPlugins.griefprevention) {
			canCreateShop = WorldguardSupport.canCreateShop(location, player) && GriefPreventionSupport.canCreateShop(location, player);
		}

		return canCreateShop;
	}

	public static boolean canUseShop (Location location, Player player) {
		boolean canUseShop = true;

		if (Slabbo.enabledPlugins.worldguard) {
			canUseShop = WorldguardSupport.canUseShop(location, player);
		}

		if (Slabbo.enabledPlugins.griefprevention) {
			canUseShop = GriefPreventionSupport.canUseShop(location, player);
		}

		if (Slabbo.enabledPlugins.worldguard && Slabbo.enabledPlugins.griefprevention) {
			canUseShop = WorldguardSupport.canUseShop(location, player) || GriefPreventionSupport.canUseShop(location, player);
		}

		return canUseShop;
	}
}
