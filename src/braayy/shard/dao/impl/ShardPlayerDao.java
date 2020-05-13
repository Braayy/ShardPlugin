package braayy.shard.dao.impl;

import braayy.shard.ShardPlugin;
import braayy.shard.Util;
import braayy.shard.dao.Dao;
import braayy.shard.model.ShardPlayer;
import braayy.shard.service.impl.DatabaseService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.logging.Level;

public class ShardPlayerDao implements Dao<UUID, ShardPlayer> {

    private final ShardPlugin plugin;

    public ShardPlayerDao(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void insert(ShardPlayer shardPlayer) {
        DatabaseService databaseService = this.plugin.getService(DatabaseService.class);

        try (PreparedStatement stmt = databaseService.getConnection().prepareStatement(
                String.format("INSERT INTO shardplayer VALUES(X'%s', ?)", Util.uuidToSQL(shardPlayer.getUUID()))
        )) {
            stmt.setInt(1, shardPlayer.getShards());

            stmt.executeUpdate();
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not insert a shard player", ex);
        }
    }

    @Override
    public void update(ShardPlayer shardPlayer) {
        DatabaseService databaseService = this.plugin.getService(DatabaseService.class);

        try (PreparedStatement stmt = databaseService.getConnection().prepareStatement(
                String.format("UPDATE shardplayer SET shards = ? WHERE uuid = X'%s'", Util.uuidToSQL(shardPlayer.getUUID()))
        )) {
            stmt.setInt(1, shardPlayer.getShards());

            stmt.executeUpdate();
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not update a shard player", ex);
        }
    }

    @Override
    public void delete(ShardPlayer shardPlayer) {
        DatabaseService databaseService = this.plugin.getService(DatabaseService.class);

        try (PreparedStatement stmt = databaseService.getConnection().prepareStatement(
                String.format("DELETE FROM shardplayer WHERE uuid = X'%s'", Util.uuidToSQL(shardPlayer.getUUID()))
        )) {

            stmt.executeUpdate();
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not delete a shard player", ex);
        }
    }

    @Override
    public ShardPlayer select(UUID uuid) {
        DatabaseService databaseService = this.plugin.getService(DatabaseService.class);

        try (PreparedStatement stmt = databaseService.getConnection().prepareStatement(
                String.format("SELECT shards FROM shardplayer WHERE uuid = X'%s'", Util.uuidToSQL(uuid))
        )) {

            try (ResultSet set = stmt.executeQuery()) {
                if (set.next()) {
                    int shards = set.getInt(1);

                    return new ShardPlayer(uuid, shards);
                }
            }
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not select a shard player", ex);
        }

        return null;
    }
}