package shortestPathsFloyd_Warshall;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int M = 9;
        int[][] graph = {{0, 1, M, M, M},
                {1, 0, 1, M, M},
                {M, 1, 0, 1, 1},
                {M, M, 1, 0, M},
                {M, M, 1, M, 0}};

        int N = graph.length;
        int P = 4; // works for any P >= 1

        // Keep a copy for the sequential reference
        int[][] expected = new int[N][];
        for (int i = 0; i < N; i++) {
            expected[i] = graph[i].clone();
        }
        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    expected[i][j] = Math.min(expected[i][k] + expected[k][j], expected[i][j]);
                }
            }
        }

        // Parallel version
        CyclicBarrier barrier = new CyclicBarrier(P);
        Thread[] threads = new Thread[P];
        for (int id = 0; id < P; id++) {
            threads[id] = new Thread(new FloydTask(id, P, graph, barrier));
            threads[id].start();
        }
        for (int id = 0; id < P; id++) {
            threads[id].join();
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(graph[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println(Arrays.deepEquals(graph, expected) ? "Correct" : "Wrong answer");
    }
}

class FloydTask implements Runnable {
    private final int id, P;
    private final int[][] graph;
    private final CyclicBarrier barrier;

    FloydTask(int id, int P, int[][] graph, CyclicBarrier barrier) {
        this.id = id;
        this.P = P;
        this.graph = graph;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        int N = graph.length;
        // This thread owns rows [start, end)
        int start = (int) ((long) id * N / P);
        int end = (int) Math.min(N, (long) (id + 1) * N / P);

        for (int k = 0; k < N; k++) {
            for (int i = start; i < end; i++) {
                for (int j = 0; j < N; j++) {
                    graph[i][j] = Math.min(graph[i][k] + graph[k][j], graph[i][j]);
                }
            }
            try {
                barrier.await(); // wait for every thread to finish step k
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }
}