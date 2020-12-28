package ServerSimple;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocol {
    public String processInput(String clientInput) {
        long start = System.currentTimeMillis();

        // Check si les arguments sont bons (rajouter des cas)
        if(!clientInput.contains(";")){
            return "Asked format : <types>;<regex>\n";
        }

        // Récupère les arguments
        String[] input = clientInput.split(";");

        String[] types;
        if(input[0].equals("")){
            types = new String[0];
        }
        else{
            types = input[0].split(",");
        }

        String regex = input[1];
        Pattern p = Pattern.compile(regex);
        Matcher m;

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < ServerLaunch.DATABASE.length; i++) {
            String currType = ServerLaunch.DATABASE[i][0];
            String currRegex = ServerLaunch.DATABASE[i][1];

            m = p.matcher(currRegex);
            if(types.length == 0){
                if(m.find()){
                    str.append(currRegex);
                }
            }
            else{
                for (String type : types) {
                    if (currType.equals(type) && m.find()) {
                        str.append(currRegex);
                    }
                }
            }
        }
        System.out.println("computing time : " + (System.currentTimeMillis() - start));
        synchronized (ServerLaunch.serviceTimes) {
            ServerLaunch.serviceTimes.add(System.currentTimeMillis() - start);
        }
        str.append("\n");
        return str.toString();
    }
}
