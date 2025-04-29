package com.yoshey.inventory;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class InventoryManager {

    private final File dataFolder;

    public InventoryManager(File pluginFolder) {
        this.dataFolder = new File(pluginFolder, "inventories");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    // Speichert Inventar, Rüstung, XP, Health und Hunger
    public void saveInventory(Player player, String worldName) {
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        PlayerInventory inv = player.getInventory();

        // Inventar & Armor speichern
        config.set(worldName + ".inventory", toBase64(inv.getContents()));
        config.set(worldName + ".armor", toBase64(inv.getArmorContents()));

        // XP speichern
        config.set(worldName + ".xp.level", player.getLevel());
        config.set(worldName + ".xp.total", player.getTotalExperience());
        config.set(worldName + ".xp.progress", player.getExp());

        // Health und Hunger speichern
        config.set(worldName + ".health", player.getHealth());
        config.set(worldName + ".food", player.getFoodLevel());
        config.set(worldName + ".saturation", player.getSaturation());

        try {
            config.save(file);
            Bukkit.getLogger().info("[InventoryManager] Inventar gespeichert für " + player.getName() + " in Welt " + worldName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Komfort-Variante: speichert für aktuelle Welt
    public void saveInventory(Player player) {
        String worldName = player.getWorld().getName();
        saveInventory(player, worldName);
        Bukkit.getLogger().info("[InventoryManager] Inventar gespeichert für " + player.getName() + " in " + worldName);
    }

    // Lädt Inventar, Rüstung, XP, Health und Hunger
    public void loadInventory(Player player) {
        String world = player.getWorld().getName();
        File file = new File(dataFolder, player.getUniqueId() + ".yml");

        if (!file.exists()) {
            Bukkit.getLogger().warning("[InventoryManager] Keine Inventardatei gefunden für " + player.getName());
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (!config.contains(world + ".inventory")) {
            Bukkit.getLogger().warning("[InventoryManager] Kein gespeichertes Inventar für " + player.getName() + " in Welt " + world);
            return;
        }

        try {
            // Inventar und Rüstung laden
            ItemStack[] contents = fromBase64(config.getString(world + ".inventory"));
            ItemStack[] armor = fromBase64(config.getString(world + ".armor"));

            player.getInventory().setContents(contents);
            player.getInventory().setArmorContents(armor);

            // XP laden
            if (config.contains(world + ".xp.level")) {
                player.setLevel(config.getInt(world + ".xp.level"));
                player.setTotalExperience(config.getInt(world + ".xp.total"));
                player.setExp((float) config.getDouble(world + ".xp.progress"));
            }

            // Health laden
            if (config.contains(world + ".health")) {
                player.setHealth(config.getDouble(world + ".health"));
            }

            // Hunger laden
            if (config.contains(world + ".food")) {
                player.setFoodLevel(config.getInt(world + ".food"));
            }

            // Sättigung laden
            if (config.contains(world + ".saturation")) {
                player.setSaturation((float) config.getDouble(world + ".saturation"));
            }

            Bukkit.getLogger().info("[InventoryManager] Inventar + Status geladen für " + player.getName() + " in Welt " + world);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inventar zu Base64 serialisieren
    private String toBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Inventar aus Base64 laden
    private ItemStack[] fromBase64(String base64) {
        if (base64 == null) {
            Bukkit.getLogger().warning("[InventoryManager] Base64-String war null – Inventar konnte nicht geladen werden.");
            return new ItemStack[0];
        }

        try {
            byte[] data = Base64.getDecoder().decode(base64);
            BukkitObjectInputStream ois = new BukkitObjectInputStream(new ByteArrayInputStream(data));

            int length = ois.readInt();
            ItemStack[] items = new ItemStack[length];
            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) ois.readObject();
            }

            ois.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
    }
}
