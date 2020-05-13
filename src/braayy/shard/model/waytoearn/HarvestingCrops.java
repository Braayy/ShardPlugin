package braayy.shard.model.waytoearn;

import braayy.shard.ShardPlugin;
import braayy.shard.model.WayToEarn;
import org.bukkit.Material;

import java.util.Set;
import java.util.stream.Collectors;

public class HarvestingCrops extends WayToEarn {

    private final Set<Material> types;

    public HarvestingCrops(ShardPlugin plugin) {
        super(plugin, "harvesting_crops");

        this.types = plugin.getConfig().getStringList("ways-to-earn.harvesting_crops.types").stream()
                .map(Material::matchMaterial)
                .collect(Collectors.toSet());
    }

    public boolean isAllowed(Material material) {
        return this.types.contains(material);
    }
}
