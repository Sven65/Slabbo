package xyz.mackan.Slabbo.types;

public class ShopAction {
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
