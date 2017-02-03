import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Niki on 2017-02-03.
 *
 * @author Niki
 */
public class TurnstileServer {

    private ServerSocket server;
    private ArrayList<Turnstile> turnstiles;
    private ArrayList<Monitor> monitors;

    private static long counter;

    public TurnstileServer() {
        turnstiles = new ArrayList<>();
        monitors = new ArrayList<>();

        Collections.synchronizedList(turnstiles);
        Collections.synchronizedList(monitors);
    }

    private void startServer() {
        TurnstilesThread handleTurnstiles = new TurnstilesThread(turnstiles);
        handleTurnstiles.start();

        MonitorsThread handleMonitors = new MonitorsThread(monitors,
                                                           turnstiles);
        handleMonitors.start();

        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress("127.0.0.1", 8080));

            Socket socket;
            while ((socket = server.accept()) != null)
                handleNewConnection(socket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TurnstileServer server = new TurnstileServer();
        server.startServer();
    }

    private void handleNewConnection(Socket socket) throws IOException {
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

        switch (is.readUTF().toLowerCase()) {
            case "turnstile":
                Turnstile t = new Turnstile(socket, is);
                turnstiles.add(t);
                System.out.println("\nTurnstile connected...\n");
                break;
            case "monitor":
                Monitor m = new Monitor(socket, is);
                monitors.add(m);
                System.out.println("\nMonitor connected...\n");
                break;
        }

    }

    public static synchronized void increment() {
        counter++;
    }

    public static long getCounter() {
        return counter;
    }

}
