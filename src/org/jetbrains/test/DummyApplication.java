package org.jetbrains.test;

import java.util.List;
import java.util.Random;
import static org.jetbrains.test.CallTreeConstructor.registerFinish;
import static org.jetbrains.test.CallTreeConstructor.registerStart;
/**
 * Nikolay.Tropin
 * 18-Apr-17
 */
public class DummyApplication {
    private final List<String> args;
    private Random random = new Random(System.nanoTime());

    public DummyApplication(List<String> args) {
        this.args = args;
    }

    private boolean nextBoolean() {
        return random.nextBoolean();
    }

    private boolean stop() {
        return random.nextDouble() < 0.05;
    }

    private String nextArg() {
        int idx = random.nextInt(args.size());
        return args.get(idx);
    }

    private void sleep() {
        try {
            Thread.sleep(random.nextInt(20));
        } catch (InterruptedException ignored) {

        }
    }

    private void abc(String s) {
        registerStart();

        sleep();
        if (stop()) {
            //do nothing
        }
        else if (nextBoolean()) {
            def(nextArg());
        }
        else {
            xyz(nextArg());
        }
        registerFinish();
    }

    private void def(String s) {
        registerStart();

        sleep();
        if (stop()) {
            //do nothing
        }
        else if (nextBoolean()) {
            abc(nextArg());
        }
        else {
            xyz(nextArg());
        }
        registerFinish();
    }

    private void xyz(String s) {
        registerStart();

        sleep();
        if (stop()) {
            //do nothing
        }
        else if (nextBoolean()) {
            abc(nextArg());
        }
        else {
            def(nextArg());
        }
        registerFinish();
    }

    public void start() {
        abc(nextArg());
    }
}

