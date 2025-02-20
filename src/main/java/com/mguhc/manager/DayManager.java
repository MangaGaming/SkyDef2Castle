package com.mguhc.manager;

import com.mguhc.SkyDef;
import com.mguhc.event.StartGameEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class DayManager implements Listener {
    private boolean isDay = true;
    private int timer = 0;
    private int dayTimer = 0;
    private int dayCount = 0;

    @EventHandler
    private void OnStart(StartGameEvent event) {
        dayCount = 1;
        World world = Bukkit.getWorld("world");
        new BukkitRunnable() {
            @Override
            public void run() {
                dayTimer ++;
                if (dayTimer == 10*60) {
                    if (isDay) {
                        world.setTime(13000);
                    }
                    else {
                        dayCount ++;
                        world.setTime(1000);
                        Bukkit.broadcastMessage("§7[ §6Sky Defender §7] §6Début du jour " + dayCount + ".");
                    }
                    isDay = !isDay;
                    dayTimer = 0;
                }
                if (dayTimer == 10*60-30 && !isDay) {
                    Bukkit.broadcastMessage("§7[ §6Sky Defender §7] §6Début du jour " + (dayCount + 1) + " dans 30 secondes.");
                }
                timer ++;
            }
        }.runTaskTimer(SkyDef.getInstance(), 0, 20);
    }

    public int getDays() {
        return dayCount;
    }

    public int getTimer() {
        return timer;
    }
}
