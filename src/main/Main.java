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
    public static void main(String[] args) {
        ConcurrentIndexBuilder[] indexBuilders;
        ArrayList<Path> files = new ArrayList<>();
        int numOfThread;

        System.out.print("Enter the number of threads: ");
        numOfThread = new Scanner(System.in).nextInt();

        indexBuilders = new ConcurrentIndexBuilder[numOfThread];

        createListOfFiles(files);

        for (int i = 0; i < numOfThread; ++i) {
            indexBuilders[i] = new ConcurrentIndexBuilder(files);
            try {
                indexBuilders[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(ConcurrentIndexBuilder.getInvertedIndex());
    }

    private static void createListOfFiles(Collection<Path> collection) {

        try (Stream<Path> walk = Files.walk(Paths.get("resources"))) {

            walk.filter(Files::isRegularFile).collect(Collectors.toCollection(() -> collection));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}