package util;

public class PathHelper {
    public static boolean isFile(String path) {
        String[] pathParts = path.split("/");

        if (pathParts.length == 0) {
            try {
                throw new Exception("No input provided.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String lastElement = pathParts[pathParts.length - 1];

        return lastElement.contains(".");
    }
}
