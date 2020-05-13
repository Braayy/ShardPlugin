package braayy.shard.model.waytoearn;

import braayy.shard.ShardPlugin;
import braayy.shard.model.WayToEarn;

public class Playing extends WayToEarn {

    private int timer;

    public Playing(ShardPlugin plugin) {
        super(plugin, "playing");

        this.timer = plugin.getConfig().getInt("ways-to-earn.playing.timer");
    }

    public int getTimer() {
        return timer;
    }
}
