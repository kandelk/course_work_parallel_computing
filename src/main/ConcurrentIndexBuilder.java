package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentIndexBuilder extends Thread{
    private ArrayList<Path> fileList;
    private static ConcurrentHashMap<String, List<Integer>> invertedIndex;
    private static AtomicInteger currDoc = new AtomicInteger(0);

    ConcurrentIndexBuilder(ArrayList<Path> fileList) {
        this.fileList = fileList;
        start();
    }

    @Override
    public void run() {
        List<String> readFile;
        Path document;
        int docId;

        HashMap<String, List<Integer>> localIndex = new HashMap<>();

        while (currDoc.get() < fileList.size()) {
            try {
                docId = currDoc.getAndIncrement();
                document = fileList.get(docId);
                readFile = Files.readAllLines(document);

                for (String line : readFile) {

                    line = processString(line);
                    addStringToLocalIndex(line, docId, localIndex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        putLocalIndexToIndex(localIndex);
    }

    private void putLocalIndexToIndex(HashMap<String, List<Integer>> localIndex) {
        List<Integer> documentList;

        for (Map.Entry<String, List<Integer>> entry : localIndex.entrySet()) {

            documentList = invertedIndex.get(entry.getKey());

            if (documentList == null) {
                documentList = Collections.synchronizedList(new ArrayList<>(entry.getValue()));
            } else {
                documentList.addAll(entry.getValue());
            }

            documentList.sort(Integer::compareTo);
            invertedIndex.put(entry.getKey(), documentList);
        }
    }

    private String processString(String str) {
        String result;

        result = str.replaceAll("(^[\\W_]+)|([\\W_]+$)", "")
                .replaceAll("[\\W_]", " ")
                .replaceAll("\\s+", " ");

        return result.toLowerCase();
    }

    private void addStringToLocalIndex(String str, int docId, HashMap<String, List<Integer>> map) {
        List<Integer> documentList;
        String[] words = str.split(" ");

        for (String word : words) {

            documentList = map.get(word);

            if (documentList == null) {
                documentList = new ArrayList<>();
                map.put(word, documentList);
            }

            if (!documentList.contains(docId)) {

                documentList.add(docId);
            }
        }
    }
    
    public static ConcurrentHashMap<String, List<Integer>> getInvertedIndex() {
        return invertedIndex;
    }

    static void initIndex(int numOfThreads) {
            invertedIndex = new ConcurrentHashMap<>(50,50, numOfThreads);
    }
}
