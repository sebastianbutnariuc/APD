package helloWorld;

public class Hello extends Thread {
    public void run() {
        System.out.println("Hello World from thread " + Thread.currentThread().threadId());
    }
}
