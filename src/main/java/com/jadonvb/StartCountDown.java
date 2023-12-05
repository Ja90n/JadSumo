package com.jadonvb;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class StartCountDown {

    private Game game;
    private Task task;
    private int countdownSeconds;

    public StartCountDown(Game game) {
        this.game = game;
        countdownSeconds = 8;
    }

    public void start() {

        game.setGameState(GameState.COUNTDOWN);

        task = MinecraftServer.getSchedulerManager().scheduleTask(() -> {

            if (countdownSeconds <= 0) {
                game.start();
                task.cancel();
            }

            game.sendTitle(Component.text("Game starting in ", TextColor.color(72, 128, 152))
                    .append(Component.text(String.valueOf(countdownSeconds),TextColor.color(255, 255, 255)))
                    .append(Component.text(" second",TextColor.color(72, 128, 152)))
                    .append(Component.text(countdownSeconds == 1 ? "" : "s",TextColor.color(72, 128, 152))
                            .append(Component.text("!",TextColor.color(72, 128, 152)))),Component.text(""));

            countdownSeconds--;
        }, TaskSchedule.tick(1),TaskSchedule.tick(20));
    }

    public void cancel() {
        task.cancel();
    }

}
