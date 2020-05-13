package braayy.shard.service.impl;

import braayy.shard.ShardPlugin;
import braayy.shard.service.Service;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;

public class MessageService implements Service {

    private final ShardPlugin plugin;

    private final Map<String, String> messageMap;

    public MessageService(ShardPlugin plugin) {
        this.plugin = plugin;

        this.messageMap = new HashMap<>();
    }

    @Override
    public void enable() {
        try {
            File messagesFile = new File(this.plugin.getDataFolder(), "messages.properties");
            if (!messagesFile.exists()) {
                this.plugin.saveResource("messages.properties", false);
            }

            try (FileReader fileReader = new FileReader(messagesFile)) {
                Properties properties = new Properties();
                properties.load(fileReader);

                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    String colorizedMessage = ChatColor.translateAlternateColorCodes('&', (String) entry.getValue());

                    this.messageMap.put((String) entry.getKey(), colorizedMessage);
                }
            }
        } catch (Exception ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not load messages.properties", ex);
        }
    }

    @Override
    public void disable() {
        this.messageMap.clear();
    }

    public String get(String key, Object... args) {
        String message = this.messageMap.get(key);
        Objects.requireNonNull(message, key + " message was not found");

        if (args.length % 2 > 0) {
            throw new IllegalArgumentException("args must be even");
        }

        for (int i = 0; i < args.length; i += 2) {
            String argKey = (String) args[i];
            String argValue = args[i + 1].toString();

            message = message.replace("{" + argKey + "}", argValue);
        }

        return message;
    }

    public void sendMessage(CommandSender player, String key, Object... args) {
        String message = this.get(key, args);

        player.sendMessage(message);
    }

    public void reload() {
        this.disable();
        this.enable();
    }
}