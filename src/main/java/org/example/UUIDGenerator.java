package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UUIDGenerator {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        Map<String,Long> map = new ConcurrentHashMap<>();
        File outputFile = new File("200_threads_100000_concurrent_jobs.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (long i = 0; i < 100000L; i++) {
                executorService.submit(() -> {
                    String uuid = UUID.randomUUID().toString();
                    synchronized (writer) {
                        try {
                            writer.write("Generated UUID: " + uuid);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    map.put(uuid,map.getOrDefault(uuid,0L)+1);
                });
            }

            executorService.shutdown();

            while (!executorService.isTerminated()) {

            }

            map.entrySet()
                    .stream()
                    .filter(e->e.getValue()>1L)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
