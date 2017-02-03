import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Niki on 2017-02-03.
 *
 * @author Niki
 */
public class MonitorClient {

    public static void main(String[] args) {

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("127.0.0.1", 8080));
            if (socket.isConnected())
                System.out.println("Connected to a server");

            Thread t = new Thread(() -> {
                ObjectInputStream is = null;
                try {
                    is = new ObjectInputStream(socket.getInputStream());

                    while (true) {
                        System.out.println("Received: " + is.readLong());
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            os.flush();
            String msg = "monitor";
            String[] msgs = {"monitor", msg + "1", msg + "2", msg + "3", msg
                    + "4"};


            os.writeUTF(msg);
            os.flush();

            Random r = new Random();
            t.start();
            for (int i = 0; i < 10000; i++) {
                String tmp = msgs[i % msgs.length];
                os.writeUTF(tmp);
                os.flush();
                System.out.println(tmp);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
