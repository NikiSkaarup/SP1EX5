import java.io.*;
import java.net.Socket;

/**
 * Created by Niki on 2017-02-03.
 *
 * @author Niki
 */
public class Monitor {

    private String name;

    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream is;

    public Monitor(Socket socket, ObjectInputStream is) {
        this.socket = socket;
        this.is = is;
    }

    public Socket getSocket() { return socket; }

    public ObjectOutputStream getOutputStream() {
        if (os == null) {
            try {
                os = new ObjectOutputStream(socket.getOutputStream());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return os;
    }

    public ObjectInputStream getInputStream() {
        if (is == null) {
            try {
                is = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return is;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
