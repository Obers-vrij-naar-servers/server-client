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

    public static ConfigurationManager getInstance(){
        if (myConfigurationManager == null){
            myConfigurationManager = new ConfigurationManager();
        }
        return myConfigurationManager;
    }


    /**
     * used to load a config file by path
     * @param filePath
     */
    public void loadConfigurationFile(String filePath) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new AfspConfigurationException(e);
        }
        StringBuilder sb = new StringBuilder();
        int i;
        while (true){
            try {
                if (!(( i = fileReader.read()) != -1)) break;
            } catch (IOException e) {
                throw new AfspConfigurationException(e);
            }
            sb.append((char)i);
        }
        JsonNode conf = null;
        try {
            conf = Json.parse(sb.toString());
        } catch (JsonProcessingException e) {
            throw new AfspConfigurationException("Error parsing configuration-file",e);
        }

        try {
            myCurrentConfiguration = Json.fromJson(conf, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new AfspConfigurationException("Error parsing configuration-file internally",e);
        }
    }

    /**
     * Returns the current loaded configuration
     */
    public Configuration getCurrentConfiguration(){
        if (myCurrentConfiguration == null){
            throw new AfspConfigurationException("No current config");
        }
        return myCurrentConfiguration;

    }
}
