package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentIndexBuilder extends Thread{
    private ArrayList<Path> files;
    private static ConcurrentHashMap<String, List<Integer>> invertedIndex = new ConcurrentHashMap<>(50,50,2);
    private static AtomicInteger counter = new AtomicInteger(0);

    public ConcurrentIndexBuilder(ConcurrentLinkedQueue<Path> filenames) {
        this.filenames = filenames;
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

        result = str.replaceAll("[\\W_]", " ");
        result = result.replaceAll("\\s+", " ");
        result = result.toLowerCase();

        return result;
    }

    public static Map<String, ArrayList<String>> getIndex() {
        return index;
    }
}
