package com.mguhc;

import com.mguhc.banner.BannerLock;
import com.mguhc.listener.*;
import com.mguhc.manager.*;
import com.mguhc.scoreboard.SkyDefScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ietf.jgss.Oid;

import javax.swing.plaf.PanelUI;

public class SkyDef extends JavaPlugin implements Listener {

    private static SkyDef instance;
    private TeamManager teamManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private DayManager dayManager;
    private EffectManager effectManager;
    private BannerLock angeBanner;
    private BannerLock demonBanner;

    private final Location center = new Location(Bukkit.getWorld("world"), 0, 151, 0);

    public void onEnable() {
        instance = this;
        teamManager = new TeamManager();
        playerManager = new PlayerManager();
        effectManager = new EffectManager();
        gameManager = new GameManager();
        angeBanner = new BannerLock(new Location(Bukkit.getWorld("world"), -299, 212, 374), 4*60, "§9Anges");
        demonBanner = new BannerLock(new Location(Bukkit.getWorld("world"), 599, 174, -770), 4*60, "§cDémons");
        dayManager = new DayManager();
        registerListener();
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new ConfigListener(), this);
        pluginManager.registerEvents(new KillListener(), this);
        pluginManager.registerEvents(new TpListener(), this);
        pluginManager.registerEvents(new TeamChatListener(), this);
        pluginManager.registerEvents(new BannerListener(angeBanner, demonBanner), this);
        pluginManager.registerEvents(new HeadListener(), this);

        pluginManager.registerEvents(dayManager, this);
    }

    @EventHandler
    private void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SkyDefScoreboard skyDefScoreboard = new SkyDefScoreboard();
        skyDefScoreboard.createScoreboard(player);
        if (gameManager.getState().equals(StateEnum.Waiting)) {
            player.teleport(center);
            clearAll(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 255));
            if (!playerManager.getPlayers().contains(player)) {
                playerManager.addPlayer(player);
            }
        }
    }

    @EventHandler
    private void OnQuit(PlayerQuitEvent event) {
        if (playerManager.getPlayers().contains(event.getPlayer()) && gameManager.getState().equals(StateEnum.Waiting)) {
            playerManager.removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    private void OnMove(PlayerMoveEvent event) {
        if (gameManager.getState().equals(StateEnum.Waiting)) {
            Player player = event.getPlayer();
            if (player.getLocation().getY() < 100) {
                player.teleport(center);
            }
        }
    }

    public void clearAll(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        effectManager.removeEffects(player);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static SkyDef getInstance() {
        return instance;
    }

    public Integer getTimer() {
        return dayManager.getTimer();
    }

    public Long getCrochetage(TeamEnum team) {
        if (team.equals(TeamEnum.Ange)) {
            return angeBanner.getRemainingTime();
        }
        else if (team.equals(TeamEnum.Demon)) {
            return demonBanner.getRemainingTime();
        }
        return 0L;
    }

    public int getDays() {
        return dayManager.getDays();
    }
}