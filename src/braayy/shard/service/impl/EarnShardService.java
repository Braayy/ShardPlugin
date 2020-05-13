package braayy.shard.service.impl;

import braayy.shard.ShardPlugin;
import braayy.shard.model.WayToEarn;
import braayy.shard.model.waytoearn.HarvestingCrops;
import braayy.shard.model.waytoearn.KillingMobs;
import braayy.shard.model.waytoearn.Mining;
import braayy.shard.model.waytoearn.Playing;
import braayy.shard.service.Service;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.concurrent.ThreadLocalRandom;

public class EarnShardService implements Service, Listener {

    private final ShardPlugin plugin;

    private final Playing playing;
    private final Mining mining;
    private final KillingMobs killingMobs;
    private final HarvestingCrops harvestingCrops;
    private final WayToEarn fishing;

    public EarnShardService(ShardPlugin plugin) {
        this.plugin = plugin;

        this.playing = new Playing(plugin);
        this.mining = new Mining(plugin);
        this.killingMobs = new KillingMobs(plugin);
        this.harvestingCrops = new HarvestingCrops(plugin);
        this.fishing = new WayToEarn(plugin, "fishing");
    }

    @Override
    public void enable() {
        if (this.playing.isEnabled()) {
            int timer = 20 * 60 * this.playing.getTimer();

            this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
                AFKService afkService = this.plugin.getService(AFKService.class);
                ShardService shardService = this.plugin.getService(ShardService.class);

                for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                    if (!afkService.isAFK(player)) {
                        float chance = ThreadLocalRandom.current().nextFloat();
                        if (chance <= this.playing.getChance()) {
                            shardService.addShards(player, this.playing.getAmount());

                            player.sendMessage(ChatColor.GREEN + "You have earned " + this.playing.getAmount() + " shards for playing");
                        }
                    }
                }
            }, timer, timer);
        }
    }

    @Override
    public void disable() {}

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!this.mining.isEnabled() && !this.harvestingCrops.isEnabled()) return;

        if (this.mining.isAllowed(event.getBlock().getType())) {
            float chance = ThreadLocalRandom.current().nextFloat();
            if (chance <= this.mining.getChance()) {
                ShardService shardService = this.plugin.getService(ShardService.class);

                shardService.addShards(event.getPlayer(), this.mining.getAmount());
                event.getPlayer().sendMessage(ChatColor.GREEN + "You have earned " + this.mining.getAmount() + " shards for mining");
            }
        }

        if (this.harvestingCrops.isAllowed(event.getBlock().getType())) {
            float chance = ThreadLocalRandom.current().nextFloat();
            if (chance <= this.harvestingCrops.getChance()) {
                ShardService shardService = this.plugin.getService(ShardService.class);

                shardService.addShards(event.getPlayer(), this.harvestingCrops.getAmount());
                event.getPlayer().sendMessage(ChatColor.GREEN + "You have earned " + this.mining.getAmount() + " shards for harvesting crops");
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null || event.getEntity().getKiller().getType() != EntityType.PLAYER) return;

        if (!this.killingMobs.isEnabled()) return;
        if (!this.killingMobs.isAllowed(event.getEntityType())) return;

        float chance = ThreadLocalRandom.current().nextFloat();
        if (chance <= this.mining.getChance()) {
            Player killer = event.getEntity().getKiller();

            ShardService shardService = this.plugin.getService(ShardService.class);

            shardService.addShards(killer, this.mining.getAmount());
            killer.sendMessage(ChatColor.GREEN + "You have earned " + this.mining.getAmount() + " shards for killing a mob");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (!this.fishing.isEnabled()) return;

        float chance = ThreadLocalRandom.current().nextFloat();
        if (chance <= this.fishing.getChance()) {
            ShardService shardService = this.plugin.getService(ShardService.class);

            shardService.addShards(event.getPlayer(), this.mining.getAmount());
            event.getPlayer().sendMessage(ChatColor.GREEN + "You have earned " + this.mining.getAmount() + " shards for fishing");
        }
    }
}