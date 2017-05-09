package org.jetbrains.test;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);
        for(int i = 0; i < 5; i++) {
            int start = 100 * i;
            List<String> arguments = IntStream.range(start, start + 10)
                    .mapToObj(Integer :: toString)
                    .collect(Collectors.toList());
            service.submit(() -> new DummyApplication(arguments).start());
        }
        service.shutdown();
        service.awaitTermination(60, TimeUnit.SECONDS);
        System.out.println(CallTreeConstructor.getString()); // print to console
        // Get JSON
        PrintWriter writer = new PrintWriter("JSON/DummyApplication3.json");
        writer.print(CallTreeConstructor.getJson());
        writer.close();
    }
}
