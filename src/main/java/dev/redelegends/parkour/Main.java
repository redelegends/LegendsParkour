package dev.redelegends.parkour;

import dev.redelegends.parkour.commands.Commands;
import dev.redelegends.parkour.database.DataBase;
import dev.redelegends.parkour.database.DataTypes;
import dev.redelegends.parkour.listeners.Listeners;
import dev.redelegends.parkour.lobby.LeaderboardNPC;
import dev.redelegends.parkour.parkour.ParkourManager;
import dev.redelegends.plugin.LegendsPlugin;
import org.bukkit.Bukkit;

public class Main extends LegendsPlugin {

    private static Main plugin;

    @Override
    public void start() {

    }

    @Override
    public void load() {
        plugin = this;
    }

    @Override
    public void enable() {
        DataBase.setupDataBases(DataTypes.SQLITE, this);
        Listeners.setupListeners();
        Commands.setupCommands();
        ParkourManager.setupParkours();
        LeaderboardNPC.setupNPCs();

        sendMessage("O plugin iniciou com sucesso!");
    }

    @Override
    public void disable() {

    }

    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage("§a[" + getDescription().getName() + "] " + message);
    }

    public void sendMessage(String message, String color) {
        Bukkit.getConsoleSender().sendMessage("§" + color + "[" + getDescription().getName() + "] " + message);
    }

    public static Main getInstance() {
        return plugin;
    }
}
