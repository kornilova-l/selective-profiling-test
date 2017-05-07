package org.jetbrains.test;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import java.util.*;

/**
 * Created by Liudmila Kornilova
 * on 04.05.17.
 */

// Todo: add classname to methods

public class CallTreeConstructor {
    private static final HashMap<Integer, CallTree> threads = new HashMap<>(); // map thread's ids to call-trees

    /**
     * Record start of function
     * if thread with this name has not been started
     * - add thread to hashMap
     * search for last finished node
     * - add new node after it
     */
    static void registerStart() {
        long time = System.nanoTime(); // get time before synchronized block
        Thread thread = Thread.currentThread();
        String methodName = thread.getStackTrace()[2].getMethodName(); // get function which called this method
        String className = thread.getStackTrace()[2].getClassName(); // get function which called this method

        synchronized (threads) { // avoid race condition
            CallTree callTree = threads.get(thread.hashCode()); // get tree of current thread
            if (callTree == null) { // if there is no tree for this thread
                threads.put(thread.hashCode(), new CallTree(thread, methodName, className, time)); // todo: separate creation of tree and adding method
            } else {
                callTree.startMethod(methodName, className, time);
            }
        }
    }

    static void registerFinish() {
        long time = System.nanoTime(); // get time before synchronized block
        Thread thread = Thread.currentThread();
        synchronized (threads) { // avoid race condition
            threads.get(thread.hashCode()).finishMethod(time);
        }
    }

    static String generateString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, CallTree> threadTree : threads.entrySet()) {
            stringBuilder.append(threadTree.getValue());
        }
        return stringBuilder.toString();
    }

    static void clear() {
        threads.clear();
    }

    static boolean isCorrect() {
        Set<Map.Entry<Integer, CallTree>> threadTreeSet = threads.entrySet();
        System.out.println("count threads: " + threadTreeSet.size());
        for (Map.Entry<Integer, CallTree> threadTree : threads.entrySet()) {
            if (!threadTree.getValue().isCorrect()) {
                return false;
            }
        }
        return true;
    }

    static String generateJson() {
        return new Gson().toJson(threads);
    }
}

class CallTree {
    private String threadName;
    private LinkedList<Node> calls;
    private long startThreadTime;
    private long duration;

    public String getThreadName() {
        return threadName;
    }

    public LinkedList<Node> getCalls() {
        return calls;
    }

    public long getStartThreadTime() {
        return startThreadTime;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * Create starting node
     */
    CallTree(Thread thread, String methodName, String className, long startTreadTime) {
        threadName = thread.getName();
        calls = new LinkedList<>();
        this.startThreadTime = startTreadTime;
        calls.addFirst(new Node(methodName, className, startTreadTime));
    }

    boolean isCorrect() {
        return Objects.equals(calls.getFirst().methodName, "start");
    }

    private class Node {
        private final String methodName;
        private final String className;
        private final LinkedList<Node> calls = new LinkedList<>();
        private boolean isFinished = false;
        private final long startTime;
        private long duration;

        public String getMethodName() {
            return methodName;
        }

        public LinkedList<Node> getCalls() {
            return calls;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getDuration() {
            return duration;
        }

        public String getClassName() {
            return className;
        }

        Node(String methodName, String className, long time) {
            this.methodName = methodName;
            this.startTime = time - CallTree.this.startThreadTime;
            this.className = className;
        }

        void addChild(Node newNode) {
            calls.add(newNode);
        }

        Node getLastChild() {
            if (!calls.isEmpty()) {
                return calls.getLast();
            }
            return null;
        }

        @Override
        public String toString() {
            return methodName;
        }
    }

    /**
     * Add function to call-tree of current
     * get last not finished node (new method will become a child of this node)
     * (there is always at least one unfinished node because we created starting node for thread)
     * append new node to list of calls of this not finished node
     */
    void startMethod(String methodName, String className, long time) {
        Node lastUnfinished = getLastUnfinished();
        if (lastUnfinished == null) {
            calls.addLast(new Node(methodName, className, time));
        } else {
            lastUnfinished.addChild(new Node(methodName, className, time));
        }
    }

    private Node getLastUnfinished() {
        Node lastUnfinished = calls.getLast();
        if (lastUnfinished.isFinished) {
            return null;
        }
        Node lastChild = lastUnfinished.getLastChild();

        while (lastChild != null && !lastChild.isFinished) {
            lastUnfinished = lastChild;
            lastChild = lastChild.getLastChild();
        }
        return lastUnfinished;
    }

    void finishMethod(long time) {
        Node lastUnfinished = getLastUnfinished();
        if (lastUnfinished == null) {
            return;
        }
        lastUnfinished.isFinished = true;
        lastUnfinished.duration = time - startThreadTime - lastUnfinished.startTime;
        duration = time - startThreadTime; // update thread duration because each method may be last in sequence
    }

    /**
     * Form string recursively
     * add to string current node
     * call this method on all calls
     *
     * @param current     node
     * @param depth       depth of recursion (specifies amount of tabs being added to string)
     * @param finalString string being built
     */
    private void buildStringRecursively(Node current, int depth, StringBuilder finalString) {
        finalString.append("\n");
        for (int i = 0; i < depth; i++) {
            finalString.append("  ");
        }
        finalString.append(current); // add name of method to string
        for (Node child : current.calls) { // call all calls
            buildStringRecursively(child, depth + 1, finalString);
        }
    }

    @Override
    public String toString() {
        StringBuilder finalString = new StringBuilder();
        finalString.append(threadName).append(":\n");
        for (Node node : calls) {
            buildStringRecursively(node, 0, finalString);
        }
        finalString.append("\n");
        return finalString.toString();
    }
}
