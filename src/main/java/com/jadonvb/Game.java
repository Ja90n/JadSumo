package com.jadonvb;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
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


    public Game(Server server, InstanceContainer container) {
        gameState = GameState.RECRUITING;
        this.server = server;
        this.container = container;
        players = new ArrayList<>();
        points = new HashMap<>();
        startCountDown = new StartCountDown(this);
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
        if (points.containsKey(player.getUuid())) {
            points.put(player.getUuid(),points.get(player.getUuid()) + 1);
        } else {
            points.put(player.getUuid(),1);
        }

        for (UUID playerUUID : points.keySet()) {
            if (points.containsKey(playerUUID)) {
                if (points.get(playerUUID) >= 5) {
                    new StopCountdown(this, playerUUID);
                }
            } else {
                points.put(playerUUID,1);
            }
        }

        players.get(0).teleport(new Pos(0.5, 10, -3.5,0,0));
        players.get(1).teleport(new Pos(0.5, 10, 4.5,180,0));
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void sendTitle(Component title1, Component title2) {
        for (Player player : players) {
            player.showTitle(Title.title(title1,title2));
        }
    }

    public void start() {
        gameState = GameState.LIVE;
        players.get(0).teleport(new Pos(0.5, 10, -3.5,0,0));
        players.get(1).teleport(new Pos(0.5, 10, 4.5,180,0));
    }

    public void stop() {
        for (Player player : players.get(0).getInstance().getPlayers()) {
            player.kick(Component.text("ggs"));
            System.out.println("kicked" + player.getUsername());
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }
}
