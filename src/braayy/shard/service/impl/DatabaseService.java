package braayy.shard.service.impl;

import braayy.shard.ShardPlugin;
import braayy.shard.service.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Level;

public class DatabaseService implements Service {

    private final ShardPlugin plugin;

    private Connection connection;

    public DatabaseService(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        String databaseFilename = this.plugin.getConfig().getString("storage-filename", "storage.db");

        try {
            File storageFile = new File(this.plugin.getDataFolder(), databaseFilename);
            if (!storageFile.exists()) {
                if (!storageFile.createNewFile()) {
                    this.plugin.getLogger().severe("Could not create storage.db file");
                    return;
                }
            }

            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + storageFile.getAbsolutePath());

            try (PreparedStatement stmt = this.connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS shardplayer(uuid BINARY(16) not null, shards INT not null)"
            )) {
                stmt.executeUpdate();
            }
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not enable database service", ex);
        }
    }

    @Override
    public void disable() {
        try {
            this.connection.close();
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not disable database service", ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
