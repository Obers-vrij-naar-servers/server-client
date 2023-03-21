package config;

public class Configuration{
    private int port;
    private String folder;
    private boolean debug;

    public Configuration(int port, String folder, boolean debug) {
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

    public String getHost() {
        return folder;
    }

    public void setHost(String folder) {
        this.folder = folder;
    }

    public String getFolder() { return folder; }

    public void setFolder(String folder) { this.folder = folder; }

    public boolean getDebug() { return debug; }

    public void setDebug(boolean debug) { this.debug = debug; }

}