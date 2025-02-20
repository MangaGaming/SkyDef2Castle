package com.mguhc.manager;

import com.mguhc.SkyDef;
import com.mguhc.event.StartGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class GameManager {
    private StateEnum state = StateEnum.Waiting;

    public void startGame() {
        TeamManager teamManager = SkyDef.getInstance().getTeamManager();
        state = StateEnum.Playing;
        for (Player demon : teamManager.getPlayersInTeam(TeamEnum.Demon)) {
            for (PotionEffect potionEffect : demon.getActivePotionEffects()) {
                demon.removePotionEffect(potionEffect.getType());
            }
            demon.setGameMode(GameMode.SURVIVAL);
            demon.setMaxHealth(40);
            demon.setFoodLevel(20);
            demon.setHealth(demon.getMaxHealth());
            demon.getInventory().clear();
            demon.teleport(new Location(demon.getWorld(), 599, 158, -759));
        }
        for (Player ange : teamManager.getPlayersInTeam(TeamEnum.Ange)) {
            for (PotionEffect potionEffect : ange.getActivePotionEffects()) {
                ange.removePotionEffect(potionEffect.getType());
            }
            ange.setGameMode(GameMode.SURVIVAL);
            ange.setMaxHealth(40);
            ange.setFoodLevel(20);
            ange.setHealth(ange.getMaxHealth());
            ange.getInventory().clear();
            ange.teleport(new Location(ange.getWorld(), -299, 197, 362));
        }
        Bukkit.getPluginManager().callEvent(new StartGameEvent());
    }

    public void finishGame(TeamEnum winner, WinCause cause) {
        Location center = new Location(Bukkit.getWorld("world"), 0, 151, 0);
        if (winner.equals(TeamEnum.Ange)) {
            if (cause.equals(WinCause.AllKill)) {
                Bukkit.broadcastMessage("§7[§6 Sky Defender §7]  §6Les §cDémons §6ont tous été éliminés !");
            }
            else if (cause.equals(WinCause.Crochetage)) {
                Bukkit.broadcastMessage("§6La bannière des §cDémons §6a été crochetée !");
            }
            Bukkit.broadcastMessage("§7[§6 Sky Defender §7] §6Les §9Anges §6ont remporté la partie. GG à tout le monde !");
        }
        else if (winner.equals(TeamEnum.Demon)) {
            if (cause.equals(WinCause.AllKill)) {
                Bukkit.broadcastMessage("§7[§6 Sky Defender §7] §6Les §9Anges §6ont tous été éliminés !");
            }
            else if (cause.equals(WinCause.Crochetage)) {
                Bukkit.broadcastMessage("§6La bannière des §9Anges §6a été crochetée !");
            }
            Bukkit.broadcastMessage("§7[§6 Sky Defender §7] §6Les §cDémons §6ont remporté la partie. GG à tout le monde! ");
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer("§aLa partie est finie reconnecte toi pour recommencer");
                }
                Bukkit.reload();
            }
        }.runTaskLater(SkyDef.getInstance(), 5*60*20);
    }

    public StateEnum getState() {
        return state;
    }
}
