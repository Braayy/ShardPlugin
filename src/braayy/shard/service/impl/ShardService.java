package braayy.shard.service.impl;

import braayy.shard.ShardPlugin;
import braayy.shard.dao.Dao;
import braayy.shard.model.ShardPlayer;
import braayy.shard.service.Service;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShardService implements Service, Listener {

    private final ShardPlugin plugin;
    private final Set<ShardPlayer> shardPlayerSet;

    public ShardService(ShardPlugin plugin) {
        this.plugin = plugin;

        this.shardPlayerSet = new HashSet<>();
    }

    @Override
    public void enable() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void disable() {
        this.shardPlayerSet.clear();
    }

    private ShardPlayer getByPlayer(Player player) {
        return this.shardPlayerSet.stream().filter(shardPlayer -> shardPlayer.equalsToPlayer(player)).findFirst().orElse(null);
    }

    public void setShards(Player player, int amount) {
        ShardPlayer shardPlayer = this.getByPlayer(player);
        shardPlayer.setShards(amount);

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Dao<UUID, ShardPlayer> shardPlayerDao = this.plugin.getShardPlayerDao();

            shardPlayerDao.update(shardPlayer);
        });
    }

    public void addShards(Player player, int amount) {
        ShardPlayer shardPlayer = this.getByPlayer(player);
        shardPlayer.addShards(amount);

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Dao<UUID, ShardPlayer> shardPlayerDao = this.plugin.getShardPlayerDao();

            shardPlayerDao.update(shardPlayer);
        });
    }

    public void removeShards(Player player, int amount) {
        ShardPlayer shardPlayer = this.getByPlayer(player);
        shardPlayer.removeShards(amount);

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Dao<UUID, ShardPlayer> shardPlayerDao = this.plugin.getShardPlayerDao();

            if (shardPlayer.getShards() > 0) {
                shardPlayerDao.update(shardPlayer);
            } else {
                shardPlayerDao.delete(shardPlayer);
            }
        });
    }

    public int getShards(Player player) {
        return this.getByPlayer(player).getShards();
    }

    public boolean hasShards(Player player, int amount) {
        return this.getByPlayer(player).hasShards(amount);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            UUID playerUUID = event.getPlayer().getUniqueId();

            Dao<UUID, ShardPlayer> shardPlayerDao = this.plugin.getShardPlayerDao();
            ShardPlayer shardPlayer = shardPlayerDao.select(playerUUID);

            if (shardPlayer == null) {
                shardPlayer = new ShardPlayer(playerUUID, 0);

                shardPlayerDao.insert(shardPlayer);
            }

            this.shardPlayerSet.add(shardPlayer);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.shardPlayerSet.removeIf(shardPlayer -> shardPlayer.equalsToPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.shardPlayerSet.removeIf(shardPlayer -> shardPlayer.equalsToPlayer(event.getPlayer()));
    }

}