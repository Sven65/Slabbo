package xyz.mackan.Slabbo.pluginsupport;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldguardSupport {
	public static boolean canBypass (Player player) {
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
	}

	public static boolean canCreateShop (Location location, Player player) {
		if (canBypass(player)) return true;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

		RegionQuery query = container.createQuery();

		return query.testState(weLocation, localPlayer, Flags.BUILD);
	}

	public static boolean canUseShop (Location location, Player player) {
		if (canBypass(player)) return true;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		com.sk89q.worldedit.util.Location weLocation = BukkitAdapter.adapt(location);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

		RegionQuery query = container.createQuery();

		return query.testState(weLocation, localPlayer, Flags.INTERACT);
	}
}
