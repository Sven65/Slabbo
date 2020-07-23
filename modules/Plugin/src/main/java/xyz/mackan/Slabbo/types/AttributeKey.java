package xyz.mackan.Slabbo.types;

public enum AttributeKey {
	NO_PICKUP ("noPickup"),
	NO_DESPAWN ("noDespawn"),
	SHOP_LOCATION ("shopLocation"),
	NO_MERGE ("noMerge");

	private final String key;

	AttributeKey (String attribute) {
		this.key = attribute;
	}

	public String getKey () {
		return this.key;
	}
}