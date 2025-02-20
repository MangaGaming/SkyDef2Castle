package com.mguhc.banner;

import java.util.UUID;

import com.mguhc.SkyDef;
import com.mguhc.manager.TeamEnum;
import com.mguhc.manager.TeamManager;
import com.mguhc.manager.WinCause;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BannerLock {

    public enum BannerState {
        LOCKED,
        UNLOCKING,
        UNLOCKED
    }

    private final TeamManager teamManager = SkyDef.getInstance().getTeamManager();

    private UUID unlocker;
    private final Location loc;
    private final String chatColor = ChatColor.RESET.toString();
    private BannerState state = BannerState.LOCKED;
    private final long time; // Temps total pour déverrouiller
    private long lastInteract;
    private int task = -1;
    private final String name;
    private long startUnlocking;
    private long totalTimeElapsed = 0; // Temps total écoulé

    public BannerLock(Location loc, int timeSecs, String name) {
        this.loc = loc;
        this.time = timeSecs * 1000L; // Convertir les secondes en millisecondes
        this.lastInteract = System.currentTimeMillis();
        this.name = name;
    }

    public Location getLocation() {
        return loc.clone();
    }

    public Player getUnlocker() {
        return Bukkit.getPlayer(unlocker);
    }

    public String getName() {
        return name;
    }

    public BannerState getState() {
        return state;
    }

    public void setState(BannerState state) {
        this.state = state;
    }

    public void updateLastInteract() {
        lastInteract = System.currentTimeMillis();
    }

    public void startUnlocking(Player player) {
        // Vérifier si la bannière est déjà en cours de déverrouillage par quelqu'un d'autre
        if (getState().equals(BannerState.UNLOCKING) && !getUnlocker().equals(player)) {
            player.sendMessage(ChatColor.RED + "Cette bannière est déjà en cours de crochetage.");
            return;
        }
        changeUnlocker(player);

        Bukkit.broadcastMessage("§6La bannière des " + name + " §6commence à être crochetée !");

        if (task != -1) {
            Bukkit.getScheduler().cancelTask(task);
        }

        updateLastInteract();
        startUnlocking = System.currentTimeMillis(); // Initialiser le temps de début

        task = Bukkit.getScheduler().runTaskTimer(SkyDef.getInstance(), () -> {
            // Calculer le pourcentage de progression
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startUnlocking; // Temps écoulé depuis le début du crochetage
            Player captain = teamManager.getCaptain(teamManager.getTeam(player));
            if (captain != null && captain.equals(player)) {
                totalTimeElapsed += elapsedTime*2; // Ajouter au temps total écoulé
            }
            double progress = (double) totalTimeElapsed / time;
            int percent = (int) (progress * 100);

            // Mettre à jour la barre d'action avec le pourcentage actuel
            sendActionBar(player, "Progression: " + percent + "%");

            // Vérifier si le crochetage a été abandonné
            if (lastInteract + 1000 < System.currentTimeMillis()) {
                if (!getState().equals(BannerState.UNLOCKED)) {
                    Bukkit.broadcastMessage("§6Le crochetage de la bannière du château des " + name + " §6a été abandonné !");
                }
                Bukkit.getScheduler().cancelTask(task);
                changeUnlocker(null);
            }

            // Vérifier si le temps de déverrouillage est écoulé
            if (totalTimeElapsed >= time) {
                setState(BannerState.UNLOCKED);
                if (name.equals("§cDémon")) {
                    SkyDef.getInstance().getGameManager().finishGame(TeamEnum.Ange, WinCause.Crochetage);
                } else if (name.equals("§9Anges")) {
                    SkyDef.getInstance().getGameManager().finishGame(TeamEnum.Demon, WinCause.Crochetage);
                }
                Bukkit.getScheduler().cancelTask(task);
            } else {
                // Réinitialiser le temps de début pour le prochain joueur
                startUnlocking = System.currentTimeMillis();
            }
        }, 1L, 1L).getTaskId();
    }

    public void changeUnlocker(Player newPlayer) {
        unlocker = newPlayer == null ? null : newPlayer.getUniqueId();
        startUnlocking = System.currentTimeMillis();

        if (newPlayer == null) {
            setState(BannerState.LOCKED);
        } else {
            setState(BannerState.UNLOCKING);
        }
    }

    public void removeFromTimer(int v) {
        totalTimeElapsed += v * 1000L;
    }

    public long getRemainingTime() {
        long remainingTime = (time - totalTimeElapsed) / 1000; // Convertir en secondes
        return Math.max(remainingTime, 0); // S'assurer que le temps restant ne soit pas négatif
    }

    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }

        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(chatBaseComponent, (byte) 2); // Type 2 = Action Bar

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}