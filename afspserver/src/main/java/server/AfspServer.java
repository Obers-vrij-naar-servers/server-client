package server;

import config.Configuration;
import config.ConfigurationManager;
import core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Driver Class for the AFSP-Server
 *
 */
public class AfspServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspServer.class);
    public static void main(String[] args){
        LOGGER.info("Server Starting...");

        ConfigurationManager.getInstance().loadConfigurationFile("C:\\school\\obers\\afspserver\\src\\main\\resources\\config.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Folder: " + conf.getFolder());

        try{
            System.out.println("Starting ListenerThread");
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(),conf.getFolder());
            serverListenerThread.start();
        }catch (Exception e){
            e.printStackTrace();
            //TODO handle error
        }
    }
}
