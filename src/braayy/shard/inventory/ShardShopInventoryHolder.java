package braayy.shard.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShardShopInventoryHolder implements InventoryHolder {

    public static final ShardShopInventoryHolder INSTANCE = new ShardShopInventoryHolder();

    private ShardShopInventoryHolder() {}

    @Override
    public Inventory getInventory() {
        return null;
    }
}