package xyz.mackan.Slabbo.types;

public class ShopAction {
	public enum ShopActionType {
		NONE,
		CREATION_LIMIT_HIT,
		CREATE,
		OPEN_DELETION_GUI,
		OPEN_CLIENT_GUI,
		OPEN_ADMIN_GUI,
		LINK_CHEST,
		STOCK_SHOP,
		BULK_RESTOCK_SHOP,
	}

	public ShopActionType type;

	public Object extra;

	public ShopAction (ShopActionType type) {
		this.type = type;
	}

	public ShopAction (ShopActionType type, Object extra) {
		this.type = type;
		this.extra = extra;
	}
}
