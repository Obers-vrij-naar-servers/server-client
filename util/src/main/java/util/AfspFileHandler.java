package util;

import afsp.AfspHeader;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import afsp.AfspStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AfspFileHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(AfspFileHandler.class);

    private String localFileDir;
    private List<String> fileList = new ArrayList<>();
    private static List<FileInfo> targetFiles;
    private static int fileChoice;

    public AfspFileHandler(String folder) {
        localFileDir = folder;
        updateFileList();
    }

    private void updateFileList(){
        File file = new File(localFileDir);
        if (file.isDirectory() && file.exists()){
            fileList = Arrays.asList(file.list());
        }
    }

    public List<String> getFileList() throws AfspProcessingException {
        if (fileList == null || fileList.size() == 0){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
        return fileList;
    }

    public List<AfspHeader> getFileInfo(String fileName) throws AfspProcessingException {

        List<AfspHeader> headers = new ArrayList<>();
        updateFileList();
        boolean fileExists = false;

        for(String _fName : fileList){
            if( _fName.equals(fileName)){
                fileExists = true;
            }
        }
        if (!fileExists){
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
        Path path = Paths.get(this.localFileDir +"/"+fileName);
        long fileSize;
        long identifier;
        try{
            fileSize = Files.size(path);
            identifier = Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime().toMillis();
        } catch (IOException e){
            throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        AfspHeader fileSizeHeader = new AfspHeader(AfspHeader.HeaderType.FILE_SIZE).setHeaderContent(String.valueOf(fileSize));
        AfspHeader identifierHeader = new AfspHeader(AfspHeader.HeaderType.IDENTIFIER).setHeaderContent(String.valueOf(identifier));
        headers.add(fileSizeHeader);
        headers.add(identifierHeader);
        return headers;
    }

    public void sendFile( String fileName, int bufferSize, SocketChannel socketChannel) throws IOException {
        Path filePath = Paths.get(localFileDir+ "/" + fileName);
        System.out.print("\033[3m\u001B[37mSending file to " + socketChannel.socket().getInetAddress().toString() + ": \u001B[0m" + fileName + "...");

        FileChannel fileChannel = FileChannel.open(filePath);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        while (fileChannel.read(buffer) > 0) {
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }
        System.out.print("\u001B[32m\033[1m SUCCESS!!\u001B[0m\n");
    }

    public void receiveFile(SocketChannel channel, long fileSize, int bufferSize, String fileName, String identifier) throws IOException, AfspParsingException, AfspProcessingException {

        Path filePath = Paths.get(localFileDir+ "/" + fileName);
        Path tempFilePath = Paths.get(localFileDir+ "/" + fileName + ".bak");

        //test if fileFolder still exists, otherwise create it
        if(!Files.exists(Paths.get(localFileDir))) {
            LOGGER.info("Creating backup for " + fileName);
            Files.createDirectories(Paths.get(localFileDir));
        }

//        // Create backup file if the file already exists on server
//        if (Files.exists(filePath)) {
//            LOGGER.info(" ** CREATING BACKUP ** ");
//            Files.move(filePath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
//        }

        System.out.print("\033[3m\u001B[37mDownloading file: \u001B[0m" + fileName + "...");

        long bytesWritten = 0;
        int progress = 0;
        int dotCount = 0;
        long tenPercent = fileSize / 10;

        // Create directories
        Files.createDirectories(filePath.getParent());

        FileChannel fileChannel = FileChannel.open(tempFilePath.toAbsolutePath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        while (channel.read(buffer) >= 0) {
            buffer.flip();
            fileChannel.write(buffer);
            bytesWritten += buffer.limit();
            buffer.clear();

            int newProgress = (int) ((bytesWritten * 100) / fileSize);
            if (newProgress > progress) {
                progress = newProgress;
                if (bytesWritten >= (tenPercent * dotCount)) {
                    System.out.print(".");
                    dotCount++;
                }
            }
            if (bytesWritten >= fileSize) {
                break;
            }
        }
        System.out.println("/n/n");
        //check if the filesize of the newly created file matches the header, and if not delete it and throw an error
        long fileSizeClient = Files.size(tempFilePath);
        if (fileSize != fileSizeClient) {
            LOGGER.info("** RECEIVED CORRUPT FILE: " + fileName + " **");
            Files.delete(tempFilePath);
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        //else if it matched, move the temp file to the permanent location
        else {
            LOGGER.info(" ** DELETING BACKUP ** ");
            Files.move(tempFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);        }

        fileChannel.close();

        FileTime lastModifiedTime = FileTime.fromMillis(Long.parseLong(identifier));
        Files.setAttribute(filePath, "basic:lastModifiedTime", lastModifiedTime, LinkOption.NOFOLLOW_LINKS);

        System.out.println("\u001B[32m\033[1mSUCCESS!!\u001B[0m");
    }

    public void deleteFile(String targetFile) throws AfspProcessingException {
        Path path = Path.of(localFileDir+"/"+targetFile);
        if(Files.exists(path)) {
            try {
                // Attempt to delete the file
                Files.delete(path);
            } catch (IOException e) {
                throw new AfspProcessingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new AfspProcessingException(AfspStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
    }

    private String getFullPath(String fileName){
        return localFileDir + FileSystems.getDefault().getSeparator() + fileName;
    }

    public static int getFileChoice() {
        return fileChoice;
    }

    public static void setFileChoice(int fileChoice) {
        AfspFileHandler.fileChoice = fileChoice;
    }

    public static List<FileInfo> getTargetFiles() {
        return targetFiles;
    }

    public static void setTargetFiles(List<FileInfo> targetFiles) {
        AfspFileHandler.targetFiles = targetFiles;
    }
}
