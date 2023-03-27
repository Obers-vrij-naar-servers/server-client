package core;

public class PromptResponse {
    private Action action;
    private String requestPath;
    private boolean isFile;

    public void setRequestPath() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestDirectory) {
        this.requestPath = requestDirectory;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }
}
