import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    private static ServerSocket server;
    private static ArrayList<Turnstile> turnstiles;
    private static ArrayList<Monitor> monitors;

    private static long counter;

    public static void main(String[] args) {
        turnstiles = new ArrayList<>();
        monitors = new ArrayList<>();

        Collections.synchronizedList(turnstiles);
        Collections.synchronizedList(monitors);

        HandleTurnstiles();
        HandleMonitors();

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

    private static void HandleTurnstiles() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    if (turnstiles.size() == 0)
                        Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < turnstiles.size(); i++) {
                    if (turnstiles.get(i).getSocket().isClosed()) continue;
                    try {
                        ObjectInputStream is = turnstiles.get(i)
                                                         .getInputStream();
                        if (is.readUTF().contains("turnstile")) {
                            turnstiles.get(i).increment();
                            increment();
                            System.out.println("Turnstile" + i + " Counter:"
                                                       + turnstiles.get(i)
                                                                   .getCounter());
                            System.out.println("Totals Counter: " + counter);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();
    }

    private static void HandleMonitors() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    if (monitors.size() == 0)
                        Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < monitors.size(); i++) {
                    try {
                        ObjectInputStream is = monitors.get(i).getInputStream();
                        String s = is.readUTF();
                        if (s.contains("monitor")) {

                            ObjectOutputStream os = monitors.get(i)
                                                            .getOutputStream();
                            switch (s) {
                                case "monitor1":
                                    if (turnstiles.size() >= 1)
                                        os.writeLong((long) turnstiles.get(0)
                                                                      .getCounter());
                                    break;
                                case "monitor2":
                                    if (turnstiles.size() >= 2)
                                        os.writeLong((long) turnstiles.get(1)
                                                                      .getCounter());
                                    break;
                                default:
                                    os.writeLong(counter);
                                    break;
                            }
                            os.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();
    }

    private static void handleNewConnection(Socket socket) throws IOException {
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


    private static String handleCommands(String msg) {
        if (!msg.contains("#"))
            return msg;

        String[] parts = msg.split("#");
        switch (parts[0]) {
            case "UPPER":
                msg = parts[1].toUpperCase();
                break;
            case "LOWER":
                msg = parts[1].toLowerCase();
                break;
            case "REVERSE":
                msg = reverse(parts[1]);
                break;
            default:
                break;
        }

        return msg;
    }

    private static String reverse(String msg) {
        String reversed = "";
        char[] chars = msg.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--)
            reversed += chars[i];

        return reversed;
    }

}
