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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;

public class EnchantedSteak extends JavaPlugin implements Listener {

    private final String LORE_TEXT = "Enchanted steak";

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        setupCraftingRecipe();

        Metrics metrics = new Metrics(this, 19479);

        this.getLogger().info("Thank you for using the EnchantedSteak plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");

        checkForUpdates();
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

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://plugins.ashkiano.com/version_check.php?plugin=" + pluginName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has("error")) {
                    this.getLogger().warning("Error when checking for updates: " + jsonObject.getString("error"));
                } else {
                    String latestVersion = jsonObject.getString("latest_version");

                    String currentVersion = this.getDescription().getVersion();
                    if (currentVersion.equals(latestVersion)) {
                        this.getLogger().info("This plugin is up to date!");
                    } else {
                        this.getLogger().warning("There is a newer version (" + latestVersion + ") available! Please update!");
                    }
                }
            } else {
                this.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to check for updates. Error: " + e.getMessage());
        }
    }
}
