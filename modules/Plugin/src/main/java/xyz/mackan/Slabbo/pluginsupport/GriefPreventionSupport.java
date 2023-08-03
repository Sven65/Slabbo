package xyz.mackan.Slabbo.pluginsupport;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GriefPreventionSupport {
	public static PluginSupport.CanCreateShopResult canCreateShop (Location location, Player player) {
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);

		if (claim == null) {
			return new PluginSupport.CanCreateShopResult(true);
		}

		String error = claim.allowBuild(player, Material.AIR);

		if (error == null || error.equalsIgnoreCase("")) {
			return new PluginSupport.CanCreateShopResult(true);
		}

		return new PluginSupport.CanCreateShopResult(false);
	}

	public static boolean canUseShop (Location location, Player player) {
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, false, null);

		if (claim == null) {
			return true;
		}

		String error = claim.allowAccess(player);

		if (error == null || error.equalsIgnoreCase("")) {
			return true;
		}

		return false;
	}
}
