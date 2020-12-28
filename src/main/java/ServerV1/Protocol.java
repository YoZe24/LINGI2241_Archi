package ServerV1;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocol {
    public ArrayList<String> processInput(String clientInput) {
        ArrayList<String> answer = new ArrayList<>();

        // Check si les arguments sont bons (rajouter des cas)
        if(!clientInput.contains(";")){
            answer.add("Asked format : <types>;<regex>");
            answer.add("\n");
            return answer;
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

        for (int i = 0; i < ServerLaunch.DATABASE.length; i++) {
            String currType = ServerLaunch.DATABASE[i][0];
            String currRegex = ServerLaunch.DATABASE[i][1];

            if(types.length == 0){
                m = p.matcher(currRegex);
                if(m.find()){
                    answer.add(currRegex);
                }
            }
            else{
                for (String type : types) {
                    m = p.matcher(currRegex);
                    if (currType.equals(type) && m.find()) {
                        answer.add(currRegex);
                    }
                }
            }
        }
        answer.add("\n");
        return answer;
    }
}
