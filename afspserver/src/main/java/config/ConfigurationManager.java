package config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import util.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ConfigurationManager {

    /**
     * Singleton
     */
    private static ConfigurationManager myConfigurationManager;
    private static Configuration myCurrentConfiguration;

    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (myConfigurationManager == null) {
            myConfigurationManager = new ConfigurationManager();
        }
        return myConfigurationManager;
    }


    /**
     * used to initialize the configuration
     *
     * @param args
     */
    public void initConfiguration(String[] args) {

        int port = -1;
        String targetFolder = "";

        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                try {
                    port = Integer.parseInt(arg.substring(arg.lastIndexOf("=") + 1));
                } catch (NumberFormatException e) {
                    System.out.println("Port is not a number.\nHint don't forgot to use --port=");
                    System.exit(1);
                }
            }

            if (arg.startsWith("--folder=")) {
                targetFolder = arg.substring(arg.lastIndexOf("=") + 1);
            }
        }

        if (targetFolder.isEmpty()) {
            System.out.println("Please provide a folder location for retrieving files.\nHint don't forgot to use --folder=");
            System.exit(1);
        }

        if (port == -1) {
            System.out.println("Please provide a valid port'\nHint don't forgot to use --port=");
            System.exit(1);
        }

        myCurrentConfiguration = new Configuration(port, targetFolder);
    }

    /**
     * Returns the current loaded configuration
     */
    public Configuration getCurrentConfiguration() {
        if (myCurrentConfiguration == null) {
            throw new AfspConfigurationException("No current config");
        }
        return myCurrentConfiguration;

    }
}
