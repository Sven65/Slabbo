package xyz.mackan.Slabbo.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.SlabboAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryUtil {
    static SlabboAPI slabboAPI = Bukkit.getServicesManager().getRegistration(SlabboAPI.class).getProvider();

    public static int addItemsToPlayerInventory(PlayerInventory playerInventory, ItemStack item, int itemCount, boolean oversized) {
        List<ItemStack> stacksToAdd = new ArrayList<ItemStack>();

        if (oversized) {
            item.setAmount(itemCount);
            stacksToAdd.add(item);
        } else {
            int maxStackSize = slabboAPI.getMaxStack(item);

            int stacks = (int)Math.floor(itemCount / maxStackSize);
            int lastStack = itemCount % maxStackSize;

            for (int i = 0;i<stacks;i++) {
                int size = maxStackSize;

                ItemStack clonedStack = item.clone();
                clonedStack.setAmount(size);
                stacksToAdd.add(clonedStack);
            }

            if (lastStack > 0) {
                ItemStack clonedStack = item.clone();
                clonedStack.setAmount(lastStack);
                stacksToAdd.add(clonedStack);
            }
        }

        ItemStack[] stackArray = stacksToAdd.toArray(new ItemStack[stacksToAdd.size()]);
        HashMap<Integer, ItemStack> leftovers = playerInventory.addItem(stackArray);

        // TODO: Make this do a dry run to see if the player can acutally get all the items
        int leftoverCount = leftovers
                .values()
                .stream()
                .map(stack -> stack.getAmount())
                .reduce(0, (total, el) -> total + el);


        return leftoverCount;
    }
}
