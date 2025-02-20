package com.mguhc.scoreboard;

import com.mguhc.SkyDef;
import com.mguhc.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class SkyDefScoreboard {

    private final TeamManager teamManager;
    private final PlayerManager playerManager;

    public SkyDefScoreboard() {
        SkyDef blb = SkyDef.getInstance();
        teamManager = blb.getTeamManager();
        playerManager = blb.getPlayerManager();
    }

    public void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard(); // Créer un nouveau scoreboard

        // Créer l'objectif du scoreboard
        Objective objective = scoreboard.registerNewObjective("uhc", "dummy");
        objective.setDisplayName("§6 Sky Defender");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Initialisation des scores
        Score line = objective.getScore(" ");
        line.setScore(13);

        final Score[] dayCount = {objective.getScore("§6 Jour : §2" + getDaysString())};
        dayCount[0].setScore(12);

        final Score[] time = {objective.getScore("§6 Temps : §2" + formatTime(getTime()))};
        time[0].setScore(11);

        Score line1 = objective.getScore("§7 ___________");
        line1.setScore(10);

        final Score[] players = {objective.getScore("§6 Joueurs : §2" + getPlayerString())};
        players[0].setScore(9);

        final Score[] angel = {objective.getScore("§9 Ange : §2" + getAngeString())};
        angel[0].setScore(8);

        final Score[] demon = {objective.getScore("§c Demon : §2" + getDemonString())};
        angel[0].setScore(7);

        Score line2 = objective.getScore("§7___________");
        line2.setScore(6);

        Score crochetage = objective.getScore("§6Crochetage");
        crochetage.setScore(5);

        final Score[] angeCrochetage = { objective.getScore("§9Anges : " + getAngeCrochetageString()) };
        angeCrochetage[0].setScore(4);

        final Score[] demonCrochetage = { objective.getScore("§cDémons : " + getDemonCrochetageString()) };
        demonCrochetage[0].setScore(3);

        Score line3 = objective.getScore("§7___________§7");
        line3.setScore(2);

        final Score[] kill = {objective.getScore("§6 Kill : §2" + getEliminationsString(player))};
        kill[0].setScore(1);

        Score pub = objective.getScore("§9Dev by @MangaGaming");
        pub.setScore(0);

        // Appliquer le scoreboard initial au joueur
        player.setScoreboard(scoreboard);

        // Créer une tâche répétitive pour mettre à jour les scores
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel(); // Annuler la tâche si le joueur se déconnecte
                    return;
                }

                scoreboard.resetScores(dayCount[0].getEntry());
                dayCount[0] = objective.getScore("§6 Jour : §2" + getDaysString());
                dayCount[0].setScore(12);

                scoreboard.resetScores(time[0].getEntry());
                time[0] = objective.getScore("§6 Temps : §2" + formatTime(getTime()));
                time[0].setScore(11);

                scoreboard.resetScores(players[0].getEntry());
                players[0] = objective.getScore("§6 Joueurs : §2" + getPlayerString());
                players[0].setScore(9);

                scoreboard.resetScores(angel[0].getEntry());
                angel[0] = objective.getScore("§9 Ange : §2" + getAngeString());
                angel[0].setScore(8);

                scoreboard.resetScores(demon[0].getEntry());
                demon[0] = objective.getScore("§c Demon : §2" + getDemonString());
                demon[0].setScore(7);

                scoreboard.resetScores(angeCrochetage[0].getEntry());
                angeCrochetage[0] = objective.getScore("§9Anges : " + getAngeCrochetageString());
                angeCrochetage[0].setScore(4);

                scoreboard.resetScores(demonCrochetage[0].getEntry());
                demonCrochetage[0] = objective.getScore("§cDémons : " + getDemonCrochetageString());
                demonCrochetage[0].setScore(3);

                scoreboard.resetScores(kill[0].getEntry());
                kill[0] = objective.getScore("§6 Kill : §2" + getEliminationsString(player));
                kill[0].setScore(1);

                // Mettre à jour le préfixe et suffixe des joueurs
                updatePlayerTeams(scoreboard);


                // Rafraîchir le scoreboard du joueur
                player.setScoreboard(scoreboard);
            }
        }.runTaskTimer(SkyDef.getInstance(), 0, 20); // Mettre à jour toutes les secondes (20 ticks par seconde)
    }

    private void updatePlayerTeams(Scoreboard scoreboard) {
        for (Player player : playerManager.getPlayers()) {
            TeamEnum team = teamManager.getTeam(player);
            String prefix;
            Team scoreboardTeam;
            if (team != null) {
                prefix = (team == TeamEnum.Ange) ? ChatColor.BLUE + "[Ange] " : ChatColor.RED + "[Démon] ";
                scoreboardTeam = scoreboard.getTeam(team.name());
                if (scoreboardTeam == null) {
                    scoreboardTeam = scoreboard.registerNewTeam(team.name());
                }
                scoreboardTeam.setPrefix(prefix);
                scoreboardTeam.addEntry(player.getName());
            }
        }
    }

    private Integer getTime() {
        return SkyDef.getInstance().getTimer();
    }

    private String getDaysString() {
        return String.valueOf(SkyDef.getInstance().getDays());
    }

    private String getPlayerString() {
        return String.valueOf(playerManager.getPlayers().size());
    }

    private String getEliminationsString(Player player) {
        return String.valueOf(playerManager.getKills(player));
    }

    private String getAngeString() {
        return String.valueOf(teamManager.getPlayersInTeam(TeamEnum.Ange).size());
    }

    private String getDemonString() {
        return String.valueOf(teamManager.getPlayersInTeam(TeamEnum.Demon).size());
    }

    private String getAngeCrochetageString() {
        return String.valueOf(SkyDef.getInstance().getCrochetage(TeamEnum.Ange));
    }

    private String getDemonCrochetageString() {
        return String.valueOf(SkyDef.getInstance().getCrochetage(TeamEnum.Demon));
    }

    private String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}