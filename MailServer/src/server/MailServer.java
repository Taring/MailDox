package server;   
   
import pop3.Pop3Server;   
import smtp.SmtpServer;   
   
   
   
public class MailServer {   
   
    public static void main(String[] args) {   
        System.out.println("Mail Server start.....");   
        //SMPT   
        SmtpServer smtpServer = new SmtpServer();   
        new Thread(smtpServer).start();   
       
        //POP3
        Pop3Server pop3Server = new Pop3Server();   
        new Thread(pop3Server).start();   
           
    }   
}   