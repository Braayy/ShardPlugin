package braayy.shard.service.impl;

import braayy.shard.ShardPlugin;
import braayy.shard.Util;
import braayy.shard.inventory.ShardShopInventoryHolder;
import braayy.shard.model.ShardShopItem;
import braayy.shard.service.Service;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShardShopInventoryService implements Service, Listener {

    private final ShardPlugin plugin;

    private final String title;
    private final int size;
    private final ItemStack filler;
    private final List<ShardShopItem> items;

    public ShardShopInventoryService(ShardPlugin plugin) {
        this.plugin = plugin;

        this.title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("shard-shop.title"));
        this.size = plugin.getConfig().getInt("shard-shop.size");
        this.filler = Util.deserializeItem(plugin.getConfig().getConfigurationSection("shard-shop.filler"));

        this.items = new ArrayList<>();
    }

    @Override
    public void enable() {
        ConfigurationSection items = this.plugin.getConfig().getConfigurationSection("shard-shop.items");
        for (String key : items.getKeys(false)) {
            ShardShopItem item = new ShardShopItem(items.getConfigurationSection(key));
            this.items.add(item);
        }
    }

    @Override
    public void disable() {}

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(ShardShopInventoryHolder.INSTANCE, this.size, this.title);

        for (ShardShopItem item : this.items) {
            inventory.setItem(item.getSlot(), item.getIcon());
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, this.filler.clone());
            }
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getClickedInventory() == null) return;

        if (event.getClickedInventory().getHolder() instanceof ShardShopInventoryHolder) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            ShardService shardService = this.plugin.getService(ShardService.class);
            MessageService messageService = this.plugin.getService(MessageService.class);

            for (ShardShopItem item : this.items) {
                if (event.getCurrentItem().isSimilar(item.getIcon())) {
                    if (!shardService.hasShards(player, item.getPrice())) {
                        messageService.sendMessage(player, "shop.not-enough");
                        player.closeInventory();

                        return;
                    }

                    shardService.removeShards(player, item.getPrice());
                    messageService.sendMessage(player, "shop.bought");

                    for (String command : item.getCommands()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                    }
                    player.closeInventory();
                    return;
                }
            }
        }
    }
}
