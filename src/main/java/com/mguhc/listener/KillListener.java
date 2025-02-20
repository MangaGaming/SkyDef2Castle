package com.mguhc.listener;

import com.mguhc.SkyDef;
import com.mguhc.manager.PlayerManager;
import com.mguhc.manager.TeamEnum;
import com.mguhc.manager.TeamManager;
import com.mguhc.manager.WinCause;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillListener implements Listener {

    private final Map<Player, Player> playerKillerMap = new HashMap<>();

    @EventHandler
    private void OnDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity().getPlayer();
        Player killer = event.getEntity().getKiller();
        TeamManager teamManager = SkyDef.getInstance().getTeamManager();
        TeamEnum team = teamManager.getTeam(player);
        PlayerManager playerManager = SkyDef.getInstance().getPlayerManager();
        playerManager.removePlayer(player);
        if (killer != null) {
            playerManager.addKill(killer);
            playerKillerMap.put(player, killer);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1f, 1f);
        }
        if (team.equals(TeamEnum.Ange)) {
            Bukkit.broadcastMessage("§7[ §6Sky Defender §7] §9" + player.getName() + " §3a trouvé la MORT !");
        }
        else {
            Bukkit.broadcastMessage("§7[ §6Sky Defender §7] §c" + player.getName() +  "§3a trouvé la MORT !");
        }
        List<Player> players = playerManager.getPlayers();
        // Vérifier si tous les joueurs restants sont dans le même camp
        if (!players.isEmpty()) { // S'assurer qu'il reste des joueurs
            TeamEnum firstTeam = teamManager.getTeam(players.iterator().next()); // Obtenir le camp du premier joueur
            boolean allSameCamp = true;

            for (Player p : players) {
                TeamEnum currentCamp = teamManager.getTeam(p);
                if (currentCamp != null && !currentCamp.equals(firstTeam)) {
                    allSameCamp = false; // Si un joueur n'est pas dans le même camp, mettre à jour le drapeau
                    break;
                }
            }

            // Si tous les joueurs sont dans le même camp, finir le jeu
            if (allSameCamp) {
                SkyDef.getInstance().getGameManager().finishGame(firstTeam, WinCause.AllKill);
            }
        }
    }

    @EventHandler
    private void OnRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        TeamManager teamManager = SkyDef.getInstance().getTeamManager();
        TeamEnum teamEnum = teamManager.getTeam(player);
        if (teamEnum != null) {
            teamManager.removePlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player killer = playerKillerMap.get(player);
                    if (killer != null) {
                        player.teleport(killer);
                    }
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }.runTaskLater(SkyDef.getInstance(), 1);
        }
    }
}
