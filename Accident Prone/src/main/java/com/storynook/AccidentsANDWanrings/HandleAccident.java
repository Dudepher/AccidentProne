package com.storynook.AccidentsANDWanrings;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.storynook.PlaySounds;
import com.storynook.PlayerStats;
import com.storynook.Plugin;
import com.storynook.Event_Listeners.PantsCrafting;

import net.md_5.bungee.api.ChatColor;

public class HandleAccident {
    private static Plugin plugin;
        public HandleAccident(Plugin plugin) {
            this.plugin = plugin;
        }
        public static void handleAccident(boolean isBladder, Player player, Boolean PlaySound, Boolean MessageSent) {
        PlayerStats stats = plugin.getPlayerStats(player.getUniqueId());
        stats.setUrgeToGo(0);
        if (isBladder) {
            double wetbyamount;
            switch (stats.getLayers()) {
                case 0: wetbyamount = stats.getBladder(); break;
                case 1: wetbyamount = stats.getBladder()/1.5; break;
                case 2: wetbyamount = stats.getBladder()/2; break;
                case 3: wetbyamount = stats.getBladder()/3; break;
                case 4: wetbyamount = stats.getBladder()/4; break;
                default: wetbyamount = stats.getBladder(); break;
            }
            switch (stats.getUnderwearType()) {
                case 0:
                    if (stats.getLayers() > 0) {if(stats.getDiaperWetness() >= 100) {stats.increaseDiaperWetness(100);} else {stats.increaseDiaperWetness(50);}}
                    else {stats.increaseDiaperWetness(100);}
                    break;
                case 1: stats.increaseDiaperWetness(wetbyamount); break;
                case 2: stats.increaseDiaperWetness(wetbyamount/2); break;
                case 3: stats.increaseDiaperWetness(wetbyamount/4); break;
                default: break;
            }
            stats.setBladder(0);
            if (!stats.getBladderLockIncon()) {stats.increaseBladderIncontinence(0.5);}
        } else {
            if (stats.getUnderwearType() == 0) {stats.increaseDiaperFullness(100);}
            else if (stats.getUnderwearType() == 1) {stats.increaseDiaperFullness(stats.getBowels());}
            else if (stats.getUnderwearType() == 2) {stats.increaseDiaperFullness(stats.getBowels()/2);}
            else if (stats.getUnderwearType() == 3) {stats.increaseDiaperFullness(stats.getBowels()/4);}
            stats.setBowels(0);
            if(!stats.getBowelLockIncon()){stats.increaseBowelIncontinence(0.5);}
        }
        if (PlaySound) {
            PlaySounds.playsounds(player,(isBladder ? "pee" : "mess"), 5,1.0,0.2, false);
        }
        if (!MessageSent) {
            if (isBladder ? stats.getBladderIncontinence() == 10 && !stats.getBladderLockIncon() : stats.getBowelIncontinence() == 10 && !stats.getBowelLockIncon()) {
                player.sendMessage(ChatColor.RED + "Oh no! Someone has no control!");
            }
            else if (isBladder ? stats.getBladderIncontinence() > 7 : stats.getBowelIncontinence() > 7) {
                if (isBladder ? stats.getBladderLockIncon() : stats.getBowelLockIncon()) {
                    player.sendMessage("Some one likes using their pants, huh?");
                }
                else{
                    player.sendMessage(ChatColor.RED + "Seems like someone is losing control");
                }
            }
            else if (isBladder ? stats.getBladderIncontinence() > 3 : stats.getBowelIncontinence() > 3) {
                player.sendMessage(ChatColor.RED + "You should try to make it to the potty next time.");
            }else {
                player.sendMessage(ChatColor.RED + "Oh no! You had an accident...");
            }
        }
        
        if (stats.getDiaperWetness() >= 100 && stats.getDiaperFullness() >= 100) {
            changeLeggingsModel(player, 626018);
        }
        else if (stats.getDiaperWetness() >= 100 && stats.getDiaperFullness() < 100) {
            changeLeggingsModel(player, 626016);
        }
        else if(stats.getDiaperFullness() >= 100 && stats.getDiaperWetness() < 100){
            changeLeggingsModel(player, 626017);
        }
        if (stats.getvisableUnderwear()) {
            PantsCrafting.equipDiaperArmor(player, false, true);
        }
        // plugin.manageParticleEffects(player);
    }

    private static void changeLeggingsModel(Player player, int modelData) {
        ItemStack leggings = player.getInventory().getLeggings();
        if (leggings != null && leggings.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
            if (meta.getCustomModelData() == 626015 || meta.getCustomModelData() == 626016 || meta.getCustomModelData() == 626017) {
                meta.setCustomModelData(modelData);
                leggings.setItemMeta(meta);
                return;
            }
        }
    }
}
