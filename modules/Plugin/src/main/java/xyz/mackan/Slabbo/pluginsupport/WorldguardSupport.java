package xyz.mackan.Slabbo.pluginsupport;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.types.Shop;

import java.util.*;

public class WorldguardSupport {
	public static StateFlag CREATE_SHOPS;
	public static StateFlag USE_SHOPS;

	public static IntegerFlag MAX_SHOPS;

	public static void registerFlags () {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

		try {
			StateFlag createShopsFlag = new StateFlag("slabbo-others-create-shops", true);
			StateFlag useShopsFlag = new StateFlag("slabbo-others-use-shops", true);
			IntegerFlag maxShopsFlag = new IntegerFlag("slabbo-max-shops");

			registry.register(createShopsFlag);
			registry.register(useShopsFlag);
			registry.register(maxShopsFlag);

			CREATE_SHOPS = createShopsFlag;
			USE_SHOPS = useShopsFlag;
			MAX_SHOPS = maxShopsFlag;
		} catch (FlagConflictException e) {
			Bukkit.getLogger().severe("One or more flags conflict!");
		}
	}

	public static boolean canBypass (Player player) {
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
	}

	/**
	 * Gets the regions that are over a location
	 * @param location The location to get regions from
	 * @return The regions found
	 */
	public static Set<ProtectedRegion> getRegions(final Location location) {
		final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

		final RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));

		final ApplicableRegionSet regions = manager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));

		return regions.getRegions();
	}

	/**
	 * Gets shops in a given region
	 * @param region The region to get shops in
	 * @return The shops in the region, or null if the provided region is null.
	 */
	public static ArrayList<Shop> getShopsInRegion(ProtectedRegion region) {
		if (region == null) return null;

		ArrayList<Shop> shopsInRegion = new ArrayList<>();

		for(Shop shop : ShopManager.shops.values()) {
			if (region.contains(shop.location.getBlockX(), shop.location.getBlockY(), shop.location.getBlockZ())) {
				shopsInRegion.add(shop);
			}
		}

		return shopsInRegion;
	}



	/**
	 * Gets the highest priority region for a location
	 * @param location The location to get region for
	 * @return The region found
	 */
	public static ProtectedRegion getHighestPriorityRegionForLocation(final Location location) {
		Set<ProtectedRegion> regions = getRegions(location);

		ProtectedRegion highestPriorityRegion = null;
		int highestPriority = 0;

		for (ProtectedRegion region : regions) {
			int priority = region.getPriority();
			if (highestPriorityRegion == null || priority > highestPriority) {
				highestPriorityRegion = region;
				highestPriority = priority;
			}
		}

		return highestPriorityRegion;
	}

	public static int getMaxShopsInRegion (ProtectedRegion region) {
		Integer maxShops = region.getFlag(MAX_SHOPS);

		if (maxShops == null) {
			maxShops = Integer.MAX_VALUE;
		}

		return maxShops;
	}

	public static ArrayList<Shop> getPlayerShopsInRegion(ProtectedRegion region, Player player) {
		ArrayList<Shop> shopsInRegion = getShopsInRegion(region);
		ArrayList<Shop> userOwnedShopsInRegion = new ArrayList<>();

		for (Shop shop : shopsInRegion) {
			if (!shop.ownerId.equals(player.getUniqueId())) continue;

			userOwnedShopsInRegion.add(shop);
		}

		return userOwnedShopsInRegion;
	}

	public static PluginSupport.CanCreateShopResult canCreateShop (Location location, Player player) {
		if (canBypass(player)) return new PluginSupport.CanCreateShopResult(true);

		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		Set<ProtectedRegion> regions = getRegions(location);

		boolean isOwner = false;
		boolean canOthersCreate = true;

		String error = null;

		for (ProtectedRegion region : regions) {
			if (region.isOwner(localPlayer)) {
				isOwner = true;
			}

			StateFlag.State createShopsValue = region.getFlag(CREATE_SHOPS);

			Integer maxShopsInRegion = getMaxShopsInRegion(region);
			int shopsOwnedByUserInRegion = getPlayerShopsInRegion(region, player).size();

			if (shopsOwnedByUserInRegion >= maxShopsInRegion) {
				canOthersCreate = false;
				error = LocaleManager.getString("error-message.plugin.worldguard.region-creation-limit-reached");
			}

			if (createShopsValue == null) continue;

			if (createShopsValue == StateFlag.State.DENY) {
				canOthersCreate = false;
				error = LocaleManager.getString("error-message.plugin.worldguard.region-creation-prevented");
			}
		}

		return new PluginSupport.CanCreateShopResult(isOwner || canOthersCreate, error);
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
