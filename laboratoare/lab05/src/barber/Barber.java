package barber;

import java.util.concurrent.Semaphore;

public class Barber extends Thread {
    @Override
    public void run() {
        int servedClients = 0;

        do {
            // TODO
            Semaphore clientSemaphore = Main.clientSemaphore;
            Semaphore chairSemaphore = Main.chairSemaphore;
            Semaphore barberReadySemaphore = Main.barberReadySemaphore;

            try {
                clientSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Main.chairs++;

            // TODO

            try {
                chairSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Barber served client");
            ++servedClients;


            barberReadySemaphore.release();
            chairSemaphore.release();

        } while (checkIfThereAnyClients());
    }

    private boolean checkIfThereAnyClients() {
        int count = 0;
        for (var client: Main.leftClients) {
            if (client == 0) {
                count++;
            }
        }

        return count != 0;
    }
}
