package xyz.mackan.Slabbo.listeners;

import net.alex9849.arm.adapters.WGRegion;
import net.alex9849.arm.events.RestoreRegionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.mackan.Slabbo.manager.ShopManager;
import xyz.mackan.Slabbo.pluginsupport.ARMSupport;
import xyz.mackan.Slabbo.types.Shop;

import java.util.ArrayList;

public class ARMListener implements Listener {

    @EventHandler
    public void restoreRegionEventListener(RestoreRegionEvent event) {

        WGRegion wgRegion = event.getRegion().getRegion();

        ArrayList<Shop> shops = ARMSupport.getShopsInRegion(wgRegion);


        for (Shop shop : shops) {
            ShopManager.removeShop(shop);
        }
    }
}
