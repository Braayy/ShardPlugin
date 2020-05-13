package braayy.shard;

import braayy.shard.dao.Dao;
import braayy.shard.dao.impl.ShardPlayerDao;
import braayy.shard.model.ShardPlayer;
import braayy.shard.service.Service;
import braayy.shard.service.impl.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ShardPlugin extends JavaPlugin {

    private Set<Service> serviceSet;

    private Dao<UUID, ShardPlayer> shardPlayerDao;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.serviceSet = new HashSet<>();

        registerServices(
                AFKService.class,
                DatabaseService.class,
                ShardService.class,
                EarnShardService.class,
                ShardShopInventoryService.class
        );

        this.shardPlayerDao = new ShardPlayerDao(this);

        new ShardCommand(this).register();
    }

    @Override
    public void onDisable() {
        this.serviceSet.forEach(Service::disable);

        this.getServer().getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    public Dao<UUID, ShardPlayer> getShardPlayerDao() {
        return shardPlayerDao;
    }

    @SuppressWarnings("unchecked")
    public <T extends Service> T getService(Class<? extends Service> serviceClass) {
        Service serviceInstance = this.serviceSet.stream().filter(service -> service.getClass() == serviceClass).findFirst().orElse(null);
        if (serviceInstance == null) {
            throw new IllegalStateException(serviceClass.getSimpleName() + " was not enabled yet!");
        }

        return (T) serviceInstance;
    }

    @SafeVarargs
    private final void registerServices(Class<? extends Service>... serviceClasses) {
        try {
            for (Class<? extends Service> serviceClass : serviceClasses) {
                Service service;
                try {
                    service = serviceClass.getConstructor(ShardPlugin.class).newInstance(this);
                } catch (NoSuchMethodException ex) {
                    service = serviceClass.newInstance();
                }

                this.serviceSet.add(service);
            }

            for (Service service : this.serviceSet) {
                service.enable();
                if (service instanceof Listener) {
                    this.getServer().getPluginManager().registerEvents((Listener) service, this);
                }

                this.getLogger().info(service.getClass().getSimpleName() + " was enabled!");
            }
        } catch (Exception ex) {
            this.getLogger().log(Level.SEVERE, "Could not register services", ex);
        }
    }

}