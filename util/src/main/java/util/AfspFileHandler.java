package util;

import afsp.AfspHeader;
import afsp.exception.AfspProcessingException;
import afsp.AfspStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AfspFileHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(AfspFileHandler.class);

    private String localFileDir;
    private List<String> fileList = new ArrayList<>();


    public AfspFileHandler(String folder) {
        localFileDir = folder;
        System.out.println("File handler created");
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
        LOGGER.info("** FILE-SIZE: "+fileSize+" **");
        LOGGER.info("** IDENTIFIER:"+" **");
        var fileSizeHeader = new AfspHeader(AfspHeader.HeaderType.FILE_SIZE);
        fileSizeHeader.setHeaderContent(String.valueOf(fileSize));
        var identifierHeader = new AfspHeader(AfspHeader.HeaderType.IDENTIFIER);
        identifierHeader.setHeaderContent(String.valueOf(identifier));
        headers.add(fileSizeHeader);
        headers.add(identifierHeader);
        return headers;
    }

    public void sendFile(String fileName, SocketChannel channel,int bufferSize) throws IOException {
        LOGGER.info(" ** SENDING: " + localFileDir + FileSystems.getDefault().getSeparator() + fileName);
        long bytesWritten = 0;
        RandomAccessFile reader = new RandomAccessFile(localFileDir + FileSystems.getDefault().getSeparator() + fileName,"r");
        FileChannel fileChannel = reader.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        while (fileChannel.read(buffer) > 0){
            channel.write(buffer);
            bytesWritten += buffer.limit();
            buffer.clear();
        }
        fileChannel.close();
    }

    public void receiveFile(SocketChannel channel, long fileSize,String fileName){

        //FileChannel fileChannel = FileChannel.open()

    }



}
