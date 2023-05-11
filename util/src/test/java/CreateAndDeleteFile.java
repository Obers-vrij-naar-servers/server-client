import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateAndDeleteFile {
    public static String createFile(long fileSize) throws IOException {
        String tempFolderPath = System.getProperty("java.io.tmpdir");
        File tempFile = new File(tempFolderPath, "tempFile.txt");

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            long remainingBytes = fileSize;
            while (remainingBytes > 0) {
                int bytesToWrite = (int) Math.min(buffer.length, remainingBytes);
                fos.write(buffer, 0, bytesToWrite);
                remainingBytes -= bytesToWrite;
            }
        }

        return tempFile.getPath();
    }

    public static void deleteFile(String filePath) {
        File fileToDelete = new File(filePath);
        boolean deleted = fileToDelete.delete();
        if (deleted) {
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("File could not be deleted.");
        }
    }
}
