package com.jadonvb.instances;

import com.jadonvb.Client;
import com.jadonvb.Logger;
import com.jadonvb.Message;
import com.jadonvb.MessageTypes;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;

public class Server {

    private InstanceContainer instanceContainer;
    private Client client;
    private int port;
    private Logger logger;
    private String ip;

    public Server() {
        logger = new Logger("JadSumo");
        client = new Client();
        port = getAssignedPort();
    }

    private String getIP() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            String string = String.valueOf(socket.getLocalAddress());
            socket.close();

            StringBuilder stringBuilder = new StringBuilder(string);
            stringBuilder.delete(0,1);

            ip = stringBuilder.toString();
            return stringBuilder.toString();
        } catch (IOException ignore) {}
        return null;
    }

    private int getAssignedPort() {

        String ip = getIP();
        if (getIP() == null) {
            return -1;
        }

        Message message = new Message();
        message.setType(MessageTypes.PORT_REQUEST);
        message.setSender(ip);
        message.setReceiver("velocity");

        client.sendMessage(message);

        return 0;
    }

    public void startServer() {
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instanceContainer = instanceManager.createInstanceContainer(new AnvilLoader(Path.of("SimpleSumo")));

        VelocityProxy.enable("0d1yrkMhZEme"); // exposed secret does not matter yet

        // Start the server
        minecraftServer.start("0.0.0.0", port);

        Game game = new Game(this);

        new Events(MinecraftServer.getGlobalEventHandler(), instanceContainer, game);
    }

    public InstanceContainer getInstanceContainer() {
        return instanceContainer;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getIp() {
        return ip;
    }
}