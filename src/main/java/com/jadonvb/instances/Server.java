package com.jadonvb.instances;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
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

        MojangAuth.init();

        // Start the server
        minecraftServer.start("0.0.0.0", 25566);

        Game game = new Game(this);

        new Events(MinecraftServer.getGlobalEventHandler(), instanceContainer, game);
    }

    public InstanceContainer getInstanceContainer() {
        return instanceContainer;
    }
}