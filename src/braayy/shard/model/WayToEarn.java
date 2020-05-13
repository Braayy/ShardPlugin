package braayy.shard.model;

import braayy.shard.ShardPlugin;

public class WayToEarn {

    private final boolean enabled;
    private final float chance;
    private final int amount;

    public WayToEarn(ShardPlugin plugin, String configName) {
        this.enabled = plugin.getConfig().getBoolean("ways-to-earn." + configName + ".enabled");
        this.chance = plugin.getConfig().getInt("ways-to-earn." + configName + ".chance") / 100f;
        this.amount = plugin.getConfig().getInt("ways-to-earn." + configName + ".amount");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public float getChance() {
        return chance;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "enabled=" + enabled +
                ", chance=" + chance +
                ", amount=" + amount +
                '}';
    }
}