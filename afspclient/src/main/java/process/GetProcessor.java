package process;

import afsp.AfspHeader;
import afsp.AfspMethod;
import afsp.AfspRequest;
import afsp.AfspResponse;
import config.ConfigurationManager;
import util.AfspFileHandler;

import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class GetProcessor extends BaseProcessor {

    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());
    private final Socket socket;

    public GetProcessor(Socket socket, AfspRequest request, AfspResponse response) {
        super(request, response);
        this.socket = socket;
    }

    public void process() throws Exception {
        if (request.getMethod() == AfspMethod.GET) {
            // get the file content as bytes
            byte[] fileBytes = response.getBody().getBytes(StandardCharsets.UTF_8);


            SocketChannel socketChannel = socket.getChannel();

            if (socketChannel == null) {
                throw new Exception("Socket channel is null");
            }

            Long contentLength = 0L;
            int bufferSize = 0;

            for (AfspHeader header : response.getheaderList()) {
                if (header.getHeaderType() == AfspHeader.HeaderType.CONTENT_LENGTH) {
                    contentLength = Long.parseLong(header.getHeaderContent());
                }
            }

            fileHandler.receiveFile(socketChannel, contentLength, 8192, request.getTarget());

//                // read the image using javax.imageio
//                ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
//                BufferedImage image = ImageIO.read(bais);
//
//                // save the image to the target directory as a JPEG file
//                String filename = "example.jpg"; // replace with the actual filename
//                String targetDirectory = System.getProperty("user.home") + File.separator + "Downloads"; // use the Downloads directory in the user's home directory
//                Path path = Paths.get(targetDirectory, filename);
//                ImageIO.write(image, "JPEG", path.toFile());

        }
    }


}
