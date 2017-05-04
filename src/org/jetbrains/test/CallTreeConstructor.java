package org.jetbrains.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Liudmila Kornilova
 * on 04.05.17.
 */
public class CallTreeConstructor {
    private static HashMap<Integer, CallTree> threads = new HashMap<>(); // map thread's ids to call-trees

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

        CallTree callTree = threads.get(thread.hashCode()); // get tree of current thread

        if (callTree == null) { // if there is no tree for this thread
            threads.put(thread.hashCode(), new CallTree(thread, methodName));
        } else {
            callTree.addNode(methodName);
        }
//        System.out.println("RecordStart. " + Thread.currentThread() + " Method: " +
//                Thread.currentThread().getStackTrace()[2]);
    }

    public static void registerFinish() {
        Thread thread = Thread.currentThread();
        String methodName = thread.getStackTrace()[2].getMethodName(); // get function which called this method
        threads.get(thread.hashCode()).finishMethod(methodName);
    }

    public static void print() {
        for (Map.Entry<Integer, CallTree> threadTree : threads.entrySet()) {
            threadTree.getValue().print();
        }
    }
}

class CallTree {

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
        startNode = new Node("Start of thread \"" + thread.getName() + "\"");
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
}
