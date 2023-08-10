package com.ashkiano.enchantedsteak;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantedSteak extends JavaPlugin implements Listener {

    private final String LORE_TEXT = "Zlatý steak";

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        setupCraftingRecipe();
    }

    private void setupCraftingRecipe() {
        ItemStack enchantedSteak = new ItemStack(Material.COOKED_BEEF);
        ItemMeta meta = enchantedSteak.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();
        lore.add(LORE_TEXT);

        if (meta != null) {
            meta.setLore(lore);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            enchantedSteak.setItemMeta(meta);

            NamespacedKey key = new NamespacedKey(this, "enchanted_steak");
            ShapedRecipe recipe = new ShapedRecipe(key, enchantedSteak);

            recipe.shape("GGG", "GSG", "GGG");
            recipe.setIngredient('G', Material.GOLD_INGOT);
            recipe.setIngredient('S', Material.COOKED_BEEF);

            Bukkit.addRecipe(recipe);
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.COOKED_BEEF && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLore() && meta.getLore().contains(LORE_TEXT)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3600, 0)); // 1200 ticks = 1 minute
            }
        }
    }
}
