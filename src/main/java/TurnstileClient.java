import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Niki on 2017-02-03.
 *
 * @author Niki
 */
public class TurnstileClient {
    public static void main(String[] args) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("127.0.0.1", 8080));
            if(socket.isConnected())
                System.out.println("Connected to server");

            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            os.flush();

            String msg = "turnstile";

            for (int i = 0; i < 10000; i++) {
                os.writeUTF(msg);
                os.flush();
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
