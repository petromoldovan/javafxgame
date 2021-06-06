package server.controller;

import server.StartServer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        while (StartServer.isServerRunning) {
            try {
                // read request from the client
                String data = dataInputStream.readUTF();
                System.out.println("GOT DATA!!!" + data);
            } catch (IOException e) {
                System.out.println("ERROR: Client#run" + e.getMessage());
                break;
            }
        }
    }

    public String sendData(String data) {
        try {
            // TODO: encrypt data?
            this.dataOutputStream.writeUTF(data);
            return "SUCCESS";
        } catch (IOException e) {
            System.out.println("ERROR: Client#sendData" + e.getMessage());
            return "FAILURE";
        }
    }
}
