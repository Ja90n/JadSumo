package com.jadonvb;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.UUID;

public class StopCountdown {

    private Game game;
    private Task task;
    private int countdownSeconds;
    private Player player;

    public StopCountdown(Game game, UUID playerUuid) {
        this.game = game;

        player = MinecraftServer.getConnectionManager().getPlayer(playerUuid);
        countdownSeconds = 5;
        start();
    }

    private void start() {

        game.setGameState(GameState.ENDED);

        task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {

            game.sendTitle(Component.text(player.getUsername())
                    .append(Component.text(" has won the game!", TextColor.color(255, 137, 217))),
                    Component.text("Thank you for playing",TextColor.color(226, 221, 221)));

            if (countdownSeconds <= 0) {
                game.stop();
                task.cancel();
            }

            countdownSeconds--;
        }, TaskSchedule.tick(1),TaskSchedule.tick(20));
    }
}
