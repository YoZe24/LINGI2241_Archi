import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class RegexGenerator {
    // Modify here :
    static final int numberNeeded = 100;

    // Don't touch
    static final String[][] database = new String[2442236][2];
    static final Random rand = new Random();
    static ArrayList<StringBuilder> regexList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("-> Generating files...");
        readDB();

        init();
        generateEasy();

        reset();
        generateHard();

        reset();
        generateNetwork();
        System.out.println("-> Generation finished !");
    }

    static void init(){
        for (int i = 0; i < numberNeeded; i++) {
            regexList.add(new StringBuilder());
        }
    }

    static void reset(){
        for (int i = 0; i < numberNeeded; i++) {
            regexList.set(i, new StringBuilder());
        }
    }

    static void readDB() throws IOException {
        int cpt = 0;
        String readLine = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/database.txt"));
        } catch (FileNotFoundException e) {
            reader = new BufferedReader(new FileReader("database.txt"));
        }
        while ((readLine = reader.readLine()) != null) {
            database[cpt++] = readLine.split("@@@");
        }
    }

    static void generateEasy() throws IOException {
        int[] lines = rand.ints(1, 2442235).distinct().limit(numberNeeded).toArray();

        for (int i = 0; i < numberNeeded; i++) {
            String type = database[lines[i]][0];
            String[] line = database[lines[i]][1].split(" ",10);

            while(String.join(" ",line).contains("*") || String.join(" ",line).contains("(") || String.join(" ",line).contains("[")){
                lines[i]++;
                type = database[lines[i]][0];
                line = database[lines[i]][1].split(" ",10);
            }

            regexList.get(i).append(type).append(";");
            regexList.get(i).append(String.join(" ",line));
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/requests/easy.txt");
        } catch (IOException e) {
            writer = new FileWriter("requests/easy.txt");
        }
        for (int i = 0; i < numberNeeded; i++) {
            writer.write(regexList.get(i).toString() + "\n");
        }
        writer.close();
    }

    static void generateNetwork() throws IOException {
        for (int i = 0; i < numberNeeded; i++) {
            int[] ints = rand.ints(1, 6).distinct().limit(2).toArray();
            regexList.get(i).append(ints[0]).append(",").append(ints[1]).append(";");
            char toSearch = (char)(rand.nextInt(26) + 'a');
            regexList.get(i).append("^").append(toSearch);
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/requests/network.txt");
        } catch (IOException e) {
            writer = new FileWriter("requests/network.txt");
        }
        for (int i = 0; i < numberNeeded; i++) {
            writer.write(regexList.get(i).toString() + "\n");
        }
        writer.close();
    }

    static void generateHard() throws IOException {
        for (int i = 0; i < numberNeeded; i++) {
            int[] ints = rand.ints(1, 6).distinct().limit(2).toArray();
            regexList.get(i).append(ints[0]).append(",").append(ints[1]).append(";").append(".{0,1}");
            for (int j = 0; j < 10; j++) {
                char c = (char)(rand.nextInt(26) + 'a');
                regexList.get(i).append(c);
            }
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/requests/hard.txt");
        } catch (IOException e) {
            writer = new FileWriter("requests/hard.txt");
        }
        for (int i = 0; i < numberNeeded; i++) {
            writer.write(regexList.get(i).toString() + "\n");
        }
        writer.close();
    }
}
