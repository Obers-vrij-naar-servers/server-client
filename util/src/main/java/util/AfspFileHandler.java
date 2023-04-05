package util;

import afsp.AfspHeader;
import afsp.exception.AfspProcessingException;
import afsp.AfspStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
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

    public void receiveFile(SocketChannel channel, long fileSize, int bufferSize, String fileName) throws IOException {
        Path path = Path.of(getFullPath(fileName));
        FileChannel fileChannel = FileChannel.open(path.toAbsolutePath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        while (fileSize > 0) {
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            fileSize -= bytesRead;
            buffer.flip();
            fileChannel.write(buffer);
            buffer.clear();
        }

        // close the file channel
        fileChannel.close();

        try {
            // read the file content from the channel into a ByteBuffer
            ByteBuffer fileContent = ByteBuffer.allocate((int) Files.size(path));
            fileChannel = FileChannel.open(path.toAbsolutePath(), StandardOpenOption.READ);
            fileChannel.read(fileContent);
            fileContent.flip();

            // convert the ByteBuffer into a string using the appropriate character set
            String content = StandardCharsets.UTF_8.decode(fileContent).toString();

            // write the string to a file using a FileWriter, and flush the content to make sure it is written to the file
            File output = new File(getFullPath(fileName + ".txt"));
            FileWriter fileWriter = new FileWriter(output);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private String getFullPath(String fileName){
        return localFileDir + FileSystems.getDefault().getSeparator() + fileName;
    }



}
