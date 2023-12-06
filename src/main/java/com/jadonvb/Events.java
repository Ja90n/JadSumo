package com.jadonvb;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.InstanceContainer;

public class Events {

    private final GlobalEventHandler globalEventHandler;
    private final InstanceContainer container;
    private final Game game;

    public Events(GlobalEventHandler globalEventHandler, InstanceContainer container, Game game) {
        this.globalEventHandler = globalEventHandler;
        this.container = container;
        this.game = game;

        startEvents();
    }

    private void startEvents() {
        hitEvent();
        loginEvent();
        disconnectEvent();
        chatEvent();
        moveEvent();
    }

    private void hitEvent() {
        globalEventHandler.addListener(EntityAttackEvent.class, event -> {
            final Player source = (Player) event.getEntity();
            final Player target = (Player) event.getTarget();

            if (!(game.getPlayers().contains(source) && game.getPlayers().contains(target))) {
                return;
            }

            target.takeKnockback(0.34f, Math.sin(source.getPosition().yaw() * 0.017453292), -Math.cos(source.getPosition().yaw() * 0.017453292));

            target.damage(DamageType.fromEntity(source), 0);

        });
    }

    private void loginEvent() {
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();

            player.setGameMode(GameMode.ADVENTURE);
            event.setSpawningInstance(container);
            player.setRespawnPoint(new Pos(0.5, 10, 0.5,0,0));

            System.out.println(player.getUsername() + " has joined the game!");
            Audiences.players().sendMessage(player.getName().append(Component.text(" has joined the game!", TextColor.color(248, 200, 220))));

            game.addPlayer(player);
        });
    }

    private void disconnectEvent() {
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            final Player player = event.getPlayer();

            System.out.println(player.getUsername() + " has left the game!");
            Audiences.players().sendMessage(player.getName().append(Component.text(" has left the game!", TextColor.color(255, 49, 49))));

            game.removePlayer(player);
        });
    }

    private void chatEvent() {
        globalEventHandler.addListener(PlayerChatEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setCancelled(true);
            Audiences.players().sendMessage(Component.text(player.getUsername(),TextColor.color(248, 200, 220)).append(Component.text(": ")).append(Component.text(event.getMessage(),TextColor.color(255,255,255))));
            System.out.println(player.getUsername() + ": " + event.getMessage());
        });
    }

    private void moveEvent() {
        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            if (player.getPosition().y() > 0) {
                return;
            }

            if (!game.getPlayers().contains(player)) {
                return;
            }

            if (!game.getGameState().equals(GameState.LIVE)) {
                player.teleport(new Pos(0.5, 10, 0.5,0,0));
                return;
            }

            if (game.getPlayers().size() == 2) {
                for (Player winner : game.getPlayers()) {
                    if (!(winner.equals(player))) {
                        game.replayRound(winner);
                    }
                }
            } else{
                System.out.println("huh???");
            }
        });
    }
}