package utils;

import java.util.HashMap;
import java.util.Map;

public class CacheLFU {
    private final int N;
    private final float THETA;
    private final Map<String, Entry> map;

    public CacheLFU(int N, float THETA) {
        this.N = N;
        this.THETA = THETA;
        this.map = new HashMap<>();
    }

    /**
     * Add an entry to the cache. If cache is full remove the entry
     * with the lowest frequency according to the LFU algorithm.
     * Also divide by 2 each frequency if THETA is crossed to avoid
     * an entry from stagnating.
     * Synchronized to avoid concurrency problem
     * @param request
     * @param response
     */
    public synchronized void add(String request, String response) {
        if (map.size() >= this.N) {
            String keyToRemove = null;
            int minFreq = Integer.MAX_VALUE;
            int cptFreq = 0;

            for(String key : map.keySet()){
                cptFreq += map.get(key).freq;
                if(map.get(key).freq < minFreq)
                    keyToRemove = key;
            }

            if (cptFreq/(float) map.size() > this.THETA) {
                for (Entry entry : map.values())
                    entry.freq /= 2;
            }
            map.remove(keyToRemove);
        }
        map.put(request, new Entry(response, 1));
    }


    /**
     * If object is in the cache return it and increase its frequency
     * otherwise return null
     * @param request
     * @return
     */
    public synchronized String get(String request) {
        if (map.containsKey(request)) {
            Entry entry = map.get(request);
            entry.freq += 1;
            return entry.answer;
        }
        return null;
    }

    /**
     * Inner class which represent an entry with is frequency
     */
    public static class Entry {
        public String answer;
        public int freq;

        public Entry(String response, int freq) {
            this.answer = response;
            this.freq = freq;
        }
    }
}