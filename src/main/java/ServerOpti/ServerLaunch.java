package ServerOpti;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerLaunch {
    static final int port = 4444;
    static final int nbThreads = 2;
    static final int queueSize = 50;
    static final int N_TYPES = 6;

    static long cpt = 0;
    static List<Long> serviceTimes;
    static Protocol protocol;

    static ArrayList<String>[] DATABASE;

    public static void main(String[] args) throws IOException {
        DATABASE = new ArrayList[N_TYPES];
        for(int i = 0; i < N_TYPES ; i++){
            DATABASE[i] = new ArrayList<>();
        }

        serviceTimes = new ArrayList<>();
        protocol = new Protocol();

        String readLine;
        BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/database.txt"));

        ExecutorService pool = Executors.newFixedThreadPool(nbThreads);

        System.out.println("-> Server launched");
        System.out.println("-> Computing database...");

        while ((readLine = reader.readLine()) != null) {
            int row = Integer.parseInt(readLine.split("@@@")[0]);
            DATABASE[row].add(readLine.split("@@@")[1]);
        }

        try (ServerSocket serverSocket = new ServerSocket(port,queueSize);)
        {
            System.out.println("-> Server ready");
            System.out.println("-> Waiting for client...");
            int cpt = 0;
            while(true){
                synchronized (serverSocket) {
                    Socket clientSocket = serverSocket.accept();
                    pool.execute(new ServerThread(clientSocket));
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port + " or listening for a connection");
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