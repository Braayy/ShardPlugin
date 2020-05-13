package braayy.shard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.stream.Collectors;

public class Util {

    private static Class<?> ITEM_STACK_CLASS, NBT_TAG_COMPOUND_CLASS, NBT_TAG_LIST_CLASS, CRAFT_ITEM_STACK_CLASS;
    private static Method GET_TAG_METHOD, SET_METHOD, SET_TAG_METHOD, AS_NMS_COPY_METHOD, AS_CRAFT_MIRROR;

    static {
        try {
            Util.ITEM_STACK_CLASS = Util.getNMSClass("ItemStack");
            Util.NBT_TAG_COMPOUND_CLASS = Util.getNMSClass("NBTTagCompound");
            Util.NBT_TAG_LIST_CLASS = Util.getNMSClass("NBTTagList");
            Util.CRAFT_ITEM_STACK_CLASS = Util.getOBCClass("inventory.CraftItemStack");

            Util.GET_TAG_METHOD = Util.ITEM_STACK_CLASS.getMethod("getTag");
            Util.SET_METHOD = Util.NBT_TAG_COMPOUND_CLASS.getMethod("set", String.class, getNMSClass("NBTBase"));
            Util.SET_TAG_METHOD = Util.ITEM_STACK_CLASS.getMethod("setTag", Util.NBT_TAG_COMPOUND_CLASS);
            Util.AS_NMS_COPY_METHOD = Util.CRAFT_ITEM_STACK_CLASS.getMethod("asNMSCopy", ItemStack.class);
            Util.AS_CRAFT_MIRROR = Util.CRAFT_ITEM_STACK_CLASS.getMethod("asCraftMirror", ITEM_STACK_CLASS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
                itemStack = Util.setGlow(itemStack);
            }
        }

        return itemStack;
    }

    private static ItemStack setGlow(ItemStack itemStack) {
        try {
            Object nmsItemStack = Util.AS_NMS_COPY_METHOD.invoke(null, itemStack);
            Object nmsItemStackNbtTag = Util.GET_TAG_METHOD.invoke(nmsItemStack);
            Object nbtTag = nmsItemStackNbtTag != null ? nmsItemStackNbtTag : Util.NBT_TAG_COMPOUND_CLASS.newInstance();

            Util.SET_METHOD.invoke(nbtTag, "ench", NBT_TAG_LIST_CLASS.newInstance());

            Util.SET_TAG_METHOD.invoke(nmsItemStack, nbtTag);

            return (ItemStack) Util.AS_CRAFT_MIRROR.invoke(null, nmsItemStack);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static Class<?> getNMSClass(String className) throws Exception {
        String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
        return Class.forName("net.minecraft.server." + version + '.' + className);
    }

    private static Class<?> getOBCClass(String className) throws Exception {
        String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + '.' + className);
    }

}