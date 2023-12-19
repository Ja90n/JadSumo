package com.jadonvb;

import com.jadonvb.instances.Server;

public class Main {

    public static void main(String[] args) {

        new Server();
        Logger logger = new Logger("JadSumo");
        logger.log("Hoihoi");
    }
}