package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientLaunch {

    // Constantes
    static ArrayList<String> requestsDB;
    //static String host = "149.126.75.6";

    //static String host = "192.168.0.12";
    static String host = "192.168.0.12";
    static final int port = 4444;
    static final int nbClients = 50;
    static final Random rand = new Random();
    static long start;
    static int cpt = 0;
    static AtomicInteger CPT = new AtomicInteger(0);
    static float avg = 0;
    static List<Long> times;

    static final float REQUEST_PACE = (float) 2; // 1 requête toutes les X secondes
    static final float LAMBDA = (float) 1/(REQUEST_PACE * 1000);
    static final int NB_REQUEST_PER_SEQ = 1;
    static final int NB_REQUEST = NB_REQUEST_PER_SEQ * 5;

    static final String easy = "easy.txt";
    static final String hard = "hard.txt";
    static final String medium = "medium.txt";
    static final String mixed = "mixed";
    static final String network = "network.txt";

    static final String difficulty = easy;

    // Main
    public static void main(String[] args) throws InterruptedException, IOException {
        generateRequests(difficulty);

        times = new ArrayList<>();

        Thread[] threads = new Thread[nbClients + 1];

        start = System.currentTimeMillis();

        for (int i = 1; i <= nbClients; i++) {
            threads[i] = new Thread(new ClientThread(i + "", NB_REQUEST));
        }

        for (int i = 1; i <= nbClients; i++)
            threads[i].start();
        for (int i = 1; i <= nbClients; i++)
            threads[i].join();

        System.out.println("Mean waiting time = " + avg / nbClients);

        finish();

        writeToFile(times, "response_easy_simple" + difficulty);
    }

    public static void generateRequests(String difficulty) throws IOException {
        requestsDB = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/requests/" + difficulty));
        } catch (FileNotFoundException e) {
            reader = new BufferedReader(new FileReader("requests/" + difficulty));
        }
        String readLine;
        while ((readLine = reader.readLine()) != null) {
            requestsDB.add(readLine);
        }
    }

    public static void writeToFile(Collection collection,String fileName) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/results/" + fileName);
        } catch (IOException e) {
            writer = new FileWriter("results/" + fileName);
        }

        StringBuilder str = new StringBuilder();
        for(Object o: collection){
            str.append(o.toString()).append("\n");
        }
        writer.write(str.toString() + "\n");
        writer.close();
    }

    public static double exponential(float lambda) {
        return Math.log(1 - rand.nextDouble())/(- lambda);
    }

    public static void finish(){
        boolean send = false;
        do{
            try (Socket socket = new Socket(ClientLaunch.host, ClientLaunch.port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            )
            {
                out.println("Exit.");
                send = true;
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + ClientLaunch.host);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + ClientLaunch.host);
            }
        }while(!send);

    }
}