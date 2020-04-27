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

        while (counter.get() < files.size()) {
            try {
                docId = counter.getAndIncrement();
                document = files.get(docId);
                readFile = Files.readAllLines(document);

                for (String line : readFile) {

                    line = processString(line);
                    addStringToMap(line, docId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

            list = invertedIndex.computeIfAbsent(word, k -> Collections.synchronizedList(new ArrayList<>()));

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
