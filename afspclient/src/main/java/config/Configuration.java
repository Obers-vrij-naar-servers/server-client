package config;

public class Configuration{
    private int port;
    private String folder;

    public Configuration(int port, String folder) {
        this.port = port;
        this.folder = folder;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return folder;
    }

    public void setHost(String folder) {
        this.folder = folder;
    }
}