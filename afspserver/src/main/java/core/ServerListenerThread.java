//package core;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class ServerListenerThread extends Thread {
//
//    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);
//
//    private int port;
//    private String webroot;
//    private ServerSocket serverSocket;
//
//    public ServerListenerThread(int port) throws IOException {
//        this.port = port;
//        serverSocket = new ServerSocket(this.port);
//    }
//
//    @Override
//    public void run() {
//        while (true){
//            try {
//                Socket socket = serverSocket.accept();
//                LOGGER.info(" * Connection accepted: " + socket.getInetAddress() + " * ");
//                AfspConnectionWorkerThread workerThread = new AfspConnectionWorkerThread(socket);
//                workerThread.start();
//
//                //serverSocket.close();
//                //TODO HANDLE close
//
//            } catch (IOException e) {
//                LOGGER.error("Error in listenerThread", e);
//            }
//        }
//    }
//
//}
