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
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

public class EarnShardService implements Service, Listener {

    private final ShardPlugin plugin;

    private Playing playing;
    private Mining mining;
    private KillingMobs killingMobs;
    private HarvestingCrops harvestingCrops;
    private WayToEarn fishing;
    private BukkitTask playingTask;

    public EarnShardService(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        this.playing = new Playing(plugin);
        this.mining = new Mining(plugin);
        this.killingMobs = new KillingMobs(plugin);
        this.harvestingCrops = new HarvestingCrops(plugin);
        this.fishing = new WayToEarn(plugin, "fishing");

        if (this.playingTask != null) {
            this.playingTask.cancel();
        }

        if (this.playing.isEnabled()) {
            int timer = 20 * 60 * this.playing.getTimer();

            this.playingTask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
                AFKService afkService = this.plugin.getService(AFKService.class);
                ShardService shardService = this.plugin.getService(ShardService.class);
                MessageService messageService = this.plugin.getService(MessageService.class);

                for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                    if (!afkService.isAFK(player)) {
                        float chance = ThreadLocalRandom.current().nextFloat();
                        if (chance <= this.playing.getChance()) {
                            shardService.addShards(player, this.playing.getAmount());

                            messageService.sendMessage(player, "earn.playing", "amount", this.playing.getAmount());
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

        ShardService shardService = this.plugin.getService(ShardService.class);
        MessageService messageService = this.plugin.getService(MessageService.class);

        if (this.mining.isAllowed(event.getBlock().getType())) {
            float chance = ThreadLocalRandom.current().nextFloat();
            if (chance <= this.mining.getChance()) {
                shardService.addShards(event.getPlayer(), this.mining.getAmount());

                messageService.sendMessage(event.getPlayer(), "earn.mining", "amount", this.mining.getAmount());
            }
        }

        if (this.harvestingCrops.isAllowed(event.getBlock().getType())) {
            float chance = ThreadLocalRandom.current().nextFloat();
            if (chance <= this.harvestingCrops.getChance()) {
                shardService.addShards(event.getPlayer(), this.harvestingCrops.getAmount());

                messageService.sendMessage(event.getPlayer(), "earn.harvesting", "amount", this.harvestingCrops.getAmount());
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null || event.getEntity().getKiller().getType() != EntityType.PLAYER) return;

        if (!this.killingMobs.isEnabled()) return;
        if (!this.killingMobs.isAllowed(event.getEntityType())) return;

        ShardService shardService = this.plugin.getService(ShardService.class);
        MessageService messageService = this.plugin.getService(MessageService.class);

        float chance = ThreadLocalRandom.current().nextFloat();
        if (chance <= this.killingMobs.getChance()) {
            Player killer = event.getEntity().getKiller();

            shardService.addShards(killer, this.killingMobs.getAmount());

            messageService.sendMessage(killer, "earn.killing", "amount", this.killingMobs.getAmount());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (!this.fishing.isEnabled()) return;

        ShardService shardService = this.plugin.getService(ShardService.class);
        MessageService messageService = this.plugin.getService(MessageService.class);

        float chance = ThreadLocalRandom.current().nextFloat();
        if (chance <= this.fishing.getChance()) {
            shardService.addShards(event.getPlayer(), this.fishing.getAmount());

            messageService.sendMessage(event.getPlayer(), "earn.fishing", "amount", this.fishing.getAmount());
        }
    }
}