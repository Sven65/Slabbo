package xyz.mackan.Slabbo.types;

import org.bukkit.Bukkit;

public enum BukkitVersion {
	v1_8_R1,
	v1_8_R2,
	v1_8_R3,
	v1_9_R1,
	v1_9_R2,
	v1_10_R1,
	v1_11_R1,
	v1_12_R1,
	v1_13_R1,
	v1_13_R2,
	v1_14_R1,
	v1_15_R1,
	v1_16_R1,
	v1_16_R2,
	v1_16_R3,
	v1_17_R1,
	v1_18_R1,
	v1_18_R2,
	v1_19_R1,
	v1_19_R2,
	v1_19_R3,
	v1_20_R1,
	v1_20_R2,
	v1_20_R3,
	v1_20_R4,
	v1_21_R1,
	v1_21_R2,
	v1_21_R3;

	public int getVersionIndex () {
		int i = 0;

		for (BukkitVersion version : values()) {
			if (equals(version)) return i;
			i++;
		}

		return -1;
	}

	public static BukkitVersion getCurrentVersion () {
		String bukkitVersion = Bukkit.getServer().getBukkitVersion();

		MinecraftVersion nmsVersion = MinecraftVersion.from(bukkitVersion);

		String internalsName = nmsVersion.bukkitVersion.getVersion();

		return BukkitVersion.valueOf("v"+internalsName);
	}

	public String getVersion() {
		return this.toString().replace("v", "");
	}


	public boolean isAfter (BukkitVersion version) {
		int currentIndex = getVersionIndex();
		int pIndex = version.getVersionIndex();

		return currentIndex > pIndex;
	}

	public boolean isSameOrLater (BukkitVersion version) {
		int currentIndex = getVersionIndex();
		int pIndex = version.getVersionIndex();

		return currentIndex >= pIndex;
	}
}
