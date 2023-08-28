package xyz.mackan.Slabbo.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.alex9849.arm.adapters.WGRegion;
import net.alex9849.arm.events.RemoveRegionEvent;
import net.alex9849.arm.events.RestoreRegionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.pluginsupport.ARMSupport;
import xyz.mackan.Slabbo.pluginsupport.PluginSupport;
import xyz.mackan.Slabbo.pluginsupport.WorldguardSupport;
import xyz.mackan.Slabbo.types.Shop;

import java.util.ArrayList;

public class ARMListener implements Listener {
    @EventHandler
    public void removeRegionEventListener(RemoveRegionEvent event) {
        Slabbo.getInstance().getLogger().info("[Slabbo] ARM Region removed");
    }

    @EventHandler
    public void restoreRegionEventListener(RestoreRegionEvent event) {
        Slabbo.getInstance().getLogger().info("[Slabbo] ARM Region restored");

        WGRegion wgRegion = event.getRegion().getRegion();

        ArrayList<Shop> shops = ARMSupport.getShopsInRegion(wgRegion);

        Slabbo.getInstance().getLogger().info(String.format("[Slabbo] Region contained %d shops", shops.size()));

        //WorldguardSupport.getShopsInRegion(event.getRegion().getRegion())
    }
}
