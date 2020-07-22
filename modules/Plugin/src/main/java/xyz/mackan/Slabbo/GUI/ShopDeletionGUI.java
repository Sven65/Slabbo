package xyz.mackan.Slabbo.GUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.mackan.Slabbo.GUI.items.GUIItems;
import xyz.mackan.Slabbo.Slabbo;
import xyz.mackan.Slabbo.abstractions.ISlabboSound;
import xyz.mackan.Slabbo.types.Shop;
import xyz.mackan.Slabbo.types.SlabboSound;
import xyz.mackan.Slabbo.utils.DataUtil;
import xyz.mackan.Slabbo.utils.ItemUtil;
import xyz.mackan.Slabbo.utils.ShopUtil;

import java.util.UUID;

public class ShopDeletionGUI  implements Listener {
	private Shop shop;
	private Inventory inv;

	public ShopDeletionGUI (Shop shop) {
		this.shop = shop;

		Bukkit.getPluginManager().registerEvents(this, Slabbo.getInstance());

		inv = Bukkit.createInventory(null, 9, "[Slabbo] "+Slabbo.localeManager.getString("general.general.destroy-shop"));

		initializeItems();
	}

	public void handleDestroy (HumanEntity humanEntity) {
		UUID userId = humanEntity.getUniqueId();
		ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();


		ItemUtil.removeShopItemsAtLocation(shop.location);

		Slabbo.shopUtil.removeShop(shop);

		((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("DESTROY"), 1, 1);

		DataUtil.saveShops();

		humanEntity.closeInventory();
	}

	public void handleCancel (HumanEntity humanEntity) {
		ISlabboSound slabboSound = Bukkit.getServicesManager().getRegistration(ISlabboSound.class).getProvider();

		humanEntity.closeInventory();

		((Player) humanEntity).playSound(shop.location, slabboSound.getSoundByKey("CANCEL"), 1, 1);

	}

	public void initializeItems () {
		inv.setItem(3, GUIItems.getDestroyConfirmItem());
		inv.setItem(4, GUIItems.getUserInfoItem(shop));
		inv.setItem(5, GUIItems.getCancelItem());
	}

	public void openInventory (final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick (final InventoryClickEvent e) {
		if (e.getInventory() != inv) return;
		e.setCancelled(true);

		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

		Player p = (Player) e.getWhoClicked();

		int slot = e.getRawSlot();

		if (slot > 8) return; // User clicked outside shop GUI

		switch (slot) {
			case 3:
				handleDestroy(p);
				break;
			case 5:
				handleCancel(p);
				break;
		}
	}

	// Cancel dragging in our inventory
	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}
}
