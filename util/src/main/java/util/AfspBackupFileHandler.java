package util;

import afsp.AfspHeader;
import afsp.AfspStatusCode;
import afsp.exception.AfspParsingException;
import afsp.exception.AfspProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AfspBackupFileHandler {

    //SENDFILE AND RECEIVEFILE implemented by ROB
    private final Logger LOGGER = LoggerFactory.getLogger(AfspBackupFileHandler.class);

    private String localFileDir;
    private List<String> fileList = new ArrayList<>();

    public AfspBackupFileHandler(String folder) {
        this.localFileDir = folder;
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
    public void sendFile( String fileName, int bufferSize, SocketChannel socketChannel) throws IOException {
        Path filePath = Paths.get(localFileDir+ "/" + fileName);
        System.out.print("\033[3m\u001B[37mSending file to " + socketChannel.socket().getInetAddress().toString().substring(1) + ": \u001B[0m" + fileName + "...");

        FileChannel fileChannel = FileChannel.open(filePath);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        while (fileChannel.read(buffer) > 0) {

            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

        }
        System.out.print("\u001B[32m\033[1m SUCCESS!!\u001B[0m\n");
    }
    public void receiveFile(SocketChannel channel, long fileSize, int bufferSize, String fileName, String identifier) throws IOException, AfspParsingException {

        Path filePath = Paths.get(localFileDir+ "/" + fileName);

        // Mocht de Files-folder zijn verwijderd, maakt hij eerst een nieuwe aan.
        if(!Files.exists(Paths.get(localFileDir))) {
            Files.createDirectories(Paths.get(localFileDir));
        }

        System.out.print("\033[3m\u001B[37mDownloading file: \u001B[0m" + fileName + "...");

        long bytesWritten = 0;
        int progress = 0;
        int dotCount = 0;
        long tenPercent = fileSize / 10;

        // Maak de benodigde mappen aan
        Files.createDirectories(filePath.getParent());

        FileChannel fileChannel = FileChannel.open(filePath.toAbsolutePath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        while (channel.read(buffer) != -1) {
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

            if (fileChannel.size() >= fileSize) {
                break;
            }
        }

        long fileSizeClient = Files.size(filePath);

        if (fileSize != fileSizeClient) {
            fileChannel.close();
            Files.delete(filePath);
            throw new AfspParsingException(AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }

        fileChannel.close();

        FileTime lastModifiedTime = FileTime.fromMillis(Long.parseLong(identifier));
        Files.setAttribute(filePath, "basic:lastModifiedTime", lastModifiedTime, LinkOption.NOFOLLOW_LINKS);

        System.out.println("\u001B[32m\033[1mSUCCESS!!\u001B[0m");
    }
    public List<AfspHeader> getFileInfo(String fileName) throws AfspProcessingException {

        List<AfspHeader> headers = new ArrayList<>();
        updateFileList();
        boolean fileExists = false;

        LOGGER.info("** FILE-LIST: "+fileName+" **");
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
        LOGGER.info("** FILE-SIZE: "+fileSize+" **");
        LOGGER.info("** IDENTIFIER:"+" **");
        AfspHeader fileSizeHeader = new AfspHeader(AfspHeader.HeaderType.FILE_SIZE).setHeaderContent(String.valueOf(fileSize));
        AfspHeader identifierHeader = new AfspHeader(AfspHeader.HeaderType.IDENTIFIER).setHeaderContent(String.valueOf(identifier));
        headers.add(fileSizeHeader);
        headers.add(identifierHeader);
        return headers;
    }
}
