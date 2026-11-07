package doubleVectorElements;

public class MyThread extends Thread {
    private int tid;
    private int[] v;

    public MyThread(int tid, int[] v) {
        this.tid = tid;
        this.v = v;
    }

    @Override
    public void run() {
        int N = DoubleVectorElements.N;
        int P = DoubleVectorElements.P;

        int start = (int) ((long) tid * N / P);
        int end = (int) Math.min(N, (long) (tid + 1) * N / P);

        for (int i = start; i < end; i++) {
            v[i] *= 2;
        }
    }
}
