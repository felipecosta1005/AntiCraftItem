package org.anticraftitem.felipedev;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AntiCraftItem extends JavaPlugin implements Listener {

    private Set<Material> blockedItems;
    private String bypass_permission;
    private List<String> no_permission_message;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public void loadConfig() {
        this.blockedItems = new HashSet<>();

        for (String itemName : this.getConfig().getStringList("blocked-items")) {
            this.blockedItems.add(Material.valueOf(itemName));
        }

        this.bypass_permission = this.getConfig().getString("bypass-permission");
        this.no_permission_message = translateColorList(this.getConfig().getStringList("no-permission-message"));
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (!this.blockedItems.contains(item.getType())) {
            return;
        }

        if (player.hasPermission(bypass_permission + ".bypass") || player.hasPermission(bypass_permission + "." + item.getType().name())) {
            return;
        }

        event.setCancelled(true);
        for (String line : this.no_permission_message) {
            player.sendMessage(line);
        }
    }

    public List<String> translateColorList(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, ChatColor.translateAlternateColorCodes('&', lines.get(i)));
        }
        return lines;
    }
}
