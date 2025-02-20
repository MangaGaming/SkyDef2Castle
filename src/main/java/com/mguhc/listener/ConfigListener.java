package com.mguhc.listener;

import com.mguhc.SkyDef;
import com.mguhc.manager.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigListener implements Listener {

    private final TeamManager teamManager;
    private final GameManager gameManager;
    private final PlayerManager playerManager; // Ajout de PlayerManager
    private final LuckPerms luckPerms;

    private final Map<Player, String> captainInputState = new HashMap<>();

    public ConfigListener() {
        SkyDef skyDef = SkyDef.getInstance();
        teamManager = skyDef.getTeamManager();
        gameManager = skyDef.getGameManager();
        playerManager = skyDef.getPlayerManager(); // Initialisation de PlayerManager
        luckPerms = LuckPermsProvider.get();
    }

    @EventHandler
    private void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (gameManager.getState().equals(StateEnum.Waiting)) {
            if (player.hasPermission("skydef.host")) {
                player.getInventory().addItem(getConfigItem());
            }
            player.getInventory().addItem(getTeamItem());
        }
    }

    @EventHandler
    private void OnInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && item.equals(getConfigItem())) {
            openConfigInventory(player);
        } else if (item != null && item.equals(getTeamItem())) {
            event.setCancelled(true);
            openTeamSelectionInventory(player);
        }
    }

    private void openConfigInventory(Player player) {
        Inventory configInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Configuration");

        // Ajouter l'épée en diamant pour le rôle de Mod
        ItemStack modItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta modMeta = modItem.getItemMeta();
        if (modMeta != null) {
            modMeta.setDisplayName(ChatColor.GREEN + "Mod");
            modItem.setItemMeta(modMeta);
        }
        configInventory.setItem(0, modItem); // Placer l'épée en diamant en slot 0

        // Ajouter la laine verte pour lancer la partie
        ItemStack startGameItem = new ItemStack(Material.WOOL, 1, (short) 5); // 5 pour la couleur verte
        ItemMeta startGameMeta = startGameItem.getItemMeta();
        if (startGameMeta != null) {
            startGameMeta.setDisplayName(ChatColor.GREEN + "Lancer la partie");
            startGameItem.setItemMeta(startGameMeta);
        }
        configInventory.setItem(4, startGameItem); // Placer au centre

        // Ajouter l'item "Host" en bas
        ItemStack hostItem = new ItemStack(Material.ANVIL);
        ItemMeta hostMeta = hostItem.getItemMeta();
        if (hostMeta != null) {
            hostMeta.setDisplayName(ChatColor.BLUE + "Host");
            hostItem.setItemMeta(hostMeta);
        }
        configInventory.setItem(8, hostItem); // Placer en bas à droite

        // Ajouter L'item capitaine
        ItemStack captainItem = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta captainMeta = captainItem.getItemMeta();
        if (captainMeta != null) {
            captainMeta.setDisplayName("§6Capitaine");
            captainItem.setItemMeta(captainMeta);
        }
        configInventory.setItem(3, captainItem);

        player.openInventory(configInventory);
    }

    private void openTeamSelectionInventory(Player player) {
        Inventory teamInventory = Bukkit.createInventory(null, 9, ChatColor.BLUE + "Choisir une Équipe");

        // Ajouter l'équipe Rouge
        ItemStack redTeamItem = new ItemStack(Material.WOOL, 1, (short) 14); // 14 pour la couleur rouge
        ItemMeta redMeta = redTeamItem.getItemMeta();
        if (redMeta != null) {
            redMeta.setDisplayName(ChatColor.RED + "Équipe Demon");
            redTeamItem.setItemMeta(redMeta);
        }
        teamInventory.setItem(3, redTeamItem); // Placer à la position 3

        // Ajouter l'équipe Bleue
        ItemStack blueTeamItem = new ItemStack(Material.WOOL, 1, (short) 11); // 11 pour la couleur bleue
        ItemMeta blueMeta = blueTeamItem.getItemMeta();
        if (blueMeta != null) {
            blueMeta.setDisplayName(ChatColor.BLUE + "Équipe Ange");
            blueTeamItem.setItemMeta(blueMeta);
        }
        teamInventory.setItem(5, blueTeamItem); // Placer à la position 5

        player.openInventory(teamInventory);
    }

    @EventHandler
    private void OnInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Configuration")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.DIAMOND_SWORD) {
                    // Ouvrir l'inventaire des Mods
                    openModInventory(player);
                } else if (clickedItem.getType() == Material.WOOL) {
                    player.sendMessage(ChatColor.GREEN + "Partie lancée");
                    player.closeInventory();
                    gameManager.startGame();
                } else if (clickedItem.getType() == Material.ANVIL) {
                    // Ouvrir l'inventaire des joueurs
                    openHostInventory(player);
                } else if (clickedItem.getType() == Material.DIAMOND_HELMET) {
                    openCaptainInventory(player);
                }
            }
        } else if (event.getView().getTitle().equals(ChatColor.BLUE + "Choisir une Équipe")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.WOOL && clickedItem.getDurability() == 14) {
                    teamManager.removePlayer(player);
                    teamManager.addPlayer(player, TeamEnum.Demon);
                    player.sendMessage(ChatColor.RED + "Vous avez rejoint l'équipe Demon !");
                    player.closeInventory(); // Fermer l'inventaire après la sélection
                } else if (clickedItem.getType() == Material.WOOL && clickedItem.getDurability() == 11) {
                    teamManager.removePlayer(player);
                    teamManager.addPlayer(player, TeamEnum.Ange);
                    player.sendMessage(ChatColor.BLUE + "Vous avez rejoint l'équipe Ange !");
                    player.closeInventory(); // Fermer l'inventaire après la sélection
                }
            }
        }
    }

    private void openHostInventory(Player player) {
        // Créer un inventaire de 54 slots
        Inventory hostInventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Sélectionner un Host");

        // Récupérer tous les joueurs en ligne
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Créer un item pour chaque joueur
            ItemStack playerItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); // Utiliser une tête de joueur
            ItemMeta meta = playerItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(onlinePlayer.getName()); // Nom du joueur
                List<String> lore = new ArrayList<>();
                // Vérifier si le joueur a déjà la permission
                if (onlinePlayer.hasPermission("skydef.host")) {
                    lore.add(ChatColor.RED + "Déjà Host");
                } else {
                    lore.add(ChatColor.GREEN + "Cliquez pour donner le statut de Host");
                }
                meta.setLore(lore);
                playerItem.setItemMeta(meta);
            }
            // Ajouter l'item à l'inventaire
            hostInventory.addItem(playerItem);
        }

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(hostInventory);
    }

    private void openModInventory(Player player) {
        // Créer un inventaire de 54 slots
        Inventory modInventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Sélectionner un Mod");

        // Récupérer tous les joueurs en ligne
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Créer un item pour chaque joueur
            ItemStack playerItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); // Utiliser une tête de joueur
            ItemMeta meta = playerItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(onlinePlayer.getName()); // Nom du joueur
                List<String> lore = new ArrayList<>();
                // Vérifier si le joueur a déjà la permission
                if (onlinePlayer.hasPermission("skydef.host")) {
                    lore.add(ChatColor.RED + "Déjà Mod");
                } else {
                    lore.add(ChatColor.GREEN + "Cliquez pour donner le statut de Mod");
                }
                meta.setLore(lore);
                playerItem.setItemMeta(meta);
            }
            // Ajouter l'item à l'inventaire
            modInventory.addItem(playerItem);
        }

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(modInventory);
    }

    private void openCaptainInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "De quel équipe ?");

        // Ajouter l'équipe Rouge
        ItemStack redTeamItem = new ItemStack(Material.WOOL, 1, (short) 14); // 14 pour la couleur rouge
        ItemMeta redMeta = redTeamItem.getItemMeta();
        if (redMeta != null) {
            redMeta.setDisplayName(ChatColor.RED + "Équipe Demon");
            redTeamItem.setItemMeta(redMeta);
        }
        inventory.setItem(3, redTeamItem); // Placer à la position 3

        // Ajouter l'équipe Bleue
        ItemStack blueTeamItem = new ItemStack(Material.WOOL, 1, (short) 11); // 11 pour la couleur bleue
        ItemMeta blueMeta = blueTeamItem.getItemMeta();
        if (blueMeta != null) {
            blueMeta.setDisplayName(ChatColor.BLUE + "Équipe Ange");
            blueTeamItem.setItemMeta(blueMeta);
        }
        inventory.setItem(5, blueTeamItem); // Placer à la position 5

        player.openInventory(inventory);
    }

    @EventHandler
    private void onHostInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Sélectionner un Host")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Vérifier si l'item cliqué est un item de joueur
            if (clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
                String playerName = clickedItem.getItemMeta().getDisplayName();
                Player selectedPlayer = Bukkit.getPlayer(playerName);

                if (selectedPlayer != null) {
                    User user = luckPerms.getUserManager().getUser (selectedPlayer.getUniqueId());
                    if (selectedPlayer.hasPermission("skydef.host")) {
                        // Retirer la permission api.mod
                        assert user != null;
                        user.data().remove(Node.builder("skydef.host").build());
                        luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur

                        player.sendMessage(ChatColor.RED + selectedPlayer.getName() + " n'est plus Host.");
                        selectedPlayer.sendMessage(ChatColor.RED + "Vous avez été retiré du statut de Host.");

                    } else {
                        if (user != null) {
                            // Ajouter la permission api.host
                            user.data().add(Node.builder("skydef.host").build());
                            luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur

                            player.sendMessage(ChatColor.GREEN + selectedPlayer.getName() + " a maintenant le statut de Host.");
                            selectedPlayer.sendMessage(ChatColor.GREEN + "Vous avez été promu au statut de Host.");

                            selectedPlayer.getInventory().addItem(getConfigItem()); // Donner l'étoile du Nether au joueur
                            selectedPlayer.updateInventory();
                        } else {
                            player.sendMessage(ChatColor.RED + "Erreur : Impossible de récupérer les données de permission pour " + selectedPlayer.getName());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void OnModInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Sélectionner un Mod")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Vérifier si l'item cliqué est un item de joueur
            if (clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
                String playerName = clickedItem.getItemMeta().getDisplayName();
                Player selectedPlayer = Bukkit.getPlayer(playerName);

                if (selectedPlayer != null) {
                    User user = luckPerms.getUserManager().getUser (selectedPlayer.getUniqueId());
                    if (selectedPlayer.hasPermission("skydef.mod")) {
                        // Retirer la permission blb.mod
                        assert user != null;
                        user.data().remove(Node.builder("skydef.mod").build());
                        luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur

                        player.sendMessage(ChatColor.RED + selectedPlayer.getName() + " n'est plus Mod.");
                        selectedPlayer.sendMessage(ChatColor.RED + "Vous avez été retiré du statut de Mod.");
                        playerManager.addPlayer(selectedPlayer);
                    } else {
                        if (user != null) {
                            // Ajouter la permission blb.mod
                            user.data().add(Node.builder("skydef.mod").build());
                            luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur

                            player.sendMessage(ChatColor.GREEN + selectedPlayer.getName() + " a maintenant le statut de Mod.");
                            selectedPlayer.sendMessage(ChatColor.GREEN + "Vous avez été promu au statut de Mod.");
                            playerManager.removePlayer(selectedPlayer);
                        } else {
                            player.sendMessage(ChatColor.RED + "Erreur : Impossible de récupérer les données de permission pour " + selectedPlayer.getName());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    private void OnCaptainInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("De quel équipe ?")) {
            Player player = (Player) event.getWhoClicked();
            ItemStack item = event.getCurrentItem();
            if (item == null || !item.hasItemMeta()) {
                return;
            }
            if (item.getType() == Material.WOOL && item.getDurability() == 14) {
                captainInputState.put(player, "demonCaptain");
                player.sendMessage("Veuillez maintenant entrer le pseudo du joueur que vous voulez mettre capitaine");
                player.closeInventory(); // Fermer l'inventaire après la sélection
            } else if (item.getType() == Material.WOOL && item.getDurability() == 11) {
                captainInputState.put(player, "angeCaptain");
                player.sendMessage("Veuillez maintenant entrer le pseudo du joueur que vous voulez mettre capitaine");
                player.closeInventory(); // Fermer l'inventaire après la sélection
            }
        }
    }

    @EventHandler
    private void OnChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String state = captainInputState.get(player);
        if (state == null) {
            return;
        }
        if (state.equals("demonCaptain")) {
            Player p = Bukkit.getPlayer(message);
            if (p != null) {
                teamManager.setCaptain(p, TeamEnum.Demon);
                player.sendMessage("§a" + p.getName() + " a été mis capitaine de l'équipe §cDemon");
            }
            else {
                player.sendMessage("§cLe joueur n'existe pas");
            }
            captainInputState.remove(p);
            event.setCancelled(true);
        }
        else if (state.equals("angeCaptain")) {
            Player p = Bukkit.getPlayer(message);
            if (p != null) {
                teamManager.setCaptain(p, TeamEnum.Ange);
                player.sendMessage("§a" + p.getName() + " a été mis capitaine de l'équipe §9Ange");
            }
            else {
                player.sendMessage("§cLe joueur n'existe pas");
            }
            captainInputState.remove(p);
            event.setCancelled(true);
        }
    }

    private ItemStack getConfigItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Config");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack getTeamItem() {
        ItemStack item = new ItemStack(Material.BANNER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Choisir son équipe");
            item.setItemMeta(meta);
        }
        return item;
    }
}