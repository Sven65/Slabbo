package xyz.mackan.Slabbo.importers;

import xyz.mackan.Slabbo.types.Shop;

import java.util.List;

public class ImportResult {
	public List<Shop> shops;
	public List<String> skippedShops;

	public ImportResult (List<Shop> shops, List<String> skippedShops) {
		this.shops = shops;
		this.skippedShops = skippedShops;
	}
}
