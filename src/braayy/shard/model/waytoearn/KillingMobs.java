package braayy.shard.model.waytoearn;

import braayy.shard.ShardPlugin;
import braayy.shard.model.WayToEarn;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.stream.Collectors;

public class KillingMobs extends WayToEarn {

    private final Set<EntityType> types;

    public KillingMobs(ShardPlugin plugin) {
        super(plugin, "killing_mobs");

        this.types = plugin.getConfig().getStringList("ways-to-earn.killing_mobs.types").stream()
                .map(string -> EntityType.valueOf(string.toUpperCase().replace(' ', '_')))
                .collect(Collectors.toSet());
    }

    public boolean isAllowed(EntityType entityType) {
        return this.types.contains(entityType);
    }

    @Override
    public String toString() {
        return "KillingMobs{" +
                super.toString() +
                "types=" + types +
                '}';
    }
}
