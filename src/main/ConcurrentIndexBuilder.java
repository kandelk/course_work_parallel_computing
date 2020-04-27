package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentIndexBuilder extends Thread{
    private ArrayList<Path> files;
    private static ConcurrentHashMap<String, List<Integer>> invertedIndex;
    private static AtomicInteger counter = new AtomicInteger(0);

    public ConcurrentIndexBuilder(ArrayList<Path> files) {
        this.files = files;
        start();
    }

    @Override
    public void run() {
        List<String> readFile;
        Path document;
        int docId;

        HashMap<String, List<Integer>> map = new HashMap<>();

        while (counter.get() < files.size()) {
            try {
                docId = counter.getAndIncrement();
                document = files.get(docId);
                readFile = Files.readAllLines(document);

                for (String line : readFile) {

                    line = processString(line);
                    addStringToMap(line, docId, map);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        putMapToIndex(map);
    }

    private void putMapToIndex(HashMap<String, List<Integer>> map) {
        List<Integer> list;

        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {

            list = invertedIndex.get(entry.getKey());

            if (list == null) {
                list = Collections.synchronizedList(new ArrayList<>(entry.getValue()));
            } else {
                list.addAll(entry.getValue());
            }

            list.sort(Integer::compareTo);
            invertedIndex.put(entry.getKey(), list);
        }
    }

    private String processString(String str) {
        String result;

        result = str.replaceAll("(^[\\W_]+)|([\\W_]+$)", "")
                .replaceAll("[\\W_]", " ")
                .replaceAll("\\s+", " ");

        return result.toLowerCase();
    }

    private void addStringToMap(String str, int docId, HashMap<String, List<Integer>> map) {
        List<Integer> list;
        String[] words = str.split(" ");

        for (String word : words) {

            list = map.get(word);

            if (list == null) {
                list = new ArrayList<>();
                map.put(word, list);
            }

            if (!list.contains(docId)) {

                list.add(docId);
            }
        }
    }
    
    public static ConcurrentHashMap<String, List<Integer>> getInvertedIndex() {
        return invertedIndex;
    }

    public static void initIndex(int numOfThreads) {
            invertedIndex = new ConcurrentHashMap<>(50,50, numOfThreads);
    }
}
