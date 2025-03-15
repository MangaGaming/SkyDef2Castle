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
            if (isInCube(player.getLocation(),
                    AngeDownTpLocation.getBlockX() - 1,
                    AngeDownTpLocation.getBlockZ() - 1,
                    AngeDownTpLocation.getBlockX() + 1,
                    AngeDownTpLocation.getBlockZ() + 1)) {
                player.teleport(AngeHightTpLocation.add(0, 1, 2));
            } else if (isInCube(player.getLocation(),
                    AngeHightTpLocation.getBlockX() - 1,
                    AngeHightTpLocation.getBlockZ() - 1,
                    AngeHightTpLocation.getBlockX() + 1,
                    AngeHightTpLocation.getBlockZ() + 1)) {
                player.teleport(AngeDownTpLocation.add(0, 1, 2));
            }
        }
        // Téléportation pour l'équipe Demon
        else if (team.equals(TeamEnum.Demon)) {
            if (isInCube(player.getLocation(),
                    DemonDownTpLocation.getBlockX() - 1,
                    DemonDownTpLocation.getBlockZ() - 1,
                    DemonDownTpLocation.getBlockX() + 1,
                    DemonDownTpLocation.getBlockZ() + 1)) {
                player.teleport(DemonHightTpLocation.add(0, 1, 2));
            } else if (isInCube(player.getLocation(),
                    DemonHightTpLocation.getBlockX() - 1,
                    DemonHightTpLocation.getBlockZ() - 1,
                    DemonHightTpLocation.getBlockX() + 1,
                    DemonHightTpLocation.getBlockZ() + 1)) {
                player.teleport(DemonDownTpLocation.add(0, 1, -2));
            }
        }
    }

    private boolean isInCube(Location location, int x1, int z1, int x2, int z2) {
        // Obtenez les coordonnées de la location
        int locX = location.getBlockX();
        int locZ = location.getBlockZ();

        // Déterminez les limites du cube
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);

        // Vérifiez si la location est à l'intérieur des limites du cube
        return (locX >= minX && locX <= maxX) && (locZ >= minZ && locZ <= maxZ);
    }
}