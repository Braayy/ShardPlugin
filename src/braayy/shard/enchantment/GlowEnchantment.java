package braayy.shard.enchantment;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class GlowEnchantment extends Enchantment {

    public static final GlowEnchantment INSTANCE = new GlowEnchantment(100);

    private GlowEnchantment(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Glow";
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return true;
    }

    public static void register() {
        try {
            Field acceptingNewField = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNewField.setAccessible(true);

            acceptingNewField.set(null, true);

            Enchantment.registerEnchantment(GlowEnchantment.INSTANCE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}