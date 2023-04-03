package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class ServerChannelListenerThread  extends Thread{


    private final static Logger LOGGER = LoggerFactory.getLogger(ServerChannelListenerThread.class);

    private int port;
    private String webroot;
    private ServerSocketChannel serverSocketChannel;

    public ServerChannelListenerThread(int port) throws IOException{
        LOGGER.debug("CREATED");
        this.port = port;
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.bind(new InetSocketAddress(this.port));
    }
    @Override
    public void run(){
        while (true){
            try {
                var channel = serverSocketChannel.accept();
                LOGGER.info(" * Connection accepted: " + channel.getLocalAddress());
                AfspConnectionWorkerThreadChanneled workerThread = new AfspConnectionWorkerThreadChanneled(channel);
                workerThread.start();


            } catch (IOException e) {
                LOGGER.error("Error in listenerThread", e);
            }
        }
    }

}
