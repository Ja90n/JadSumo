package com.jadonvb;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;

import java.nio.file.Path;

public class Server {

    private InstanceContainer instanceContainer;

    public Server() {
        startServer();
    }

    private void startServer() {
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instanceContainer = instanceManager.createInstanceContainer(new AnvilLoader(Path.of("SimpleSumo")));

        startEvents();

        // Start the server
        minecraftServer.start("0.0.0.0", 25565);
    }

    private void startEvents() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        //Login event
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0.5, 10, -3.5,0,0));
            System.out.println(player.getUsername() + " has joined the game!");
            Audiences.players().sendMessage(player.getName().append(Component.text(" has joined the game!", TextColor.color(248, 200, 220))));
        });

        //Disconnect event
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            final Player player = event.getPlayer();
            System.out.println(player.getUsername() + " has left the game!");
            Audiences.players().sendMessage(player.getName().append(Component.text(" has left the game!", TextColor.color(255, 49, 49))));
        });

        //Chat event
        globalEventHandler.addListener(PlayerChatEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setCancelled(true);
            Audiences.players().sendMessage(Component.text(player.getUsername(),TextColor.color(248, 200, 220)).append(Component.text(": ")).append(Component.text(event.getMessage(),TextColor.color(255,255,255))));
            System.out.println(player.getUsername() + ": " + event.getMessage());
        });

        //Fall event
        globalEventHandler.addListener(PlayerMoveEvent.class, event -> {
            final Player player = event.getPlayer();
            if (player.getPosition().y() < 0) {
                player.teleport(new Pos(0.5,10,0.5));
            }
        });
    }
}
