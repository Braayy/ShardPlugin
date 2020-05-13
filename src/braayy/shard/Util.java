package braayy.shard;

import braayy.shard.enchantment.GlowEnchantment;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;
import java.util.stream.Collectors;

public class Util {

    public static String uuidToSQL(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static ItemStack deserializeItem(ConfigurationSection section) {
        Material type = Material.matchMaterial(section.getString("type"));
        int damage = section.getInt("damage", 0);
        int amount = section.getInt("amount", 1);

        ItemStack itemStack = new ItemStack(type, amount, (short) damage);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("display-name")));

        if (section.contains("lore")) {
            meta.setLore(section.getStringList("lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList())
            );
        }

        itemStack.setItemMeta(meta);

        if (section.contains("glow")) {
            if (section.getBoolean("glow")) {
                itemStack.addUnsafeEnchantment(GlowEnchantment.INSTANCE, 1);
            }
        }

        return itemStack;
    }

}