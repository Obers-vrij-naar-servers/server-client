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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}