package ServerSimple;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerLaunch {

    // Constantes
    static final int port = 4444;
    static final int nbThreads = 8;
    static final int queueSize = 50;
    static final int N = 2442236;

    static List<Long> serviceTimes;
    // Base de données
    public static String[][] DATABASE;

    // Main
    public static void main(String[] args) throws IOException {
        // Démarre le serveur et lit la database
        DATABASE = new String[N][2];

        serviceTimes = new ArrayList<>();

        String readLine;
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/database.txt"));
        ExecutorService pool = Executors.newFixedThreadPool(nbThreads);

        System.out.println("-> Server launched");
        System.out.println("-> Computing database...");
        int cpt = 0;
        while ((readLine = reader.readLine()) != null) {
            DATABASE[cpt++] = readLine.split("@@@");
        }

        // Ouvre le serveur indéfiniment et traite les requêtes des clients qui arrivent
        try (ServerSocket serverSocket = new ServerSocket(port,queueSize);)
        {
            System.out.println("-> Server ready");
            System.out.println("-> Waiting for client...");

            while(true){
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ServerThread(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public static void writeToFile(Collection collection, String fileName) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/"+fileName);
        } catch (IOException e) {
            writer = new FileWriter(fileName);
        }
        StringBuilder str = new StringBuilder();
        for(Object o: collection){
            str.append(o.toString()).append("\n");
        }
        writer.write(str.toString() + "\n");
        writer.close();
    }
}