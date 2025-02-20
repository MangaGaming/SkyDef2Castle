package com.mguhc.listener;

import com.mguhc.SkyDef;
import com.mguhc.manager.TeamEnum;
import com.mguhc.manager.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TpListener implements Listener {

    @EventHandler
    private void OnMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        TeamManager teamManager = SkyDef.getInstance().getTeamManager();
        TeamEnum team = teamManager.getTeam(player);

        // Définition des emplacements de téléportation
        Location AngeDownTpLocation = new Location(Bukkit.getWorld("world"), -307, 65, 256);
        Location AngeHightTpLocation = new Location(Bukkit.getWorld("world"), -299, 163, 313);
        Location DemonDownTpLocation = new Location(Bukkit.getWorld("world"), 610, 79, -702);
        Location DemonHightTpLocation = new Location(Bukkit.getWorld("world"), 599, 128, -717);

        // Vérification de l'équipe
        if (team == null) {
            return;
        }

        // Téléportation pour l'équipe Ange
        if (team.equals(TeamEnum.Ange)) {
            if (isSameLocation(player.getLocation(), AngeDownTpLocation)) {
                player.teleport(AngeHightTpLocation.add(0, 1, 2));
            } else if (isSameLocation(player.getLocation(), AngeHightTpLocation)) {
                player.teleport(AngeDownTpLocation.add(0, 1, 2));
            }
        }
        // Téléportation pour l'équipe Demon
        else if (team.equals(TeamEnum.Demon)) {
            if (isSameLocation(player.getLocation(), DemonDownTpLocation)) {
                player.teleport(DemonHightTpLocation.add(0, 1, 2));
            } else if (isSameLocation(player.getLocation(), DemonHightTpLocation)) {
                player.teleport(DemonDownTpLocation.add(0, 1, 2));
            }
        }
    }

    // Méthode pour comparer les coordonnées arrondies
    private boolean isSameLocation(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) &&
                Math.round(loc1.getX()) == Math.round(loc2.getX()) &&
                Math.round(loc1.getY()) == Math.round(loc2.getY()) &&
                Math.round(loc1.getZ()) == Math.round(loc2.getZ());
    }
}