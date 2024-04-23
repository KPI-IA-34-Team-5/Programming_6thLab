import java.util.concurrent.TimeUnit;

public class ArithmeticProgressionSum {

    public static void main(String[] args) throws InterruptedException {
        int n = 5; // номер варіанту
        int N = 100_000_000; // верхня межа суми
        long startTime, endTime, elapsedTime;

        // Метод 1: Формула суми арифметичної прогресії
        startTime = System.nanoTime();
        long sumByFormula = sumByFormula(n, N);
        endTime = System.nanoTime();
        elapsedTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Сума за формулою: " + sumByFormula + ", час: " + elapsedTime + " мс");

        // Метод 2: Простий цикл
        startTime = System.nanoTime();
        long sumByLoop = sumByLoop(n, N);
        endTime = System.nanoTime();
        elapsedTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Сума за циклом: " + sumByLoop + ", час: " + elapsedTime + " мс");

        // Метод 3-7: Цикл у декількох тредах
        int[] threadCounts = {2, 4, 8, 16, 32};
        for (int k : threadCounts) {
            startTime = System.nanoTime();
            long sumByMultiThread = sumByMultiThread(n, N, k);
            endTime = System.nanoTime();
            elapsedTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            System.out.println("Сума за " + k + " тредів: " + sumByMultiThread + ", час: " + elapsedTime + " мс");
        }
    }

    public static long sumByFormula(int n, int N) {
        return (long) n * (N + 1) * N / 2;
    }

    public static long sumByLoop(int n, int N) {
        long sum = 0;
        for (int i = 1; i <= N; i++) {
            sum += (long) i * n;
        }
        return sum;
    }

    public static long sumByMultiThread(int n, int N, int threadCount) throws InterruptedException {
        final long[] sum = {0};
        Thread[] threads = new Thread[threadCount];
        int chunkSize = N / threadCount; // Розмір кожної частини для обробки тредом

        for (int i = 0; i < threadCount; i++) {
            final int start = i * chunkSize + 1;
            final int end = (i == threadCount - 1) ? N : (i + 1) * chunkSize; // Обробка решти чисел
            MyRunnable myRunnable = new MyRunnable(start, end, n, sum);
            threads[i] = new Thread(myRunnable);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return sum[0];
    }

    static class MyRunnable implements Runnable {
        private final int start;
        private final int end;
        private final int n;
        private final long[] sum;

        public MyRunnable(int start, int end, int n, long[] sum) {
            this.start = start;
            this.end = end;
            this.n = n;
            this.sum = sum;
        }

        @Override
        public void run() {
            long partialSum = 0;
            for (int j = start; j <= end; j++) {
                partialSum += (long) j * n;
            }
            synchronized (sum) {
                sum[0] += partialSum;
            }
        }
    }
}