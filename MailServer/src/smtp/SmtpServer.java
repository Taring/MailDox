package smtp;   
   
import java.io.IOException;   
import java.net.ServerSocket;   
import java.net.Socket;   
   
public class SmtpServer implements Runnable {   
    public void run() {   
        try {  
            ServerSocket serversocket = new ServerSocket(2025);   
            while (true) {  
                Socket socket = serversocket.accept();   
                System.out.println("Wait for Smtp!");   
                SmtpService service = new SmtpService(socket);   
                new Thread(service).start();   
            }  
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
   
    }   
   
} 