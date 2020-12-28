package ServerOpti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable{
    private final Socket clientSocket;
    private final int id;

    public ServerThread(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.id = id;
    }

    // Méthode utilisée dans chaque thread pour gérer la requête d'un client
    public void run() {
        long startServe = System.currentTimeMillis();
        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
            //String client = in.readLine();
            String request = in.readLine();

            if(request.equals("Exit.")) {
                ServerLaunch.writeToFile(ServerLaunch.serviceTimes, "results/service_5_network.txt");
                this.clientSocket.close();
            }

            System.out.println("Time to read : "+(System.currentTimeMillis()-startServe));


            // On process la requête et on affiche la réponse chez le client
            String answer = ServerLaunch.protocol.processInput(request);

            long start = System.currentTimeMillis();
            //System.out.println("time before print the "+answer.length() + " char answer");

            out.println(answer);
            System.out.println("-> "+ ++ServerLaunch.cpt +" : Client # served | time used to print : "+(System.currentTimeMillis() - start));
            long timeToServe = System.currentTimeMillis()-startServe;
            ServerLaunch.avgTime += timeToServe;
            System.out.println("Time to serve : " +timeToServe);

            System.out.println("Mean serve time "+ServerLaunch.avgTime/ServerLaunch.cpt);
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + ServerLaunch.port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
