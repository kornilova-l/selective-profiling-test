package org.jetbrains.test;

import com.google.gson.Gson;

import java.util.*;

/**
 * Created by Liudmila Kornilova
 * on 04.05.17.
 *
 * <p>
 * Class which construct call trees for threads
 * <p>
 * Class has following API:
 * registerStart() - should be put at the beginning of method
 * registerStart() - should be put at the end of method
 * getString() - get indented string
 * getJson() - get json
 */

public class CallTreeConstructor {
    private static final HashMap<Integer, CallTree> threads = new HashMap<>(); // map thread's hashCodes to call-trees

    /**
     * Record start of method
     */
    static void registerStart() {
        long time = System.nanoTime(); // get time before synchronized block
        Thread thread = Thread.currentThread();
        String methodName = thread.getStackTrace()[2].getMethodName(); // get function which called this method
        String className = thread.getStackTrace()[2].getClassName(); // get function which called this method

        synchronized (threads) { // avoid race condition
            // creating of three adds about 4 ms
            // I tried to separate creating call-tree (using computeIfAbsent) and adding first Node, but this added ~50ms
            CallTree callTree = threads.get(thread.hashCode()); // get tree of current thread
            if (callTree == null) { // if there is no tree for this thread
                threads.put(thread.hashCode(), new CallTree(thread, methodName, className, time)); // create call-tree and add first node
            } else {
                callTree.startMethod(methodName, className, time);
            }
        }
    }

    /**
     * Record finish of method
     */
    static void registerFinish() {
        long time = System.nanoTime(); // get time before synchronized block
        Thread thread = Thread.currentThread();
        synchronized (threads) { // avoid race condition
            threads.get(thread.hashCode()).finishMethod(time); // mark method as finished
        }
    }

    /**
     * Get string representation of call-trees for all threads
     * This representation is easily readable
     * but does not contain specific information such as start time
     * @return string representation of call-trees
     */
    static String getString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, CallTree> threadTree : threads.entrySet()) {
            stringBuilder.append(threadTree.getValue());
        }
        return stringBuilder.toString();
    }

    /**
     * Get all data stored in JSON format
     * @return JSON string with all information about call-trees
     */
    static String getJson() {
        return new Gson().toJson(threads);
    }

    /**
     * Class of call-tree
     * (each thread has it's own call-tree)
     */
    static class CallTree {
        private String threadName;
        private LinkedList<Node> nodes = new LinkedList<>(); // methods
        private long startThreadTime;
        private long duration;

        // all getters are needed for exporting to JSON
        public String getThreadName() {
            return threadName;
        }

        public LinkedList<Node> getNodes() {
            return nodes;
        }

        public long getStartThreadTime() {
            return startThreadTime;
        }

        public long getDuration() {
            return duration;
        }

        /**
         * Create call-tree and add first node
         *
         * @param thread current thread
         * @param methodName name of first method in this call-tree
         * @param className class name of first method in this call-tree
         * @param startTreadTime when first method was called
         */
        CallTree(Thread thread, String methodName, String className, long startTreadTime) {
            threadName = thread.getName();
            nodes = new LinkedList<>();
            this.startThreadTime = startTreadTime;
            nodes.addFirst(new Node(methodName, className, startTreadTime));
        }

        /**
         * Node of call-tree
         */
        private class Node {
            private final String methodName;
            private final String className;
            private final LinkedList<Node> nodes = new LinkedList<>(); // child calls
            private boolean isFinished = false;
            private final long startTime;
            private long duration;

            /**
             * Create node
             * @param methodName name of method of this node
             * @param className class name of method
             * @param time when method was called
             */
            Node(String methodName, String className, long time) {
                this.methodName = methodName;
                this.startTime = time - CallTree.this.startThreadTime;
                this.className = className;
            }

            // all getters are needed for exporting JSON
            public String getMethodName() { // all getters are for exporting to JSON
                return methodName;
            }

            public LinkedList<Node> getNodes() { // all getters are for exporting to JSON
                return nodes;
            }

            public long getStartTime() { // all getters are for exporting to JSON
                return startTime;
            }

            public long getDuration() { // all getters are for exporting to JSON
                return duration;
            }

            public String getClassName() { // all getters are for exporting to JSON
                return className;
            }

            /**
             * add child to this node
             * @param child child
             */
            void addChild(Node child) {
                nodes.add(child);
            }

            /**
             * Get last child of node
             * @return last child or null if node does not have children
             */
            Node getLastChild() {
                if (!nodes.isEmpty()) {
                    return nodes.getLast();
                }
                return null;
            }

            @Override
            public String toString() {
                return methodName;
            }
        }

        /**
         * Add function to call-tree
         * the idea is simple:
         * find last unfinished node
         * and add new node to children of that unfinished node
         * @param methodName name of called method
         * @param className class name of called method
         * @param time when method was called
         */
        void startMethod(String methodName, String className, long time) {
            Node lastUnfinished = getLastUnfinished();
            if (lastUnfinished == null) {
                nodes.addLast(new Node(methodName, className, time));
            } else {
                lastUnfinished.addChild(new Node(methodName, className, time));
            }
        }

        /**
         * Find last unfinished node
         * @return last unfinished node or null if all nodes are finished
         */
        private Node getLastUnfinished() {
            Node lastUnfinished = nodes.getLast();
            if (lastUnfinished.isFinished) {
                return null; // all nodes are finished
            }
            Node lastChild = lastUnfinished.getLastChild();

            while (lastChild != null && !lastChild.isFinished) {
                lastUnfinished = lastChild;
                lastChild = lastChild.getLastChild();
            }
            return lastUnfinished;
        }

        /**
         * Mark method as finished
         * @param time when method was finished
         */
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
         *
         * @param current node
         * @param depth depth of recursion (this specifies amount of tabs being added to string)
         * @param finalString string being built
         */
        private void buildStringRecursively(Node current, int depth, StringBuilder finalString) {
            finalString.append("\n");
            for (int i = 0; i < depth; i++) {
                finalString.append("  ");
            }
            finalString.append(current); // add name of method to string
            for (Node child : current.nodes) { // call all nodes
                buildStringRecursively(child, depth + 1, finalString);
            }
        }

        /**
         * Get string representation of call-tree
         * @return string representation of call-tree
         */
        @Override
        public String toString() {
            StringBuilder finalString = new StringBuilder();
            finalString.append(threadName).append(":\n");
            for (Node node : nodes) {
                buildStringRecursively(node, 0, finalString);
            }
            finalString.append("\n");
            return finalString.toString();
        }
    }
}
