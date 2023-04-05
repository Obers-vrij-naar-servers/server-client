package process;

import java.util.List;

public class ProcessResult {
    private static List<String> files;
    private static int fileChoice;

    public static int getFileChoice() {
        return fileChoice;
    }

    public static void setFileChoice(int fileChoice) {
        ProcessResult.fileChoice = fileChoice;
    }

    public static List<String> getFiles() {
        return files;
    }

    public static void setFiles(List<String> files) {
        ProcessResult.files = files;
    }
}