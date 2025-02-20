package com.mguhc.listener;

import com.mguhc.SkyDef;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class HeadListener implements Listener {

    private final List<UUID> playersWithNoFall = new ArrayList<>();

    @EventHandler
    private void OnDeath(PlayerDeathEvent event) {
        Player p = event.getEntity().getPlayer();
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(p.getName());
        skull.setItemMeta(meta);
        event.getDrops().add(skull);
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        if (e.getItem() == null) {
            return;
        }

        if(e.getItem().getType() == Material.SKULL_ITEM) {
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
                Player p = e.getPlayer();

                List<List<PotionEffect>> skulleffects = new ArrayList<>();
                skulleffects.add(Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, 8 * 60 * 20, 0)));
                skulleffects.add(Collections.singletonList(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 8 * 60 * 20, 0)));
                skulleffects.add(Collections.singletonList(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8 * 60 * 20, 0)));
                skulleffects.add(Collections.singletonList(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 8 * 60 * 20, 0)));
                skulleffects.add(Collections.singletonList(new PotionEffect(PotionEffectType.REGENERATION, 8 * 60 * 20, 0)));
                skulleffects.add(Collections.singletonList(new PotionEffect(PotionEffectType.ABSORPTION, 8*60*20, 1)));
                skulleffects.add(Arrays.asList(new PotionEffect(PotionEffectType.FAST_DIGGING, 8 * 60 * 20, 0), new PotionEffect(PotionEffectType.SATURATION, 8 * 60 * 20, 0)));
                skulleffects.add(Collections.singletonList(new PotionEffect(PotionEffectType.JUMP, 8 * 60 * 20, 0)));
                int r = (int) (Math.random() * (skulleffects.size()));
                List<PotionEffect> pe = skulleffects.get(r);
                for (PotionEffect potionEffect : pe) {
                    if (potionEffect.getType().equals(PotionEffectType.JUMP)) {
                        playersWithNoFall.add(p.getUniqueId());
                        p.sendMessage("Vous avez reçu l'effet No Fall");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                p.sendMessage("Vous n'avez plus l'effet No Fall");
                                playersWithNoFall.remove(p.getUniqueId());
                            }
                        }.runTaskLater(SkyDef.getInstance(), potionEffect.getDuration());
                    }
                    else {
                        p.addPotionEffect(potionEffect);
                        p.sendMessage("Vous avez reçu l'effet " + potionEffect.getType().getName());
                    }
                }

                if(p.getInventory().getItemInHand().getAmount() <= 1){
                    p.getInventory().setItemInHand(new ItemStack(Material.AIR));
                } else p.getInventory().getItemInHand().setAmount(p.getInventory().getItemInHand().getAmount() - 1);

                p.updateInventory();
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void OnDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            EntityDamageEvent.DamageCause cause = event.getCause();
            if (cause.equals(EntityDamageEvent.DamageCause.FALL) && playersWithNoFall.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
