package org.jetbrains.test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.jetbrains.test.CallTreeConstructor.registerFinish;
import static org.jetbrains.test.CallTreeConstructor.registerStart;

/**
 * Created by Liudmila Kornilova
 * on 04.05.17.
 *
 * Application for testing CallTreeConstructor
 * It does not have sleep() calls
 * it has only one thread and determined call tree with following structure:
 * start
 *  fun1
 *      fun3
 *          fun4
 *          fun4
 *          fun5
 *              fun6
 *                  fun7
 *                      fun8
 *  fun2
 */

public class TestApplicationWithoutSleep {
    private void fun1() {
        registerStart();
        fun3();
        registerFinish();
    }

    private void fun2() {
        registerStart();
        registerFinish();
    }

    private void fun3() {
        registerStart();
        fun4();
        fun4();
        fun5();
        registerFinish();
    }

    private void fun4() {
        registerStart();
        registerFinish();
    }

    private void fun5() {
        registerStart();
        fun6();
        registerFinish();
    }

    private void fun6() {
        registerStart();
        fun7();
        registerFinish();
    }

    private void fun7() {
        registerStart();
        fun8();
        registerFinish();
    }

    private void fun8() {
        registerStart();
        registerFinish();
    }

    public void start() {
        registerStart();
        fun1();
        fun2();
        registerFinish();
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        TestApplicationWithoutSleep ta = new TestApplicationWithoutSleep();
        ta.start();
        System.out.println(CallTreeConstructor.getString()); // print to console
        // export to JSON
        PrintWriter writer = new PrintWriter("JSON/TestApplicationWithoutSleep.json");
        writer.print(CallTreeConstructor.getJson());
        writer.close();
    }
}
