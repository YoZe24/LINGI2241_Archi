package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RegexGenerator {
    /***
     * Number of requests needed
     * @param : int
     */
    static final int numberNeeded = 1000;

    /***
     * Variables needed in the program
     */
    static final String[][] database = new String[2442236][2];
    static final Random rand = new Random();
    static ArrayList<StringBuilder> regexList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("-> Generating files...");
        readDB();

        init();
        generateEasy();

        reset();
        generateMedium();

        reset();
        generateHard();

        reset();
        generateMixed();

        reset();
        generateNetwork();
        System.out.println("-> Generation finished !");
    }

    /***
     * Init function preparing the arraylist for the generation
     */
    static void init(){
        for (int i = 0; i < numberNeeded; i++) {
            regexList.add(new StringBuilder());
        }
    }

    /***
     * Reset function reseting the arraylist after the generations
     */
    static void reset(){
        for (int i = 0; i < numberNeeded; i++) {
            regexList.set(i, new StringBuilder());
        }
    }

    /***
     * Function that reads the database from file and load it into a variable in main memory
     */
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

    /***
     * Function that generates random easy requests by taking random lines in the database file and stripping it to the 10 first words
     * If a line contains some strange characters, takes the line after it
     */
    static void generateEasy() throws IOException {
        int[] lines = rand.ints(1, 2442235).distinct().limit(numberNeeded).toArray();

        for (int i = 0; i < numberNeeded; i++) {
            String type = database[lines[i]][0];
            String[] line = database[lines[i]][1].split(" ",10);

            while(String.join(" ",line).contains("*")
                    || String.join(" ",line).contains("(")
                    || String.join(" ",line).contains("[")
                    || String.join(" ",line).contains(")")
                    || String.join(" ",line).contains("]")){
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

    /***
     * Function that generates medium requests by reading into the file and taking some random lines in it, and then takes the first phrase of it
     * The type is simply set to all
     * @throws IOException
     */
    static void generateMedium() throws IOException {
        int[] lines = rand.ints(1, 2442235).distinct().limit(numberNeeded).toArray();

        for (int i = 0; i < numberNeeded; i++) {
            String[] line = database[lines[i]][1].split("\\.",1);

            while(String.join(" ",line).contains("*")
                    || String.join(" ",line).contains("(")
                    || String.join(" ",line).contains("[")
                    || String.join(" ",line).contains(")")
                    || String.join(" ",line).contains("]")){
                lines[i]++;
                line = database[lines[i]][1].split("\\.",1);
            }

            regexList.get(i).append(";");
            regexList.get(i).append(String.join(" ",line));
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/requests/medium.txt");
        } catch (IOException e) {
            writer = new FileWriter("requests/medium.txt");
        }
        for (int i = 0; i < numberNeeded; i++) {
            writer.write(regexList.get(i).toString() + "\n");
        }
        writer.close();
    }

    /***
     * Function that generates some random requests with high network demand, this is done by taking requests that match with a lot of results
     * The time needed to print the result is expensive due to the big amount of data
     * @throws IOException
     */
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

    /***
     * Function that generates hard request by randomly taking a character and comparing it a lot
     * This makes requests that need more CPU
     * @throws IOException
     */
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

    /***
     * Function that takes randomly some requests from "EASY", "MEDIUM" and "HARD" file
     * This shuffles the difficulty
     * @throws IOException
     */
    static void generateMixed() throws IOException {
        try {
            BufferedReader reader1 = new BufferedReader(new FileReader("src/main/resources/requests/easy.txt"));
            BufferedReader reader2 = new BufferedReader(new FileReader("src/main/resources/requests/medium.txt"));
            BufferedReader reader3 = new BufferedReader(new FileReader("src/main/resources/requests/hard.txt"));
            for(int i = 0 ; i < numberNeeded ; i++){
                if(i % 3 == 0){
                    regexList.get(i).append(reader1.readLine());
                }else if(i % 2 == 0){
                    regexList.get(i).append(reader2.readLine());
                }
                else{
                    regexList.get(i).append(reader3.readLine());
                }
            }
            Collections.shuffle(regexList);
        }catch (IOException e){
            e.printStackTrace();
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter("src/main/resources/requests/mixed.txt");
        } catch (IOException e) {
            writer = new FileWriter("requests/mixed.txt");
        }
        for (int i = 0; i < numberNeeded; i++) {
            writer.write(regexList.get(i).toString() + "\n");
        }
        writer.close();
    }
}
