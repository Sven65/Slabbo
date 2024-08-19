package xyz.mackan.Slabbo.types;

public enum MinecraftVersion {
    v1_8(BukkitVersion.v1_8_R1),
    v1_8_2(BukkitVersion.v1_8_R1),
    v1_8_3(BukkitVersion.v1_8_R2),
    v1_8_4(BukkitVersion.v1_8_R3),
    v1_8_5(BukkitVersion.v1_8_R3),
    v1_8_6(BukkitVersion.v1_8_R3),
    v1_8_7(BukkitVersion.v1_8_R3),
    v1_8_8(BukkitVersion.v1_8_R3),
    v1_8_9(BukkitVersion.v1_8_R3),

    v1_9(BukkitVersion.v1_9_R1),
    v1_9_1(BukkitVersion.v1_9_R1),
    v1_9_2(BukkitVersion.v1_9_R1),
    v1_9_3(BukkitVersion.v1_9_R1),
    v1_9_4(BukkitVersion.v1_9_R2),

    v1_10(BukkitVersion.v1_10_R1),
    v1_10_1(BukkitVersion.v1_10_R1),
    v1_10_2(BukkitVersion.v1_10_R1),

    v1_11(BukkitVersion.v1_11_R1),
    v1_11_1(BukkitVersion.v1_11_R1),
    v1_11_2(BukkitVersion.v1_11_R1),

    v1_12(BukkitVersion.v1_12_R1),
    v1_12_1(BukkitVersion.v1_12_R1),
    v1_12_2(BukkitVersion.v1_12_R1),

    v1_13(BukkitVersion.v1_13_R1),
    v1_13_1(BukkitVersion.v1_13_R2),
    v1_13_2(BukkitVersion.v1_13_R2),

    v1_14(BukkitVersion.v1_14_R1),
    v1_14_1(BukkitVersion.v1_14_R1),
    v1_14_2(BukkitVersion.v1_14_R1),
    v1_14_3(BukkitVersion.v1_14_R1),
    v1_14_4(BukkitVersion.v1_14_R1),

    v1_15(BukkitVersion.v1_15_R1),
    v1_15_1(BukkitVersion.v1_15_R1),
    v1_15_2(BukkitVersion.v1_15_R1),

    v1_16(BukkitVersion.v1_16_R1),
    v1_16_1(BukkitVersion.v1_16_R1),
    v1_16_2(BukkitVersion.v1_16_R2),
    v1_16_3(BukkitVersion.v1_16_R2),
    v1_16_4(BukkitVersion.v1_16_R3),
    v1_16_5(BukkitVersion.v1_16_R3),

    v1_17(BukkitVersion.v1_17_R1),
    v1_17_1(BukkitVersion.v1_17_R1),


    v1_18(BukkitVersion.v1_18_R1),
    v1_18_1(BukkitVersion.v1_18_R1),
    v1_18_2(BukkitVersion.v1_18_R2),


    v1_19(BukkitVersion.v1_19_R1),
    v1_19_1(BukkitVersion.v1_19_R1),
    v1_19_2(BukkitVersion.v1_19_R1),
    v1_19_3(BukkitVersion.v1_19_R2),
    v1_19_4(BukkitVersion.v1_19_R3),
    v1_19_5(BukkitVersion.v1_19_R3),

    v1_20(BukkitVersion.v1_20_R1),
    v1_20_1(BukkitVersion.v1_20_R1),
    v1_20_2(BukkitVersion.v1_20_R2),
    v1_20_3(BukkitVersion.v1_20_R3),
    v1_20_4(BukkitVersion.v1_20_R3),
    v1_20_5(BukkitVersion.v1_20_R3),

    v1_20_6(BukkitVersion.v1_20_R4),
    v1_21(BukkitVersion.v1_20_R4),
    v1_21_1(BukkitVersion.v1_21_R1);

    public final BukkitVersion bukkitVersion;

    private MinecraftVersion(BukkitVersion bukkitVersion) {
        this.bukkitVersion = bukkitVersion;
    }

    public static MinecraftVersion from(String version) {
        String formattedVersion = String.format("v%s", version.toLowerCase().replace("-r0.1-snapshot", "").replace('.', '_'));

        return valueOf(formattedVersion);
    }
}
