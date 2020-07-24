package xyz.mackan.Slabbo.abstractions;

import org.bukkit.Sound;

public class SlabboSound_v1_8_R3 implements ISlabboSound {
	enum SlabboSound {
		SUCCESS(Sound.LEVEL_UP),
		MODIFY_SUCCESS(Sound.ANVIL_USE),
		DESTROY(Sound.ANVIL_BREAK),
		NAVIGATION(Sound.CLICK),
		BLOCKED(Sound.CLICK),
		BUY_SELL_SUCCESS(Sound.ITEM_PICKUP),
		QUESTION(Sound.VILLAGER_IDLE),
		CANCEL(Sound.CLICK),
		DING(Sound.ARROW_HIT);

		public Sound sound;

		SlabboSound (Sound sound) {
			this.sound = sound;
		}
	}

	public Sound getSoundByKey (String key) {
		return SlabboSound.valueOf(key).sound;
	}
}
