package dev.redelegends.parkour.parkour;

import dev.redelegends.parkour.parkour.config.ParkourConfig;
import dev.redelegends.parkour.parkour.objects.PlayerTimerCache;
import dev.redelegends.Core;
import dev.redelegends.parkour.Main;
import dev.redelegends.parkour.player.PlayerTimer;
import dev.redelegends.plugin.config.LegendsConfig;
import dev.redelegends.parkour.utils.ItemBuilder;
import dev.redelegends.utils.enums.EnumSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("ALL")
public class ParkourManager implements Listener {

    private static final List<ParkourManager> PARKOURS_CACHE = new ArrayList<>();
    private static final List<String> COOLDOWN = new ArrayList<>();
    private final String key;
    private final ParkourConfig config;
    private final Set<PlayerTimerCache> PLAYERS_PARKOUR;

    public static void setupParkours() {
        LegendsConfig CONFIG = Main.getInstance().getConfig("parkour");

        if (CONFIG.getFile().length() > 1) {
            for (String key : CONFIG.getSection("parkours").getKeys(false)) {
                ParkourManager parkourManager = new ParkourManager(key);
                PARKOURS_CACHE.add(parkourManager);
            }
        }
    }

    public static ParkourManager getParkour(String key) {
        return PARKOURS_CACHE.stream().filter(pakourManager -> pakourManager.getKey().equals(key)).findFirst().orElse(null);
    }

    public static ParkourManager getParkourWithPlayer(Player  player) {
        return PARKOURS_CACHE.stream().filter(pakourManager -> pakourManager.isPlayerParkour(player.getName())).findFirst().orElse(null);
    }

    public static void createParkour(List<String> locations, String key) {
        LegendsConfig CONFIG = Main.getInstance().getConfig("parkour");
        CONFIG.set("parkours." + key + ".locations", locations);
        CONFIG.save();
        ParkourManager parkourManager = new ParkourManager(key);
        PARKOURS_CACHE.add(parkourManager);
    }

    public static void deleteParkour(String key) {
        LegendsConfig CONFIG = Main.getInstance().getConfig("parkour");
        CONFIG.set("parkours." + key + ".locations", null);
        CONFIG.save();
        ParkourManager parkourManager = ParkourManager.getParkour(key);
        parkourManager.destroy();
        PARKOURS_CACHE.remove(parkourManager);
    }

    public ParkourManager(String key) {
        LegendsConfig CONFIG = Main.getInstance().getConfig("parkour");
        config = new ParkourConfig(key, CONFIG.getRawConfig());
        config.setupConfig();
        PLAYERS_PARKOUR = new HashSet<>();
        this.key = key;

        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onPlayerParkourStart(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Location loc = player.getLocation().getBlock().getLocation();

        if (loc.getWorld().equals(Core.getLobby().getWorld())) {
            if (player.getAllowFlight()) {
                if (isPlayerParkour(player.getName())) {
                    removePlayer(player.getName());
                    player.sendMessage("§cNão é permitido usar fly enquanto faz o parkour.");
                    //CosmeticsAPI.enable(player);
                    return;
                }
            }
            if (this.config.getLOCATIONS().containsValue(loc)) {
                if (!isPlayerParkour(player.getName())) {
                    if (Objects.equals(this.config.getKeyForLocation(loc), "0")) {
                        player.sendMessage("§b§lPARKOUR §aVocê acaba de iniciar o parkour!");
                        addPlayer(player.getName());
                        inventory.setItem(3, new ItemBuilder(Material.GOLD_PLATE).displayname("§aVoltar ao Checkpoint").build());
                        inventory.setItem(5, new ItemBuilder(Material.REDSTONE).displayname("§aParar Parkour").build());
                        PlayerTimer playerTimer = PlayerTimer.createPlayerTimer(player.getName());
                        playerTimer.startCountTime();
                        player.setAllowFlight(false);
                        //CosmeticsAPI.disable(player);
                        COOLDOWN.add(player.getName());
                        EnumSound.ORB_PICKUP.play(player, 1.0F, 1.0F);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                COOLDOWN.remove(player.getName());
                            }
                        }.runTaskLaterAsynchronously(Main.getInstance(), 20 * 3L);
                    }
                    return;
                }

                int checkpoint = Integer.parseInt(getConfig().getKeyForLocation(loc));
                if (getPlayerTimeCache(player.getName()) != null) {
                    int checkpointNow = Integer.parseInt(getPlayerTimeCache(player.getName()).getCheckPoint());
                    if (checkpointNow + 1 == checkpoint) {
                        if (getConfig().getLOCATIONS().size() - 1 == checkpoint) {
                            PlayerTimer timer = PlayerTimer.loadPlayerTimerProfiler(player.getName());
                            Long currentTime = timer.getCurrentTime();
                            if (timer.isNull()) {
                                player.sendMessage("§e§lNOVO RECORDE! §aParabéns por completar o parkour, tempo gasto: " + timer.formatedTime(currentTime));
                                timer.setTime(String.valueOf(currentTime));
                                timer.resetCurrentTime();
                                removePlayer(player.getName());
                                EnumSound.LEVEL_UP.play(player, 1.0F, 1.0F);
                                leaveParkour(player);
                            } else {
                                if (currentTime < timer.getTime()) {
                                    player.sendMessage("§e§lNOVO RECORDE! §aParabéns por completar o parkour, tempo gasto: " + timer.formatedTime(currentTime));
                                    timer.setTime(String.valueOf(currentTime));
                                } else {
                                    player.sendMessage("§aParabéns por completar o Parkour! Você levou " + timer.formatedTime(currentTime));
                                }
                                timer.resetCurrentTime();
                                removePlayer(player.getName());
                                EnumSound.LEVEL_UP.play(player, 1.0F, 1.0F);
                                leaveParkour(player);
                            }
                            return;
                        }

                        getPlayerTimeCache(player.getName()).setCheckPoint(String.valueOf(checkpoint));
                        player.sendMessage("§aVocê conquistou o §b§lCheckpoint #" + checkpoint);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (getPlayerTimeCache(player.getName()) != null) {
            removePlayer(player.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (getPlayerTimeCache(player.getName()) != null) {
            removePlayer(player.getName());
        }
    }

    public PlayerTimerCache getPlayerTimeCache(String name) {
        return PLAYERS_PARKOUR.stream().filter(playerTimerCache -> playerTimerCache.getName().equals(name)).findFirst().orElse(null);
    }

    public void addPlayer(String name) {
        PlayerTimerCache playerTimerCache = new PlayerTimerCache(name);
        PLAYERS_PARKOUR.add(playerTimerCache);
    }

    public void removePlayer(String name) {
        if (isPlayerParkour(name)) {
            PLAYERS_PARKOUR.remove(getPlayerTimeCache(name));
        }
    }

    public void leaveParkour(Player player) {
        //CosmeticsAPI.enable(player);
        if (player.hasPermission("legendscore.fly")) {
            player.setAllowFlight(true);
        }
        player.getInventory().setItem(3, new ItemStack(Material.AIR));
        player.getInventory().setItem(5, new ItemStack(Material.AIR));
    }

    public boolean isPlayerParkour(String name) {
        return getPlayerTimeCache(name) != null;
    }

    public ParkourConfig getConfig() {
        return config;
    }

    public String getKey() {
        return key;
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
        this.config.destroy();
    }
}
