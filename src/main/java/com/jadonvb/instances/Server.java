package com.jadonvb.instances;

import com.jadonvb.*;
import com.jadonvb.enums.MessageType;
import com.jadonvb.enums.ServerType;
import com.jadonvb.messages.Message;
import com.jadonvb.messages.MessageHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.timer.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

public class Server {

    private InstanceContainer instanceContainer;
    private final Client client;
    private int port;
    private final Logger logger;
    private String ip;
    private TimerTask portTask;
    private boolean isRunning;

    public Server() {
        logger = new Logger("JadSumo");
        client = new Client(ServerType.GAME);

        isRunning = false;

        MessageHandler messageHandler = new MessageHandler(this);
        client.addMessageListener(messageHandler);

        getAssignedPort();
    }

    private void getAssignedPort() {
        logger.log("hoihois");
        portTask = new TimerTask() {
            @Override
            public void run() {
                String ip = getIP();
                if (getIP() == null) {
                    return;
                }

                Message message = new Message();
                message.setType(MessageType.PORT_REQUEST);
                message.setSender(ip);
                message.setReceiver("velocity");

                client.sendMessage(message);
                logger.log("Asked for port");
            }
        };

        long delay = 2500L;
        Timer timer = new Timer();
        timer.schedule(portTask, delay,1000);
    }

    public void startMinecraftServer() {
        isRunning = true;
        portTask.cancel();
        logger.log("Got port");
        logger.log("Starting on " + port);
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instanceContainer = instanceManager.createInstanceContainer(new AnvilLoader(Path.of("SimpleSumo")));

        VelocityProxy.enable("0d1yrkMhZEme"); // exposed secret does not matter yet

        // Start the server
        minecraftServer.start("0.0.0.0", port);

        logger.log("Hi guys");

        Game game = new Game(this);

        new Events(MinecraftServer.getGlobalEventHandler(), instanceContainer, game);
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

    public boolean isRunning() {
        return isRunning;
    }
}