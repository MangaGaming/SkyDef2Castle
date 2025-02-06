package com.mguhc;

import com.mguhc.listener.ConfigListener;
import com.mguhc.manager.GameManager;
import com.mguhc.manager.PlayerManager;
import com.mguhc.manager.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyDef extends JavaPlugin {

    private static SkyDef instance;
    private TeamManager teamManager;
    private GameManager gameManager;
    private PlayerManager playerManager;

    public void onEnable() {
        instance = this;
        teamManager = new TeamManager();
        gameManager = new GameManager();
        playerManager = new PlayerManager();
        registerListener();
    }

    private void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ConfigListener(), this);

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
}