package ServerOpti;

import utils.CacheLFU;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocol {
    private static final int N_TYPES = 6;
    private CacheLFU cache;

    public Protocol(){
        cache = new CacheLFU(100,5);
    }

    public String processInput(String clientInput) {
        long start = System.currentTimeMillis();

        if(!clientInput.contains(";")){
            return ("Asked format : <types>;<regex>\n");
        }

        String[] input = clientInput.split(";");

        String cacheAnswer = cache.get(clientInput);
        if(cacheAnswer != null) {
            ServerLaunch.serviceTimes.add(System.currentTimeMillis()-start);
            return cacheAnswer;
        }

        Set<String> setTypes = new TreeSet<String>();
        if(!input[0].equals("")){
            setTypes.addAll(Arrays.asList(input[0].split(",")));
        }

        Pattern p = Pattern.compile(input[1]);
        Matcher m;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < N_TYPES; i++) {
            if(setTypes.size() == 0 || setTypes.contains(String.valueOf(i))){
                for(String sentence : ServerLaunch.DATABASE[i]){
                    m = p.matcher(sentence);
                    if(m.find()) builder.append(sentence);
                }
            }
        }
        builder.append("\n");
        String answer = builder.toString();
        cache.add(clientInput,answer);

        synchronized (ServerLaunch.serviceTimes) {
            ServerLaunch.serviceTimes.add(System.currentTimeMillis() - start);
        }
        return answer;
    }
}
