package com.jadonvb;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Game {

    private GameState gameState;
    private InstanceContainer container;
    private ArrayList<Player> players;
    private Server server;
    private HashMap<UUID, Integer> points;
    private StartCountDown startCountDown;
    private Task actionBarTask;


    public Game(Server server, InstanceContainer container) {
        setGameState(GameState.RECRUITING);
        this.server = server;
        this.container = container;
        players = new ArrayList<>();
        points = new HashMap<>();
        startCountDown = new StartCountDown(this);
    }

    public void start() {
        setGameState(GameState.LIVE);

        for (Player player : players) {
            points.put(player.getUuid(),0);
        }

        startActionBarTast();
        teleportPlayers();
    }

    public void stop() {
        for (Player player : players.get(0).getInstance().getPlayers()) {
            player.kick(Component.text("Game concluded", TextColor.color(107, 242, 255)));
            System.out.println("kicked" + player.getUsername());
        }

        actionBarTask.cancel();

        Scheduler scheduler = MinecraftServer.getSchedulerManager();
        scheduler.scheduleNextTick(() -> {
            System.out.println("ënding server");
            MinecraftServer.stopCleanly();
            System.out.println("ënding done");
        });
    }

    public void addPlayer(Player player) {
        players.add(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();

        if (players.size() >= 2) {
            if (gameState.equals(GameState.RECRUITING)) {
                startCountDown.start();
            }
        }
        System.out.println(players.size());
    }

    public void removePlayer(Player player) {
        players.remove(player);

        if (gameState == GameState.COUNTDOWN) {
            if (players.size() <= 1) {
                startCountDown.cancel();
            }
        }
    }

    public void replayRound(Player player) {

        points.put(player.getUuid(),points.get(player.getUuid()) + 1);

        for (UUID playerUUID : points.keySet()) {
            if (points.get(playerUUID) >= 5) {
                new StopCountdown(this, playerUUID);
            }
        }



        teleportPlayers();
    }

    public void startActionBarTast() {
        actionBarTask = MinecraftServer.getSchedulerManager().scheduleTask(() -> {

            Audiences.players().sendActionBar(
            Component.text(players.get(0).getUsername())
            .append(Component.text(": ")
            .append(Component.text(points.get(players.get(0).getUuid()))))
            .append(Component.text(" | "))
            .append(Component.text(players.get(1).getUsername())
            .append(Component.text(": ")
            .append(Component.text(points.get(players.get(1).getUuid()))))));

        },TaskSchedule.tick(0),TaskSchedule.tick(10));
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        System.out.println(gameState);
    }

    public void sendTitle(Component title1, Component title2) {
        for (Player player : players) {
            player.showTitle(Title.title(title1,title2));
        }
    }

    private void teleportPlayers() {
        players.get(0).teleport(new Pos(0.5, 10, -3.5,0,0));
        players.get(1).teleport(new Pos(0.5, 10, 4.5,180,0));
    }


    public ArrayList<Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }
}
