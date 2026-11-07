package helloWorld;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < numberOfCores; i++) {
            threads.add(new Thread(new Hello()));
            threads.get(i).start();
        }

        for (int i = 0; i < 2; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
