package SocketProgramming;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server {
    public static ArrayList<Clients> clientInfo = new ArrayList<>(100);
    public static ArrayList<FileInfo> Files = new ArrayList<>(1000);
    public static long MAX_CHUNK_SIZE = 800;
    public static long MIN_CHUNK_SIZE = 500;
    public static long MAX_BUFFER_SIZE = 1000000000;
    public static long currently_using_space=0;
    static Random r = new Random();
    public static int CHUNK_SIZE = (int) (r.nextInt((int) (MAX_CHUNK_SIZE-MIN_CHUNK_SIZE)) + MIN_CHUNK_SIZE);
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket welcomeSocket = new ServerSocket(6666);
        while(true) {
            System.out.println("Waiting for connection...");
            Socket socket = welcomeSocket.accept();
            System.out.println("Connection established");

            // open thread
            Thread worker = new Worker(socket);
            worker.start();

        }

    }
}
