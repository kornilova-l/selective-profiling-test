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
 * it has only one thread and determined call tree with following structure:
 * start
 *  fun1
 *      fun3
 *          fun4
 *          fun4
 *          fun5
 *              fun6
 *  fun2
 */

public class TestApplication {
    private void fun1() throws InterruptedException {
        registerStart("arg1");
        Thread.sleep(3);
        fun3();
        Thread.sleep(5);
        registerFinish();
    }

    private void fun2() throws InterruptedException {
        registerStart("arg2");
        Thread.sleep(10);
        registerFinish();
    }

    private void fun3() throws InterruptedException {
        registerStart("arg3");
        Thread.sleep(3);
        fun4();
        Thread.sleep(3);
        fun4();
        fun5();
        registerFinish();
    }

    private void fun4() throws InterruptedException {
        registerStart("arg4");
        Thread.sleep(20);
        registerFinish();
    }

    private void fun5() throws InterruptedException {
        registerStart("arg5");
        Thread.sleep(40);
        fun6();
        Thread.sleep(10);
        registerFinish();
    }

    private void fun6() throws InterruptedException {
        registerStart("arg6");
        Thread.sleep(15);
        registerFinish();
    }

    public void start() throws InterruptedException {
        registerStart("");
        Thread.sleep(20);
        fun1();
        Thread.sleep(30);
        fun2();
        Thread.sleep(5);
        registerFinish();
    }

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        TestApplication ta = new TestApplication();
        ta.start();
        System.out.println(CallTreeConstructor.getString()); // print to console
        // export to JSON
        PrintWriter writer = new PrintWriter("JSON/TestApplication.json");
        writer.print(CallTreeConstructor.getJson());
        writer.close();
    }
}
