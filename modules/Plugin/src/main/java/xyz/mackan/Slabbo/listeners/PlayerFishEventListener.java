package xyz.mackan.Slabbo.listeners;

import net.minecraft.world.entity.item.EntityItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;
import xyz.mackan.Slabbo.abstractions.SlabboItemAPI;

public class PlayerFishEventListener implements Listener {
    private static SlabboAPI api = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();


    @EventHandler
    public void onFish (PlayerFishEvent e) {
        Entity caught = e.getCaught();

        if (caught == null) return;

        if (!api.isItem(caught)) return;

        Item item = (Item) caught;


        if (!api.isSlabboItem(item)) return;

        e.setCancelled(true);
    }
}
