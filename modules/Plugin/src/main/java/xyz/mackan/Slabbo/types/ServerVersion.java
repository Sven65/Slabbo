package xyz.mackan.Slabbo.types;

import org.bukkit.Bukkit;

public class ServerVersion {
    private static final int[] current = parse(Bukkit.getBukkitVersion());

    private static int[] parse (String version) {
        String[] parts = version.split("-")[0].split("\\.");

        int[] parsed = new int[3];

        for (int i = 0; i < Math.min(3, parts.length); i++) {
            parsed[i] = Integer.parseInt(parts[i]);
        }

        return parsed;
    }

    public static boolean isSameOrLater (int major, int minor, int patch) {
        int[] target = { major, minor, patch };

        for (int i = 0; i < 3; i++) {
            if (current[i] != target[i]) return current[i] > target[i];
        }

        return true;
    }
}