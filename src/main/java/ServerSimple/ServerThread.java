package ServerSimple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread implements Runnable{
    private final Socket clientSocket;
    public ServerThread(Socket clientSocket) { this.clientSocket = clientSocket; }

    @Override
    public void run() {
        try{
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            final String request = in.readLine();

            if(request.equals("Exit.")) {
                ServerLaunch.writeToFile(ServerLaunch.serviceTimes, "results/service_simple_16_hard.txt");
                this.clientSocket.close();
            }
            Protocol protocol = new Protocol();
            String output = protocol.processInput(request);
            out.println(output);
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + ServerLaunch.port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
