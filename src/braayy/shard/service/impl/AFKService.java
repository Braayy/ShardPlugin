package braayy.shard.service.impl;

import braayy.shard.ShardPlugin;
import braayy.shard.service.Service;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AFKService implements Service, Listener {


    private final Map<UUID, Long> lastMoveMap;

    public AFKService() {
        this.lastMoveMap = new HashMap<>();
    }

    @Override
    public void enable() {}

    @Override
    public void disable() {
        this.lastMoveMap.clear();
    }

    public boolean isAFK(Player player) {
        Long lastLocation = this.lastMoveMap.get(player.getUniqueId());
        if (lastLocation == null) return true;

        return (System.currentTimeMillis() / 1000) - lastLocation >= TimeUnit.MINUTES.toSeconds(1);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        this.lastMoveMap.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() / 1000);
    }

}
