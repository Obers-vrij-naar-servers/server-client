package server;

import config.Configuration;
import config.ConfigurationManager;
import core.ServerChannelListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.LoggerConfiguration.reloadLogbackConfiguration;

/**
 * Driver Class for the AFSP-Server
 */
public class AfspServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspServer.class);

    public static void main(String[] args) {
        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        reloadLogbackConfiguration(AfspServer.class.getClassLoader().getResourceAsStream("server-logback.xml"));

        LOGGER.info("Server Starting...");
        LOGGER.info("Using Mode: " + (conf.getDebug() ? "Debug" : "Production"));
        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Folder: " + conf.getFolder());

        try {
            System.out.println("Starting ListenerThread");
            ServerChannelListenerThread serverListenerThread = new ServerChannelListenerThread(conf.getPort());
            serverListenerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO handle error
        }
    }
}
