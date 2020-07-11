package xyz.mackan.Slabbo.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import sun.security.provider.PolicyParser;

import java.util.Set;

public class PermissionUtil {
	public static int getLimit (Player player) {
		Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();

		int limit = 0;

		for (PermissionAttachmentInfo perm : perms) {
			if (!perm.getPermission().startsWith("slabbo.limit.")) continue;

			String limitNode = perm.getPermission().substring(13);

			if (limitNode.equalsIgnoreCase("*")) {
				limit = Integer.MAX_VALUE;
				break;
			}

			int current = Integer.parseInt(limitNode);

			if (current > limit) {
				limit = current;
			}
		}

		return limit;
	}
}
