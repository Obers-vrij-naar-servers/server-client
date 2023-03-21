package process;

enum MenuOption {
    SHOW_ALL_FILES("Show all files"),
    SYNC_FILES_TO_LOCAL_FOLDER("Sync files to local folder"),
    UPLOAD_FILES_TO_SERVER("Upload files to server"),
    EXIT("Exit");

    private final String label;

    MenuOption(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public int getOptionText() {
        return ordinal() + 1;
    }
}
