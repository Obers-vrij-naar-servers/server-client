package config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

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
        String host = "";
        String folder = "";

        for (String arg : args) {
            if (arg.startsWith("--port=")) {
                try {
                    port = Integer.parseInt(arg.substring(arg.lastIndexOf("=") + 1));
                } catch (NumberFormatException e) {
                    System.out.println("Port is not a number, hint don't forgot to use --port=");
                    System.exit(1);
                }
            }

            if (arg.startsWith("--host=")) {
                host = arg.substring(arg.lastIndexOf("=") + 1);
            }
            if (arg.startsWith("--folder=")) {
                folder = arg.substring(arg.lastIndexOf("=") + 1);
                if (!Files.exists(Path.of(folder))){
                    System.out.println("--folder does not exist");
                    System.exit(1);
                }
            }
        }

        if (host.isEmpty()) {
            System.out.println("Please provide a valid host, hint don't forgot to use --host=");
            System.exit(1);
        }

        if (port == -1) {
            System.out.println("Please provide a valid port, hint don't forgot to use --port=");
            System.exit(1);
        }
        if (folder.isEmpty()) {
            System.out.println("Please provide a valid folder, hint don't forgot to use --port=");
            System.exit(1);
        }


        myCurrentConfiguration = new Configuration(port, host, folder);
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
