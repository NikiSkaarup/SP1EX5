import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Niki on 2017-02-03.
 *
 * @author Niki
 */
public class MonitorsThread extends Thread {

    ArrayList<Monitor> monitors;
    ArrayList<Turnstile> turnstiles;

    public MonitorsThread(ArrayList<Monitor> monitors, ArrayList<Turnstile>
            turnstiles) {
        this.monitors = monitors;
        this.turnstiles = turnstiles;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (monitors.size() == 0)
                    Thread.sleep(10);

                for (int i = 0; i < monitors.size(); i++)
                    handle(i);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(int i) {
        String m = "monitor";

        try {
            ObjectInputStream is = monitors.get(i).getInputStream();
            String s = is.readUTF();
            if (s.contains(m)) {
                ObjectOutputStream os = monitors.get(i).getOutputStream();
                int j = -1;
                if (s.length() > m.length())
                    j = Integer.parseInt(s.substring(m.length()));

                if (j > 0) {
                    if (turnstiles.size() >= j)
                        os.writeLong((long) turnstiles.get(j - 1).getCounter());
                } else {
                    os.writeLong(TurnstileServer.getCounter());
                }

                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
