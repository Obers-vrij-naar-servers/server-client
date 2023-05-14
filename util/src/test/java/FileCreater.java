import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;

public class FileCreater {

    public Path sourceDir;

    public static String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
            + "Sed sed ex sit amet felis tincidunt ultrices. "
            + "Maecenas non justo sagittis, congue sapien sed, molestie risus. "
            + "Ut ornare est id enim convallis, vel euismod lorem vestibulum. "
            + "Suspendisse efficitur, tortor vel luctus dapibus, mauris orci laoreet libero, "
            + "in suscipit purus lacus in enim. Donec eu magna id nisl ultricies commodo. "
            + "Praesent euismod libero at mi pharetra eleifend. "
            + "Sed vel luctus enim, sed venenatis lectus. Donec tempor quam ac nunc vestibulum pharetra. "
            + "Aenean vel mi vel lorem molestie placerat sed vitae sapien. "
            + "Vivamus venenatis odio enim, vel commodo nisl ultricies et. "
            + "Nam sit amet mauris elit. Pellentesque habitant morbi tristique senectus et netus et malesuada "
            + "fames ac turpis egestas. Cras sit amet libero id enim ullamcorper posuere vitae nec risus.";

    private Path getSourceDir() throws IOException {
        // Create a temporary directory
        Path tempDir = Path.of(System.getProperty("java.io.tmpdir"));
        // Resolve the "sourceDir" subdirectory within the temporary directory
        Path sourceDir = tempDir.resolve("sourceDir");

        // Create the "sourceDir" subdirectory if it doesn't exist
        if (!Files.exists(sourceDir)) {
            Files.createDirectory(sourceDir);
        }

        return sourceDir;
    }

    public void createFile(long fileSize, String fileName, long lastModifiedTime) throws IOException {
        Path sourceDir = getSourceDir();
        // Create the file path using the temporary directory and specified filename
        Path filePath = sourceDir.resolve(fileName);

        // Create the file with the specified size and last modified time
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            raf.setLength(fileSize);
        }
        Files.setPosixFilePermissions(filePath, PosixFilePermissions.fromString("rw-r--r--"));
        Files.setLastModifiedTime(filePath, FileTime.fromMillis(lastModifiedTime));
        Files.setAttribute(filePath, "basic:lastModifiedTime", FileTime.fromMillis(lastModifiedTime));
    }

    public void createLoremIpsumFile(String fileName, long lastModifiedTime) throws IOException {
        Path sourceDir = getSourceDir();

        Path filePath = sourceDir.resolve(fileName);

        Files.write(filePath, loremIpsum.getBytes(StandardCharsets.UTF_8));
        Files.setPosixFilePermissions(filePath, PosixFilePermissions.fromString("rw-r--r--"));
        Files.setLastModifiedTime(filePath, FileTime.from(Instant.now()));
        Files.setAttribute(filePath, "basic:lastModifiedTime", FileTime.fromMillis(lastModifiedTime));
    }
}