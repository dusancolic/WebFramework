package example.server;

import example.RouteMapper;
import example.tester.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException {
        RouteMapper routeMapper = new RouteMapper();
        Test test = new Test();
        routeMapper.registerRoutes(Test.class, test);
        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:"+TCP_PORT);
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket, routeMapper)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void run(RouteMapper routeMapper) {
        try {
            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket, routeMapper)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
