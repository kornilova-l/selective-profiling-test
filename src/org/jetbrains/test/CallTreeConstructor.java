package org.jetbrains.test;

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
    public static void registerStart() {
        Thread thread = Thread.currentThread();
        String methodName = thread.getStackTrace()[2].getMethodName(); // get function which called this method

        synchronized (threads) {
            CallTree callTree = threads.get(thread.hashCode()); // get tree of current thread

            if (callTree == null) { // if there is no tree for this thread
            /*  there may be problems if two threads will put element to static HashMap
                so HashMap must be synchronized
             */
                    threads.put(thread.hashCode(), new CallTree(thread, methodName));
            } else {
                callTree.addNode(methodName);
            }
//        System.out.println("RecordStart. " + Thread.currentThread() + " Method: " +
//                Thread.currentThread().getStackTrace()[2]);
        }
    }

    public static void registerFinish() {
        Thread thread = Thread.currentThread();
        String methodName = thread.getStackTrace()[2].getMethodName(); // get function which called this method
        synchronized (threads) {
            threads.get(thread.hashCode()).finishMethod(methodName);
        }
    }

    public static void print() {
        for (Map.Entry<Integer, CallTree> threadTree : threads.entrySet()) {
            threadTree.getValue().print();
        }
    }

    public static void printToFile(String fileName) {
        for (Map.Entry<Integer, CallTree> threadTree : threads.entrySet()) {
            System.out.println(threadTree.getValue());
        }
    }

    public static void clear() {
        threads.clear();
    }

    public static boolean isCorrect() {
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
}

class CallTree {

    boolean isCorrect() {
        return Objects.equals(startNode.children.getFirst().methodName, "start");
    }

    private static class Node {
        private String methodName;
        private LinkedList<Node> children = new LinkedList<>();
        private boolean isFinished = false;

        Node(String methodName) {
            this.methodName = methodName;
        }

        void addChild(Node newNode) {
            if (isFinished) {
                throw new IndexOutOfBoundsException("you cannot append child to finished node!");
            }
            children.add(newNode);
        }

        Node getLastChild() {
            if (!children.isEmpty()) {
                return children.getLast();
            }
            return null;
        }

        Node getFirstChild() {
            if (!children.isEmpty()) {
                return children.getFirst();
            }
            return null;
        }

        public void print(int depth) {
            for (int i = 0; i < depth; i++) {
                System.out.print("  ");
            }
            System.out.println(methodName);
        }
    }
    private Node startNode;
    /**
     * Create starting node
     */
    CallTree(Thread thread, String methodName) {
        startNode = new Node("\"" + thread.getName() + "\"");
        startNode.children.addFirst(new Node(methodName));
    }

    /**
     * Add function to call-tree of current
     * get last not finished node (new method will become a child of this node)
     * (there is always at least one unfinished node because we created starting node for thread)
     * append new node to list of children of this not finished node
     */
    public void addNode(String methodName) {
        Node lastUnfinished = getLastUnfinished();
        lastUnfinished.addChild(new Node(methodName));
    }

    private Node getLastUnfinished() {
        Node lastUnfinished = startNode;
        Node lastChild = lastUnfinished.getLastChild();

        while (lastChild != null && !lastChild.isFinished) {
            lastUnfinished = lastChild;
            lastChild = lastChild.getLastChild();
        }
        return lastUnfinished;
    }

    public void finishMethod(String methodName) {
        getLastUnfinished().isFinished = true;
    }

    public void print() {
        System.out.println("Thread: " + startNode.methodName);
        printRecursively(startNode, startNode.children.getFirst(), 1);
    }

    private void printRecursively(Node parent, Node current, int depth) {
        if (current == null) {
            return;
        }
        current.print(depth);
        printRecursively(current, current.getFirstChild(), depth + 1);
        int nextNodeIndex = parent.children.indexOf(current) + 1;
        try {
            Node nextNode = parent.children.get(nextNodeIndex);
            if (nextNode != null) {
                printRecursively(parent, nextNode, depth);
            }
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public String toString() {
        return startNode.methodName + " hello";
    }
}
