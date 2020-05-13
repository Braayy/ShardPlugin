package braayy.shard.model;

import braayy.shard.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShardShopItem {

    private final int slot;
    private final int price;
    private final ItemStack icon;
    private final List<String> commands;

    public ShardShopItem(ConfigurationSection section) {
        this.slot = section.getInt("slot");
        this.price = section.getInt("price");
        this.icon = Util.deserializeItem(section);
        this.commands = section.getStringList("commands");
    }

    public int getSlot() {
        return slot;
    }

    public int getPrice() {
        return price;
    }

    public ItemStack getIcon() {
        return icon.clone();
    }

    public List<String> getCommands() {
        return commands;
    }
}