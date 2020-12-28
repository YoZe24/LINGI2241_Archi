package ServerOpti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable{
    private final Socket clientSocket;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        )
        {
            String request = in.readLine();
            if(request.equals("Exit.")) {
                ServerLaunch.writeToFile(ServerLaunch.serviceTimes, "results/service_opti_" + ServerLaunch.nbThreads + "_hard.txt");
                this.clientSocket.close();
            }
            String answer = ServerLaunch.protocol.processInput(request);
            out.println(answer);
            System.out.println("-> "+ ++ServerLaunch.cpt +" : Client # served");
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + ServerLaunch.port + " or listening for a connection");
        }
    }
}