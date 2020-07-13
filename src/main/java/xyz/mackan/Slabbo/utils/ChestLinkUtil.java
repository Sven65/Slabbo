package xyz.mackan.Slabbo.utils;

import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.types.Shop;

import java.util.HashMap;
import java.util.UUID;

public class ChestLinkUtil {
	public HashMap<UUID, String> pendingLinks = new HashMap<UUID, String>();

	// Chest Location, Shop
	public HashMap<String, Shop> links = new HashMap<String, Shop>();

	public ChestLinkUtil () { }

	public boolean hasPendingLink (Player p) {
		UUID id = p.getUniqueId();

		return pendingLinks.containsKey(id);
	}
}
