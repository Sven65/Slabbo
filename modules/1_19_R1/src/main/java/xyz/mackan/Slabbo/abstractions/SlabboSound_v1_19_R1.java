package xyz.mackan.Slabbo.abstractions;

import org.bukkit.Sound;

public class SlabboSound_v1_19_R1 implements ISlabboSound {
	enum SlabboSound {
		SUCCESS(Sound.ENTITY_PLAYER_LEVELUP),
		MODIFY_SUCCESS(Sound.BLOCK_ANVIL_USE),
		DESTROY(Sound.BLOCK_ANVIL_BREAK),
		NAVIGATION(Sound.BLOCK_COMPARATOR_CLICK),
		BLOCKED(Sound.BLOCK_COMPARATOR_CLICK),
		BUY_SELL_SUCCESS(Sound.ENTITY_ITEM_PICKUP),
		QUESTION(Sound.ENTITY_VILLAGER_AMBIENT),
		CANCEL(Sound.BLOCK_COMPARATOR_CLICK),
		DING(Sound.ENTITY_ARROW_HIT);

		public Sound sound;

		SlabboSound (Sound sound) {
			this.sound = sound;
		}
	}

	public Sound getSoundByKey (String key) {
		return SlabboSound.valueOf(key).sound;
	}
}
