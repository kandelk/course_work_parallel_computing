package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {

        ConcurrentLinkedQueue<Path> queue = new ConcurrentLinkedQueue<>();

        System.out.print("Enter the number of threads: ");
        int N = new Scanner(System.in).nextInt();

        createListOfFilesInFolder(queue);
    }

    private static void createListOfFilesInFolder(Collection<Path> collection) {

        try (Stream<Path> walk = Files.walk(Paths.get("resources"))) {

            walk.filter(Files::isRegularFile).collect(Collectors.toCollection(() -> collection));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}