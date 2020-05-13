package braayy.shard.model.waytoearn;

import braayy.shard.ShardPlugin;
import braayy.shard.model.WayToEarn;
import org.bukkit.Material;

import java.util.Set;
import java.util.stream.Collectors;

public class Mining extends WayToEarn {

    private final Set<Material> types;

    public Mining(ShardPlugin plugin) {
        super(plugin, "mining");

        this.types = plugin.getConfig().getStringList("ways-to-earn.mining.types").stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
    }

    public boolean isAllowed(Material material) {
        return this.types.contains(material);
    }

}
