package org.jetbrains.test;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * Created by Liudmila Kornilova
 * on 04.05.17.
 */
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

        synchronized (threads) { // avoid race condition
            CallTree callTree = threads.get(thread.hashCode()); // get tree of current thread
            if (callTree == null) { // if there is no tree for this thread
                threads.put(thread.hashCode(), new CallTree(thread, methodName, time));
            } else {
                callTree.startMethod(methodName, time);
            }
        }
    }

    static void registerFinish() {
        long time = System.nanoTime(); // get time before synchronized block
        Thread thread = Thread.currentThread();
        String methodName = thread.getStackTrace()[2].getMethodName(); // get function which called this method
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
//        if (threadTreeSet.size() != 3) {
        System.out.println("count threads: " + threadTreeSet.size());
//            return false;
//        }
        for (Map.Entry<Integer, CallTree> threadTree : threads.entrySet()) {
            if (!threadTree.getValue().isCorrect()) {
                return false;
            }
        }
        return true;
    }

    static String generateJson() {
        return JSON.toJSONString(threads);
    }
}

class CallTree {
    private String threadName;
    private LinkedList<Node> calls;
    private long startTreadTime;

    public String getThreadName() {
        return threadName;
    }

    public LinkedList<Node> getCalls() {
        return calls;
    }

    /**
     * Create starting node
     */
    CallTree(Thread thread, String methodName, long startTreadTime) {
        threadName = thread.getName();
        calls = new LinkedList<>();
        this.startTreadTime = startTreadTime;
        calls.addFirst(new Node(methodName, startTreadTime));
    }

    boolean isCorrect() {
        return Objects.equals(calls.getFirst().methodName, "start");
    }

    private class Node {
        private String methodName;
        private LinkedList<Node> childCalls = new LinkedList<>();
        private boolean isFinished = false;
        private long startTime;
        private long finishTime;

        public String getMethodName() {
            return methodName;
        }

        public LinkedList<Node> getChildCalls() {
            return childCalls;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getFinishTime() {
            return finishTime;
        }

        Node(String methodName, long time) {
            this.methodName = methodName;
            this.startTime = time - startTreadTime;
        }

        void addChild(Node newNode) {
            if (isFinished) {
                throw new IndexOutOfBoundsException("you cannot append child to finished node!");
            }
            childCalls.add(newNode);
        }

        Node getLastChild() {
            if (!childCalls.isEmpty()) {
                return childCalls.getLast();
            }
            return null;
        }

        Node getFirstChild() {
            if (!childCalls.isEmpty()) {
                return childCalls.getFirst();
            }
            return null;
        }

        public void print(int depth) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
            System.out.println(methodName);
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
     * append new node to list of childCalls of this not finished node
     */
    void startMethod(String methodName, long time) {
        Node lastUnfinished = getLastUnfinished();
        if (lastUnfinished == null) {
            calls.addLast(new Node(methodName, time));
        } else {
            lastUnfinished.addChild(new Node(methodName, time));
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
        lastUnfinished.finishTime = time - startTreadTime;
    }

    /**
     * Form string recursively
     * add to string current node
     * call this method on all childCalls
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
        for (Node child : current.childCalls) { // call all childCalls
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
