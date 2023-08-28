package xyz.mackan.Slabbo.pluginsupport;


import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.alex9849.arm.adapters.WGRegion;
import net.alex9849.arm.regions.Region;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.types.Shop;

import java.util.ArrayList;

public class ARMSupport {
    /**
     * Gets shops in a given region
     * @param region The region to get shops in
     * @return The shops in the region, or null if the provided region is null.
     */
    public static ArrayList<Shop> getShopsInRegion(WGRegion region) {
        if (region == null) return null;

        ArrayList<Shop> shopsInRegion = new ArrayList<>();

        for(Shop shop : ShopManager.shops.values()) {
            if (region.contains(shop.location.getBlockX(), shop.location.getBlockY(), shop.location.getBlockZ())) {
                shopsInRegion.add(shop);
            }
        }

        return shopsInRegion;
    }
}
