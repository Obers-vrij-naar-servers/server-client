package server;

import config.Configuration;
import config.ConfigurationManager;
import core.ServerChannelListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Driver Class for the AFSP-Server
 */
public class AfspServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspServer.class);

    public static void main(String[] args) {
        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

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
