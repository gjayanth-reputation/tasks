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

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        Map<String,Long> map = new ConcurrentHashMap<>();
        File outputFile = new File("200_threads_1000000_concurrent_jobs.txt");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))){
            for (long i = 0; i < 1000000L; i++) {
                executorService.submit(() -> {
                    String uuid = UUID.randomUUID().toString().replace("-","");
                    try {
                        writer.write("Generated UUID: " + uuid);
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    map.put(uuid,map.getOrDefault(uuid,0L)+1);
                });
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        executorService.shutdown();

        map.entrySet()
                .stream()
                .filter(e->e.getValue()>1L)
                .forEach(System.out::println);

    }
}
