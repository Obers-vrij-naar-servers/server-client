package config;

public class Configuration{
    private int port;
    private String folder;
    private Boolean debug;

    public Configuration(int port, String folder, Boolean debug) {
        this.port = port;
        this.folder = folder;
        this.debug = debug;
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

    public Boolean getDebug() { return debug; }

    public void setDebug(Boolean debug) { this.debug = debug; }
}