package barber;

import java.util.concurrent.Semaphore;

public class Client extends Thread {
    private final int id;

    public Client(int id) {
        super();
        this.id = id;
    }

    @Override
    public void run() {
        // TODO
        Semaphore clientSemaphore = Main.clientSemaphore;
        Semaphore chairSemaphore = Main.chairSemaphore;
        Semaphore barberReadySemaphore = Main.barberReadySemaphore;

        try {
            chairSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (Main.chairs > 0) {
            // client occupies a seat
            Main.chairs--;

            System.out.println("Client " + id + " is waiting for haircut");
            System.out.println("Available seats: " + Main.chairs);

            // TODO

            clientSemaphore.release();

            System.out.println("Client " + id + " is served by the barber");

            chairSemaphore.release();

            Main.leftClients[id] = Main.SERVED_CLIENT;

            try {
                barberReadySemaphore.acquire();
            }  catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // TODO
            chairSemaphore.release();
            System.out.println("Client " + id + " left unserved");
            Main.leftClients[id] = Main.UNSERVED_CLIENT;
        }
    }
}
