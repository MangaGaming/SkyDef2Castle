package com.mguhc.listener;

import com.mguhc.SkyDef;
import com.mguhc.manager.TeamEnum;
import com.mguhc.manager.TeamManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mguhc.banner.BannerLock;

public class BannerListener implements Listener {

    private final BannerLock demonBanner;
    private final BannerLock angeBanner;

    public BannerListener(BannerLock angeBanner, BannerLock demonBanner) {
        this.angeBanner = angeBanner;
        this.demonBanner = demonBanner;
    }

    @EventHandler
    private void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (isSameLocation(block.getLocation(), angeBanner.getLocation()) || isSameLocation(block.getLocation(), demonBanner.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }
        if (block != null) {
            Location blockLoc = block.getLocation();
            Location angeLoc = angeBanner.getLocation();
            Location demonLoc = demonBanner.getLocation();

            if (isSameLocation(blockLoc, angeLoc) && SkyDef.getInstance().getDays() >= 6) {
                if (angeBanner.getState().equals(BannerLock.BannerState.UNLOCKING)) {
                    angeBanner.updateLastInteract();
                } else if (angeBanner.getState().equals(BannerLock.BannerState.LOCKED)) {
                    angeBanner.startUnlocking(player);
                }
            } else if (isSameLocation(blockLoc, demonLoc) && SkyDef.getInstance().getDays() >= 6) {
                if (demonBanner.getState().equals(BannerLock.BannerState.UNLOCKING)) {
                    demonBanner.updateLastInteract();
                } else if (demonBanner.getState().equals(BannerLock.BannerState.LOCKED)) {
                    demonBanner.startUnlocking(player);
                }
            }
        }
    }

    // Méthode pour comparer les coordonnées arrondies
    private boolean isSameLocation(Location loc1, Location loc2) {
        return  loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }

    @EventHandler
    private void OnDeath(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        TeamManager teamManager = SkyDef.getInstance().getTeamManager();
        if (teamManager.getTeam(player).equals(TeamEnum.Ange)) {
            angeBanner.removeFromTimer(30);
        }
        if (teamManager.getTeam(player).equals(TeamEnum.Demon)) {
            demonBanner.removeFromTimer(30);
        }
    }
}