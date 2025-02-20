package com.mguhc.listener;

import com.mguhc.SkyDef;
import com.mguhc.manager.TeamEnum;
import com.mguhc.manager.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class TeamChatListener implements Listener {

    private final TeamManager teamManager = SkyDef.getInstance().getTeamManager();

    @EventHandler
    private void OnChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        TeamEnum team = teamManager.getTeam(player);

        if (message.contains("!") && team != null) {
            event.setCancelled(true); // Annuler l'événement de chat

            // Supprimer les "!" du message
            String cleanedMessage = message.replace("!", "");

            // Envoyer le message nettoyé aux autres membres de l'équipe
            for (Player p : teamManager.getPlayersInTeam(team)) {
                p.sendMessage("§6[Team] " + player.getName() + " : §f" + cleanedMessage);
            }
        }
    }
}
