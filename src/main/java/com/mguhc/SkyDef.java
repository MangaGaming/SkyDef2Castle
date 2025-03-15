package com.mguhc;

import com.mguhc.banner.BannerLock;
import com.mguhc.listener.*;
import com.mguhc.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class SkyDef extends JavaPlugin implements Listener {

    private static SkyDef instance;
    private TeamManager teamManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private DayManager dayManager;
    private BannerLock angeBanner;
    private BannerLock demonBanner;
    private PermissionManager permissionManager;

    public void onEnable() {
        instance = this;
        teamManager = new TeamManager();
        playerManager = new PlayerManager();
        permissionManager = new PermissionManager();
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

    public static void clearAll(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
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

    public PermissionManager getPermissionManager() {
        return permissionManager;
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