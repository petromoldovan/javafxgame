package server;

import server.controller.Client;
import server.controller.ClientManager;
import server.controller.RoomManager;
import server.database.Database;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StartServer {
    public static ServerSocket serverSocket;
    public static ClientManager clientManager;
    public static RoomManager roomManager;
    public static volatile boolean isServerRunning = true;
    private static Database database;

    public static void main(String[] args) {
        new StartServer();
    }

    public StartServer () {
        try {
            database = new Database();
            database.create();
            
            int PORT = 5656;

            // create server
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port: " + PORT);

            // init manager
            clientManager = new ClientManager();
            roomManager = new RoomManager();

            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10,
                    10,
                    10,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(10)
            );

            // main loop
            while (isServerRunning) {
                try {
                    // blocking accept connection listener
                    Socket s = serverSocket.accept();
                    System.out.println("got new connection: " + s);

                    // create new runnable object
                    Client client = new Client(s);
                    // add client to the client manager
                    clientManager.addClient(client);

                    // execute runnable object
                    executor.execute(client);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    public static Database getDatabase() {
        return database;
    }
}
