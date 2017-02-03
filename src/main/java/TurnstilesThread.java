import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Niki on 2017-02-03.
 *
 * @author Niki
 */
public class TurnstilesThread extends Thread {

    private ArrayList<Turnstile> turnstiles;

    public TurnstilesThread(ArrayList<Turnstile> turnstiles) {
        this.turnstiles = turnstiles;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (turnstiles.size() == 0)
                    Thread.sleep(10);

                for (int i = 0; i < turnstiles.size(); i++)
                    handle(i);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void handle(int i) {
        if (turnstiles.get(i).getSocket().isClosed()) return;
        try {
            ObjectInputStream is = turnstiles.get(i)
                                             .getInputStream();
            if (is.readUTF().contains("turnstile")) {
                turnstiles.get(i).increment();
                TurnstileServer.increment();
                System.out.println("Turnstile" + i + " Counter:"
                                           + turnstiles.get(i)
                                                       .getCounter());
                System.out.println("Totals Counter: " + TurnstileServer
                        .getCounter());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
