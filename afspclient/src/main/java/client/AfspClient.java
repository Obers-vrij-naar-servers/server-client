package client;

import config.Configuration;
import config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import process.ClientPrompt;

import static util.LoggerConfiguration.reloadLogbackConfiguration;


public class AfspClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(AfspClient.class);

    public static void main(String[] args)  {
        System.setProperty("org.jline.terminal.dumb", "true");

        ConfigurationManager.getInstance().initConfiguration(args);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        reloadLogbackConfiguration(AfspClient.class.getClassLoader().getResourceAsStream("client-logback.xml"));

        LOGGER.info("Client Starting...");
        LOGGER.info("Using Mode: " + (conf.getDebug() ? "Debug" : "Production"));
        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Folder: " + conf.getFolder());
        LOGGER.info("Using Host: " + conf.getHost());

        ClientPrompt clientPrompt = new ClientPrompt(conf);

        clientPrompt.start();
    }
}
