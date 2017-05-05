package org.jetbrains.test;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        CallTreeConstructor.clear();
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 5; i++) {
            int start = 100 * i;
            List<String> arguments = IntStream.range(start, start + 10)
                    .mapToObj(Integer::toString)
                    .collect(Collectors.toList());

            service.submit(() -> new DummyApplication(arguments).start());
        }
        service.shutdown();

        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ignored) {
        }
        System.out.println(CallTreeConstructor.generateString());
        if (CallTreeConstructor.isCorrect()) {
            System.out.println("correct");
        } else {
            System.out.println("WRONG!");
        }
        try {
            PrintWriter writer = new PrintWriter("output.txt");
            writer.print(CallTreeConstructor.generateJson());
            writer.close();
        } catch (FileNotFoundException ignored) {
        }
    }
}
