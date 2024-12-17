package xyz.mackan.Slabbo.types;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import xyz.mackan.Slabbo.Slabbo;

import javax.annotation.Nullable;

public enum Metadata {
    IS_SLABBO_DISPLAY_ITEM ("IS_SLABBO_DISPLAY_ITEM", true);

    private final String key;
    private final MetadataValue value;

    Metadata(String key, @Nullable Object value) {
        this.key = key;
        this.value = new FixedMetadataValue(Slabbo.getInstance(), value);
    }

    public String getKey () { return this.key; }
    public MetadataValue getValue () { return this.value; }
}
