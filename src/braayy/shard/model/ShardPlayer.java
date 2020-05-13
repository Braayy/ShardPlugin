package braayy.shard.model;

import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class ShardPlayer {

    private final UUID uuid;
    private int shards;

    public ShardPlayer(Player player) {
        this(player.getUniqueId(), 0);
    }

    public ShardPlayer(UUID uuid, int shards) {
        this.uuid = uuid;
        this.shards = shards;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getShards() {
        return shards;
    }

    public void addShards(int amount) {
        this.shards += amount;
    }

    public void removeShards(int amount) {
        this.shards -= amount;
        if (this.shards < 0) {
            this.shards = 0;
        }
    }

    public boolean hasShards(int amount) {
        return this.shards - amount >= 0;
    }

    public void setShards(int amount) {
        this.shards = amount;
    }

    public boolean equalsToPlayer(Player player) {
        return player != null && this.uuid.equals(player.getUniqueId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShardPlayer that = (ShardPlayer) o;
        return shards == that.shards &&
                Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, shards);
    }

}