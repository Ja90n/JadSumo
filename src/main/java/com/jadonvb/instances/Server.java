package com.jadonvb.instances;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.apache.velocity.app.Velocity;

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

        VelocityProxy.enable("0d1yrkMhZEme"); // exposed secret does not matter yet

        // Start the server
        minecraftServer.start("0.0.0.0", 30065);

        Game game = new Game(this);

        new Events(MinecraftServer.getGlobalEventHandler(), instanceContainer, game);
    }

    public InstanceContainer getInstanceContainer() {
        return instanceContainer;
    }
}