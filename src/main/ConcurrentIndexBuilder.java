package main;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentIndexBuilder extends Thread{
    private ConcurrentLinkedQueue<Path> filenames;
    private static Map<String, ArrayList<String>> index = new HashMap<>(50, 50);

    public ConcurrentIndexBuilder(ConcurrentLinkedQueue<Path> filenames) {
        this.filenames = filenames;
        start();
    }

    @Override
    public void run() {

    }

    public String processString(String str) {
        String result;

        result = str.replaceAll("\\W_", " ");
        result = result.replaceAll("\\s+", " ");

        return result;
    }

    public static Map<String, ArrayList<String>> getIndex() {
        return index;
    }
}
