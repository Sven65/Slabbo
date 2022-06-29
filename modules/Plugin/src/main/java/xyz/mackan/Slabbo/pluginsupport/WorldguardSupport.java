package xyz.mackan.Slabbo.pluginsupport;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class WorldguardSupport {
	public static StateFlag CREATE_SHOPS;
	public static StateFlag USE_SHOPS;

	public static void registerFlags () {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

		try {
			StateFlag createShopsFlag = new StateFlag("slabbo-others-create-shops", true);
			StateFlag useShopsFlag = new StateFlag("slabbo-others-use-shops", true);

			registry.register(createShopsFlag);
			registry.register(useShopsFlag);

			CREATE_SHOPS = createShopsFlag;
			USE_SHOPS = useShopsFlag;
		} catch (FlagConflictException e) {
			Bukkit.getLogger().severe("One or more flags conflict!");
		}
	}

	public static boolean canBypass (Player player) {
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
	}

	public static Set<ProtectedRegion> getRegions(final Location location) {
		final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

		final RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));

		final ApplicableRegionSet regions = manager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));

		return regions.getRegions();
	}

	public static boolean canCreateShop (Location location, Player player) {
		if (canBypass(player)) return true;

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		Set<ProtectedRegion> regions = getRegions(location);

		boolean isOwner = false;
		boolean canOthersCreate = true;

		for (ProtectedRegion region : regions) {
			if (region.isOwner(localPlayer)) {
				isOwner = true;
			}

			StateFlag.State createShopsValue = region.getFlag(CREATE_SHOPS);

			if (createShopsValue == null) continue;

			if (createShopsValue == StateFlag.State.DENY) {
				canOthersCreate = false;
			}
		}

		return isOwner || canOthersCreate;
	}

	public static boolean canUseShop (Location location, Player player) {
		if (canBypass(player)) return true;
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		Set<ProtectedRegion> regions = getRegions(location);

		boolean isOwner = false;
		boolean canOthersUse = true;

		for (ProtectedRegion region : regions) {
			if (region.isOwner(localPlayer)) {
				isOwner = true;
			}

			StateFlag.State useShopsValue = region.getFlag(USE_SHOPS);

			if (useShopsValue == null) continue;

			if (useShopsValue == StateFlag.State.DENY) {
				canOthersUse = false;
			}
		}

		return isOwner || canOthersUse;
	}
}
