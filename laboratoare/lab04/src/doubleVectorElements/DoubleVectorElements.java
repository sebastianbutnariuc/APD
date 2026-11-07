package doubleVectorElements;

import java.util.Arrays;

public class DoubleVectorElements {
    public static final int N = 100000013;
    public static final int P = 4;

    public static void main(String[] args) {
        int[] v = new int[N];
        MyThread[] threads = new MyThread[P];

        for (int i = 0; i < N; i++) {
            v[i] = i;
        }

        for (int i = 0; i < P; i++) {
            MyThread t = new MyThread(i, v);
            threads[i] = t;
            t.start();
        }

        for (int i = 0; i < P; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (int i = 0; i < N; i++) {
            if (v[i] != i * 2) {
                System.out.println("Wrong answer");
                System.exit(1);
            }
        }
        System.out.println("Correct");
    }
}
