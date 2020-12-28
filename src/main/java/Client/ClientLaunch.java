package Client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientLaunch {

    // Constantes
    static ArrayList<String> requestsDB;
//    static String host = "192.168.1.52";
//    static String host = "149.126.75.6";

    static String host = "192.168.0.12";
    static final int port = 4444;
    static final int nbClients = 10;
    static final Random rand = new Random();
    static long start;
    static int cpt = 0;
    static AtomicInteger CPT = new AtomicInteger(0);
    static float avg = 0;
    static List<Long> times;

    static final float REQUEST_PACE = (float) 2; // Une requête toutes les X secondes
    static final float LAMBDA = (float) 1/(REQUEST_PACE * 1000);
    static final int NB_REQUEST_PER_SEQ = 1;
    static final int NB_REQUEST = NB_REQUEST_PER_SEQ * 5;

    static final String easy = "easy.txt";
    static final String hard = "hard.txt";
    static final String network = "network.txt";
    static final String medium = "medium.txt";

    // Main
    public static void main(String[] args) throws InterruptedException, IOException {
        generateRequests(medium);
        System.out.println("Lambda : "+LAMBDA + " NB request per batch "+NB_REQUEST_PER_SEQ+ " PACE "+REQUEST_PACE);

        times = new ArrayList<>();

        Thread[] threads = new Thread[nbClients + 1];

        start = System.currentTimeMillis();

        for (int i = 1; i <= nbClients; i++) {
            //double sleep = exponential(LAMBDA);
            //Thread.sleep((long) sleep);
            threads[i] = new Thread(new ClientThread(i + "", NB_REQUEST));
        }

        for (int i = 1; i <= nbClients; i++)
            threads[i].start();
        for (int i = 1; i <= nbClients; i++)
            threads[i].join();


        System.out.println("The " + ClientLaunch.nbClients + " finished their requests in " + (System.currentTimeMillis() - ClientLaunch.start) / 1000. + " seconds");
        System.out.println("Mean waiting time = " + ClientLaunch.avg / ClientLaunch.nbClients);

        ClientLaunch.writeToFile();
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

    public static void writeToFile() throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/results/response_time.txt");
        } catch (IOException e) {
            writer = new FileWriter("results/response_time.txt");
        }
        StringBuilder str = new StringBuilder();
        for (Long time : times) {
            str.append(time + "\n");
        }
        writer.write(str.toString() + "\n");
        writer.close();
    }

    public static double exponential(float lambda) {
        return Math.log(1 - rand.nextDouble())/(- lambda);
    }
}