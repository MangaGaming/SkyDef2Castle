package com.mguhc.listener;

import com.mguhc.SkyDef;
import com.mguhc.manager.GameManager;
import com.mguhc.manager.TeamManager;
import org.bukkit.event.Listener;

public class ConfigListener implements Listener {

    private final TeamManager teamManager;
    private final GameManager gameManager;

    public ConfigListener() {
        SkyDef skyDef = SkyDef.getInstance();
        teamManager = skyDef.getTeamManager();
        gameManager = skyDef.getGameManager();
    }


}
