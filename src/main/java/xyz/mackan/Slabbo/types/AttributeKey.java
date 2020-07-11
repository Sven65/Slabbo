package xyz.mackan.Slabbo.types;

import org.bukkit.NamespacedKey;
import xyz.mackan.Slabbo.Slabbo;

public enum AttributeKey {
	NO_PICKUP ("noPickup"),
	NO_DESPAWN ("noDespawn");

	private final NamespacedKey key;

	AttributeKey (String attribute) {
		this.key = new NamespacedKey(Slabbo.getInstance(), attribute);
	}

	public NamespacedKey getKey () {
		return this.key;
	}
}