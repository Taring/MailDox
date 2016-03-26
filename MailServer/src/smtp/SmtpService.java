 package smtp;   
   
import java.io.BufferedReader;   
   
import java.io.File;   
import java.io.FileInputStream;   
import java.io.FileOutputStream;   
import java.io.IOException;   
import java.io.InputStreamReader;  
import java.io.PrintStream;   
import java.net.Socket;   
import java.util.Properties;   
   
import util.Base64;   
   
public class SmtpService implements Runnable {   

    private Socket s ;   
    private BufferedReader br;   
    private PrintStream ps;   
    private PrintStream ps1;   
    private PrintStream ps2;   
    private Properties p;   
   
    File mailFromPath;   
    File mailToPath;   
       
    public SmtpService(Socket s) {   
        super();   
        this.s = s;   
    }   
   
    public void init (){   
        try {   
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));   
            ps = new PrintStream(s.getOutputStream());   
            p = new Properties();   
            p.load(new FileInputStream("user.properties"));   
               
        } catch (IOException e) {      
            e.printStackTrace();   
        }   
           
    }   
   
    public void run() {   
        init();   
        ps.println("220 ");   
        try {   
            while(parseCommand(br.readLine())){   
                   
                   
            }   
        } catch (IOException e) {   
            e.printStackTrace();   
        }finally{   
            try {   
                br.close();   
                ps.close();   
                s.close();   
            } catch (IOException e) {   
                e.printStackTrace();   
            }   
        }   
    }   
       
       
    private boolean parseCommand(String readLine) {   
        // TODO Auto-generated method stub   
        System.out.println("client:"+readLine);   
        if(readLine.startsWith("EHLO")){   
            ps.println("250-mail");   
            ps.println("250-PIPELINING");   
            ps.println("250-AUTH LOGIN PLAIN");   
            ps.println("250-AUTH=LOGIN PLAIN");   
            ps.println("250 8BITMIME");   
               
        }else if(readLine.startsWith("HELO")){   
            ps.println("250 mail");   
               
        }else if(readLine.startsWith("AUTH LOGIN")){   
            ps.println("334 dXNlcm5hbWU6");   
               
            String username;   
            try {   
                username = new String(Base64.decode(br.readLine()));   
                ps.println("334 UGFzc3dvcmQ6");   
                String password = new String(Base64.decode(br.readLine()));   
                System.out.println(username+":"+password);   
                if(!(p.containsKey(username)&&p.getProperty(username).equalsIgnoreCase(password))){   
                    //System.out.println("Fail to check them!");   
                    System.out.println("Check Error, We will create address");
                    
                	//return false;   
                }   
            } catch (IOException e) {     
                e.printStackTrace();   
            }   
            //System.out.println(123);   
               
               
            ps.println("235 Authentication successful");   
        }else if(readLine.startsWith("MAIL FROM:")){   
            String name = readLine.substring(readLine.lastIndexOf("<")+1,readLine.lastIndexOf("@"));   
            //File f = new File("mail/"+name+File.separator+"send");   
            File f = new File("mail/"+name);
            f.mkdirs();
             mailFromPath =  new File(f,String.valueOf(System.currentTimeMillis())+".txt");   
                try {
                    mailFromPath.createNewFile();   
                } catch (IOException e) {   
                    // TODO Auto-generated catch block   
                    e.printStackTrace();   
                }   
            ps.println("250 Mail OK");   
        }else if(readLine.startsWith("RCPT TO:")){   
            String toName = readLine.substring(readLine.lastIndexOf("<")+1,readLine.lastIndexOf("@"));   
               
            //File f = new File("mail/"+toName+File.separator+"receive");
            File f = new File("mail/" + toName);
            f.mkdirs();   
            mailToPath =  new File(f,String.valueOf(System.currentTimeMillis())+".txt");   
            try {   
                mailToPath.createNewFile();   
            } catch (IOException e) {   
                // TODO Auto-generated catch block   
                e.printStackTrace();   
            }   
            ps.println("250 Mail OK");   
        }else if(readLine.startsWith("DATA")){   
            ps.println("354 Send from Rising mail proxy");   
            String line =null;   
            try {   
                //ps1 = new PrintStream(new FileOutputStream(mailFromPath));   
                ps2 = new PrintStream(new FileOutputStream(mailToPath));   
                while((line = br.readLine())!=null){   
                    if(line.equals("."))break;   
                    //ps1.println(line);   
                    ps2.println(line);   
                    //ps1.flush();   
                    ps2.flush();   
                }   
                //ps1.close();   
                ps2.close();   
            } catch (IOException e) {   
                // TODO Auto-generated catch block   
                e.printStackTrace();   
            }   
               
            ps.println("250 Mail OK");   
        }else if(readLine.equalsIgnoreCase("QUIT")){   
            ps.println("221 Bye");   
            return false;   
        }else {   
            ps.println("250 wrong");   
               
        }   
        return true;   
    }   
   
       
       
    public static void main(String[] args) { 
           
        File f = new File("mail/FSD/receive");   
        f.mkdirs();   
    File mailFrom = new File(f,String.valueOf(System.currentTimeMillis())+".txt");   
    try {   
        mailFrom.createNewFile();   
    } catch (IOException e) {   
        // TODO Auto-generated catch block   
        e.printStackTrace();   
    }   
}   
   
   
} 