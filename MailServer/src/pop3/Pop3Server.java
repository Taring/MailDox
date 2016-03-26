package pop3;   
   
import java.io.IOException;   
import java.net.ServerSocket;   
import java.net.Socket;   
   
public class Pop3Server implements Runnable {   
   
    public void run() {   
        try {
            ServerSocket serversocket = new ServerSocket(2110);   
            while (true) {
                Socket socket = serversocket.accept();   
                Pop3Service service = new Pop3Service(socket);   
                new Thread(service).start();   
            }
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
   
} 