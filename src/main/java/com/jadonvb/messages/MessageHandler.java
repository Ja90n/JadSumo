package com.jadonvb.messages;

import com.jadonvb.instances.Server;

public class MessageHandler implements MessageListener {

    private Server server;

    public MessageHandler(Server server) {
        this.server = server;
    }

    @Override
    public void getMessage(Message message) {
        switch (message.getType()) {
            case PORT_REQUEST -> portRequest(message);
            case CLIENT_NOT_FOUND -> System.exit(400);
        }
    }

    private void portRequest(Message message) {
        if (server.isRunning()) {
            return;
        }

        if (!message.getReceiver().equals(server.getIp())) {
            System.out.println("??");
            return;
        }
        int port = Integer.parseInt(message.getArguments().get(0));
        if (port == -1) {
            server.getLogger().error("Got invallid port!");
            server.getLogger().error("Shutting down!");
            System.exit(400);
        }
        server.setPort(port);
        server.startMinecraftServer();
    }

}
