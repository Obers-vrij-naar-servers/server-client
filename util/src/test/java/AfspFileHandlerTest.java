import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.exception.AfspException;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.source.tree.AssertTree;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import util.AfspFileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

public class AfspFileHandlerTest {

    private TestServer server;
    private TestClient client;
    private SocketChannel serverChannel;
    private SocketChannel clientChannel;
    private Path tempFolderPath = Path.of(System.getProperty("java.io.tmpdir"));
    private Path sourceDir = tempFolderPath.resolve("sourceDir");
    private Path targetDir = tempFolderPath.resolve("targetDir");

    private AfspFileHandler serverFileHandler = new AfspFileHandler(targetDir.toString());
    private AfspFileHandler clientFileHandler = new AfspFileHandler(sourceDir.toString());
    private FileCreater fileCreater = new FileCreater();


    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        server = new TestServer();
        client = new TestClient();
        server.run();
        client.run();
        serverChannel = server.getChannel();
        clientChannel = client.getChannel();

    }

    @AfterEach
    public void cleanUp() throws IOException, InterruptedException {
        server.stopServer().interrupt();
        client.stopServer().interrupt();

        if (Files.exists(targetDir)) {
            try {
                Files.walk(targetDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                Files.delete(targetDir);
            } catch (Exception e) {

            }
        }
        try {
            if (Files.exists(sourceDir)) {
                // Walk the directory tree, deleting each file and subdirectory
                Files.walk(sourceDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                Files.delete(sourceDir);
            }
        } catch (Exception e) {

        }
    }

    @Test
    public void testSendLoremIpsum() throws InterruptedException, IOException {
        int tryCount = 0;
        //max 5 times to create the file
        while (tryCount < 5) {
            try {
                fileCreater.createLoremIpsumFile("lorem.txt", 1);
                break;
            } catch (IOException e) {
                if (tryCount < 5) {
                    tryCount += 1;
                } else {
                    fail("Could not create file to send");
                    return;
                }
            }
        }

        Thread serverThread = new Thread(() -> {
            try {
                serverFileHandler.receiveFile(clientChannel, 827, 8192, "lorem.txt", "1");
            } catch (IOException | AfspParsingException | AfspProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                clientFileHandler.sendFile("lorem.txt", 8192, serverChannel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Start and join the threads
        serverThread.start();
        clientThread.start();
        serverThread.join();
        clientThread.join();

        // get file-content
        String fileContent = new String(Files.readAllBytes(targetDir.resolve("lorem.txt")), StandardCharsets.UTF_8);
        //Assert
        assertTrue(Files.exists(targetDir.resolve("lorem.txt")));
        assertEquals(FileCreater.loremIpsum, fileContent);

    }

    @Test
    public void testFailOnWrongIdentifier() throws InterruptedException, IOException {
        int tryCount = 0;
        AtomicReference<AfspException> error = new AtomicReference<>(null);
        //max 5 times to create the file
        while (tryCount < 5) {
            try {
                fileCreater.createFile(1000, "wrongId.txt", 1);
                break;
            } catch (IOException e) {
                if (tryCount < 5) {
                    tryCount += 1;
                } else {
                    fail("Could not create file to send");
                    return;
                }
            }
        }

        Thread serverThread = new Thread(() -> {
            try {
                serverFileHandler.receiveFile(clientChannel, 827, 8192, "wrongId.txt", "2");
            } catch (AfspParsingException | AfspProcessingException e) {
                error.set(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                clientFileHandler.sendFile("wrongId.txt", 8192, serverChannel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Start and join the threads
        serverThread.start();
        clientThread.start();
        serverThread.join();
        clientThread.join();


        //Assert
        assertNotNull(error.get());
        assertFalse(Files.exists(targetDir.resolve("wrongId.txt")));
    }

    @Test
    public void testSend5GBFile() throws InterruptedException, IOException {
        long fileSize = Long.parseLong("5368709120");
        int tryCount = 0;
        //max 5 times to create the file
        while (tryCount < 5) {
            try {
                fileCreater.createFile(fileSize, "fiveGigs.txt", 1);
                break;
            } catch (IOException e) {
                if (tryCount < 5) {
                    tryCount += 1;
                } else {
                    fail("Could not create file to send");
                    return;
                }
            }
        }

        Thread serverThread = new Thread(() -> {
            try {
                serverFileHandler.receiveFile(clientChannel, fileSize, 8192, "fiveGigs.txt", "1");
            } catch (IOException | AfspParsingException | AfspProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        Thread clientThread = new Thread(() -> {
            try {
                clientFileHandler.sendFile("fiveGigs.txt", 8192, serverChannel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Start and join the threads
        serverThread.start();
        clientThread.start();
        serverThread.join();
        clientThread.join();

        //Assert
        assertTrue(Files.exists(targetDir.resolve("fiveGigs.txt")));
        assertEquals(fileSize, Files.size(targetDir.resolve("fiveGigs.txt")));
    }

    @Test
    public void testGetFileList() throws IOException, AfspProcessingException {
        // Arrange
        for (int i = 0; i < 5; i++) {
            fileCreater.createFile(100, "fileList_" + i + ".text", 1);
        }
        //act
        var fileList = clientFileHandler.getFileList();
        //Assert
        assertEquals(5, fileList.size());
    }

    @Test
    public void testGetFileInfo() throws IOException, AfspProcessingException {
        //Arrange
        fileCreater.createFile(1000, "fileInfo.txt", 100);
        //Act
        var request = new AfspRequest();
        List<AfspHeader> headerList = clientFileHandler.getFileInfo("fileInfo.txt");
        request.setHeaderList(headerList);
        var fileSizeHeader = request.getHeader(AfspHeader.HeaderType.FILE_SIZE);
        var identifierHeader = request.getHeader(AfspHeader.HeaderType.IDENTIFIER);
        //Assert
        assertEquals("1000", fileSizeHeader.getHeaderContent());
        assertEquals("100", identifierHeader.getHeaderContent());
    }

    @Test
    public void deleteFile() throws IOException {
        //Arrange
        fileCreater.createFile(1000, "deleteFile.txt", 100);
        fileCreater.createFile(1000, "dontDeleteFile.txt", 100);

        //Act
        try {
            clientFileHandler.deleteFile("deleteFile.txt");
        } catch (AfspProcessingException e) {
            fail("File not deleted");
        }
        //Assert
        assertFalse(Files.exists(sourceDir.resolve("deleteFile.txt")));
        assertTrue(Files.exists(sourceDir.resolve("dontDeleteFile.txt")));
    }

}
