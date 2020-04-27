package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentIndexBuilder[] threads;
        ArrayList<Path> files = new ArrayList<>();
        int numOfThread;

        System.out.print("Enter the number of threads: ");
        numOfThread = new Scanner(System.in).nextInt();

        threads = new ConcurrentIndexBuilder[numOfThread];
        ConcurrentIndexBuilder.initIndex(numOfThread);

        createListOfFiles(files);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numOfThread; i++) {
            threads[i] = new ConcurrentIndexBuilder(files);
        }
        for (int i = 0; i < numOfThread; i++) {
            threads[i].join();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;

        System.out.println(totalTime);
    }

    private static void createListOfFiles(Collection<Path> collection) {

        try (Stream<Path> walk = Files.walk(Paths.get("resources"))) {

            walk.filter(Files::isRegularFile).collect(Collectors.toCollection(() -> collection));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}