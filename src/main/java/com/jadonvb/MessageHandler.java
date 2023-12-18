package com.jadonvb;

import com.jadonvb.instances.Server;

public class MessageHandler implements MessageListener {

    private Server server;

    public MessageHandler(Server server) {
        this.server = server;
    }

    @Override
    public void getMessage(Message message) {
        Logger logger =  new Logger("JadSumo");
        logger.log("hoihoi");
        switch (message.getType()) {
            case PORT_REQUEST -> {
                if (!message.getReceiver().equals(server.getIp())) {
                    return;
                }
                int port = Integer.parseInt(message.getArguments().get(0));
                if (port == -1) {
                    server.getLogger().error("Got invallid port!");
                    server.getLogger().error("Shutting down!");
                    System.exit(400);
                }
                server.setPort(port);
                server.startServer();
            }
        }
    }
}
