package process;

import afsp.AfspHeader;
import afsp.AfspRequest;
import afsp.AfspResponse;
import afsp.AfspResponseParser;
import afsp.exception.AfspProcessingException;
import config.Configuration;
import config.ConfigurationManager;
import core.PromptResponse;
import factory.RequestFactory;
import util.AfspFileHandler;
import util.FileInfo;
import util.Helper;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static afsp.AfspStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR;

public class ListProcessor extends BaseProcessor {
    List<String> files;
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final RequestFactory requestFactory = new RequestFactory();
    private SocketChannel socketChannel;
    private AfspResponse response;

    public ListProcessor(PromptResponse promptResponse, Configuration conf) {
        super(promptResponse, conf);
    }

    public void process() throws Exception {

        try (SocketChannel socketChannel = SocketChannel.open()) {
            InetSocketAddress address = new InetSocketAddress(conf.getHost(), conf.getPort());
            socketChannel.connect(address);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            AfspRequest request = requestFactory.createRequest(promptResponse);

            buffer.put(request.toString().getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();

            AfspResponse response = new AfspResponse();

            AfspResponseParser parser = new AfspResponseParser();
            response = parser.parseResponse(socketChannel);
            LOGGER.info(" ** " + response.toString());
            String responseContent = response.getBody();
            // Strip \n from response
            responseContent = responseContent.substring(0, responseContent.length() - 1);

            List<AfspHeader> responseHeaders = response.getHeaderList();
            ;
            // Split response into separate file names
            String[] fileNames = responseContent.split("\\n");

            // Convert array to list
            files = Arrays.asList(fileNames);

            System.out.println();
            System.out.println("\u001B[36m" + "Files on the server:" + "\u001B[0m");
            System.out.println();

            // with index
            for (int i = 0; i < files.size(); i++) {
                System.out.println("\u001B[32m" + (i + 1) + ". " + files.get(i)  + "\u001B[0m");
            }

            AfspFileHandler.setTargetFiles(buildFileList(files, responseHeaders));

        } catch (Exception e) {
            throw new AfspProcessingException(SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        } finally {
            Helper.closeChanelConnections(socketChannel);
        }

    }


    public List<FileInfo> buildFileList(List<String> files, List<AfspHeader> responseHeaders) {

        var fileInfoList = new ArrayList<FileInfo>();

        for (String _file : files) {
            var headers = responseHeaders;
            var fileInfo = new FileInfo(_file, headers);
            fileInfoList.add(fileInfo);
        }

        return fileInfoList;
    }
}
