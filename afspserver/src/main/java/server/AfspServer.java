package server;

import afsp.exception.AfspProcessingException;
import config.Configuration;
import config.ConfigurationManager;
import core.ServerChannelListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AfspFileHandler;

/**
 * Driver Class for the AFSP-Server
 */
public class AfspServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspServer.class);

    public static void main(String[] args) {
        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        AfspFileHandler fileHandler = new AfspFileHandler(conf.getFolder());

        LOGGER.info("Server Starting...");
        LOGGER.info("Using Mode: " + (conf.getDebug() ? "Debug" : "Production"));
        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Folder: " + conf.getFolder());
        try {
            LOGGER.info("FILES:"+fileHandler.getFileList().toString());
        } catch (AfspProcessingException e) {
            e.printStackTrace();
        }

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
