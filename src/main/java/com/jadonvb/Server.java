package com.jadonvb;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;

import java.nio.file.Path;

public class Server {

    public Server() {
        startServer();
    }

    private void startServer() {
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer(new AnvilLoader(Path.of("SimpleSumo")));

        MojangAuth.init();

        // Start the server
        minecraftServer.start("0.0.0.0", 25566);

        Game game = new Game();

        new Events(MinecraftServer.getGlobalEventHandler(), instanceContainer, game);
    }
}