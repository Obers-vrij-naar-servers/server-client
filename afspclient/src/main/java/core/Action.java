package core;

public enum Action {
    SHOW_ALL_FILES("Show all files"),
    Download_FILE("Download file"),
//    UPLOAD_FILES_TO_SERVER("Upload files to server"),
    DELETE_FILE_FROM_SERVER("Delete file from server"),
    EXIT("Exit");

    private final String label;

    Action(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
