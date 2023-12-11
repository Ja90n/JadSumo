package com.jadonvb.instances;

import com.jadonvb.GameState;
import com.jadonvb.countdowns.StartCountDown;
import com.jadonvb.countdowns.StopCountdown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;
import java.util.*;

public class Game {

    private GameState gameState;
    private final ArrayList<Player> players;
    private final ArrayList<Player> spectators;
    private final HashMap<UUID, Integer> points;
    private final StartCountDown startCountDown;
    private Task actionBarTask;
    private final Server server;


    public Game(Server server) {
        setGameState(GameState.RECRUITING);
        spectators = new ArrayList<>();
        players = new ArrayList<>();
        points = new HashMap<>();
        this.server = server;
        startCountDown = new StartCountDown(this);
    }

    public void start() {
        setGameState(GameState.LIVE);

        for (Player player : players) {
            points.put(player.getUuid(),0);
        }

        sendTitle(Component.text(" "),Component.text(" "));

        startActionBarTask();
        teleportPlayers();
    }

    public void stop() {
        for (Player player : getAllPlayers()) {
            player.kick(Component.text("Game concluded", TextColor.color(107, 242, 255)));
        }

        actionBarTask.cancel();

        Scheduler scheduler = MinecraftServer.getSchedulerManager();
        scheduler.scheduleNextTick(() -> {
            MinecraftServer.stopCleanly();
            System.out.println("Server ended");
        });

        System.exit(0);
    }

    public void addPlayer(Player player) {

        if (players.size() >= 2) {
            spectators.add(player);
            player.setGameMode(GameMode.SPECTATOR);
            player.setAllowFlying(true);
            player.setFlying(true);

            player.setAutoViewable(false);
            return;
        }

        player.setAutoViewable(true);

        players.add(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();

        if (players.size() >= 2) {
            if (gameState.equals(GameState.RECRUITING)) {
                startCountDown.start();
            }
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);

            if (!(players.size() <= 1)) {
               return;
            }

            if (gameState == GameState.COUNTDOWN) {
                startCountDown.cancel();
                setGameState(GameState.RECRUITING);

                if (!spectators.isEmpty()) {
                    Player spectator = spectators.get(0);
                    spectators.remove(spectator);
                    addPlayer(spectator);
                }
                return;
            }

            if (gameState == GameState.LIVE) {
                new StopCountdown(this,players.get(0).getUuid());
                return;
            }
            return;
        }

        spectators.remove(player);
    }

    public void replayRound(Player player) {

        points.put(player.getUuid(),points.get(player.getUuid()) + 1);

        for (UUID playerUUID : points.keySet()) {
            if (points.get(playerUUID) >= 5) {
                new StopCountdown(this, playerUUID);
            }
        }

        System.out.println(player.getUsername() + " won a round!");

        teleportPlayers();
    }

    public void startActionBarTask() {
        actionBarTask = MinecraftServer.getSchedulerManager().scheduleTask(() -> {

            try {
                Audiences.players().sendActionBar(
                        Component.text(players.get(0).getUsername())
                        .append(Component.text(": ")
                        .append(Component.text(points.get(players.get(0).getUuid()))))
                        .append(Component.text(" | "))
                        .append(Component.text(players.get(1).getUsername())
                        .append(Component.text(": ")
                        .append(Component.text(points.get(players.get(1).getUuid()))))));
            } catch (IndexOutOfBoundsException ignored) {}

        },TaskSchedule.tick(1),TaskSchedule.tick(10));
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        System.out.println(gameState);
    }

    public void sendTitle(Component title1, Component title2) {
        for (Player player : getAllPlayers()) {
            player.showTitle(Title.title(title1,title2,
                    Title.Times.times(Duration.ofSeconds(0),Duration.ofSeconds(2),Duration.ofMillis(200))));
        }
    }

    private Set<Player> getAllPlayers() {
        return server.getInstanceContainer().getPlayers();
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
