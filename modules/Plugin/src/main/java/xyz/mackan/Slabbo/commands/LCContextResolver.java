package xyz.mackan.Slabbo.commands;

// Linked Chest Context Resolver

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.contexts.IssuerOnlyContextResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.manager.ChestLinkManager;
import xyz.mackan.Slabbo.manager.LocaleManager;
import xyz.mackan.Slabbo.types.Shop;

import java.util.Set;

public class LCContextResolver {
    static ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();

    public Block lookingAtBlock;

    LCContextResolver(Block lookingAtBlock) {
        this.lookingAtBlock = lookingAtBlock;
    }

    public static Block getLookingAt (Player player) {
        Block lookingAt = player.getTargetBlock((Set<Material>) null, 6);


        if (!ChestLinkManager.isChestLinked(lookingAt)) {
            return null;
        }

        return lookingAt;
    }

    public static IssuerOnlyContextResolver<LCContextResolver, BukkitCommandExecutionContext> getContextResolver () {
        return (c) -> {
            Player player = c.getPlayer();

            Block lookingAt = getLookingAt(player);

            if (lookingAt == null) {
                player.playSound(player.getLocation(), slabboSound.getSoundByKey("BLOCKED"), 1, 1);

                throw new ConditionFailedException(LocaleManager.getString("error-message.general.not-a-linked-chest"));
            }

            return new LCContextResolver(lookingAt);
        };
    }
}
