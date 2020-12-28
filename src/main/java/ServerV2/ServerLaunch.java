package ServerV2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerLaunch {

    // Constantes
    static final int port = 4444;
    static final int nbThreads = 5;
    static final int queueSize = 50;
    static final int N = 24422376;
    static final int N_TYPES = 6;

    // Base de données
    public static ArrayList<String>[] DATABASE;

    // Main
    public static void main(String[] args) throws IOException {
        // Démarre le serveur et lit la database
        DATABASE = new ArrayList[N_TYPES];
        for(int i = 0; i < N_TYPES ; i++){
            DATABASE[i] = new ArrayList<>();
        }

        String readLine;
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/database.txt"));
        ExecutorService pool = Executors.newFixedThreadPool(nbThreads);

        System.out.println("-> Server launched");
        System.out.println("-> Computing database...");

        while ((readLine = reader.readLine()) != null) {
            int row = Integer.parseInt(readLine.split("@@@")[0]);
            DATABASE[row].add(readLine.split("@@@")[1]);
        }

        // Ouvre le serveur indéfiniment et traite les requêtes des clients qui arrivent
        try (ServerSocket serverSocket = new ServerSocket(port,queueSize);)
        {
            System.out.println("-> Server ready");
            System.out.println("-> Waiting for client...");
            long start = System.currentTimeMillis();
            int cpt = 0;
            while(true){
                synchronized (serverSocket) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client " + ++cpt + " launched at : " + (System.currentTimeMillis()-start));
                    pool.execute(new ServerThread(clientSocket));
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}