package com.softemm.smtp;   
   
import java.io.BufferedReader;   
import java.io.File;   
import java.io.FileOutputStream;   
import java.io.IOException;   
import java.io.InputStreamReader;   
import java.io.PrintStream;   
import java.io.PrintWriter;   
import java.net.ServerSocket;   
import java.net.Socket;   
import java.text.SimpleDateFormat;   
import java.util.*;
import util.Base64;
 
   
public class SmtpServer implements Runnable {   
   
    private Socket socket;   
    private BufferedReader br;   
    private PrintWriter pw;   
    private Properties props;   
      
    private static final String HELO = "helo";   
    private static final String EHLO = "ehlo";   
    private static final String MAIL_FROM = "mail from:";   
    private static final String RCPT_TO = "rcpt to:";   
    private static final String DATA = "data";   
    private static final String QUIT = "quit";   
    private static final String AUTH_LOGIN = "auth login";   
    
    String len = "";   
    File mailToPath = null;   
   
    public SmtpServer(Socket socket,Properties props) {   
        this.socket = socket;   
        this.props = props;   
    }   
   
    public void run() {   
        // init();   
   
        doProcess();   
   
    }   
   
    public void doProcess() {   
        try {   
            br = new BufferedReader(new InputStreamReader(socket   
                    .getInputStream()));   
            pw = new PrintWriter(socket.getOutputStream());   
            pw.println("220 163.com Anti-spam GT for Coremail System <163com[20050206]>");   
            pw.flush();   
            //String len = "";   
            while ((len = br.readLine()) != null) {   
                if (len.contains(HELO)) {   
                    doHelo(pw);   
                } else if (len.contains(EHLO)) {   
                    doEhlo(pw);   
                } else if (len.contains(AUTH_LOGIN)) {   
                    doAuthLogin(br, pw);   
                } else if (len.contains(MAIL_FROM)) {   
                    doMailFrom(pw);   
                } else if (len.contains(RCPT_TO)) {   
                    doRcptTo(pw);   
                } else if (len.equals(DATA)) {   
                    doData(br, pw);   
                } else if (len.equals(QUIT)) {   
                    pw.println("221 bye");   
                    pw.flush();   
                    break;   
                }   
            }   
            br.close();   
            pw.close();   
            socket.close();   
        } catch (IOException e) {   
            // TODO Auto-generated catch block   
            e.printStackTrace();   
        }   
    }   
   
    private void doHelo(PrintWriter pw) {   
        pw.println("250 mail");   
        pw.flush();   
    }   
   
    private void doEhlo(PrintWriter pw) {   
        pw.println("250-mail");   
        pw.println("250-PIPELINING");   
        pw.println("250-AUTH LOGIN PLAIN");   
        pw.println("250-AUTH=LOGIN PLAIN");   
        pw.println("250 8BITMIME");   
        pw.flush();   
   
    }   
   
    /**  
     * @param br  
     * @param pw  
     */   
    private void doAuthLogin(BufferedReader br, PrintWriter pw) {   
   
        try {   
            pw.println("334" + Base64.encode("auth login".getBytes()));   
            pw.flush();   
            String line = "";   
            while ((line = br.readLine()) != null) {   
   
                String base64Name = Base64.encode(line.getBytes());   
                System.out.println("authLogin:= " + line);   
                Enumeration enu = props.propertyNames();   
                while (enu.hasMoreElements()) {   
                    if (base64Name.equals(Base64.encode(enu.nextElement().toString().getBytes()))) {   
                        pw.println("334 " + base64Name);   
                        pw.flush();   
                    }   
                    String pass = br.readLine();   
                    String password = props.getProperty(line);   
                    String base64Password = Base64.encode(password.getBytes());   
                    if (base64Password.equals(Base64.encode(pass.getBytes()))) {   
                        pw.println("235 Authentication successful");   
                        pw.flush();   
                        return;   
                    }   
                       
                }   
   
            }   
   
        } catch (IOException e) {   
            // TODO Auto-generated catch block   
            e.printStackTrace();   
        }   
   
    }   
   
    /**  
     * @param br  
     * @param pw  
     */   
    public void doMailFrom(PrintWriter pw) throws IOException {   
        pw.println("250 Mail OK");   
        pw.flush();   
           
        String mailFrom = len.substring(len.indexOf("<") + 1, len   
                .indexOf("@"));   
        File file = new File("Smtp" + mailFrom + "\\");   
        file.mkdirs();   
    }   
   
    /**  
     * @param pw  
     * @throws IOException  
     */   
    public void doRcptTo(PrintWriter pw) throws IOException {   
        pw.println("250 Mail OK");   
        pw.flush();   
        String mailTo = len   
                .substring(len.indexOf("<") + 1, len.indexOf("@"));   
        File file = new File("Smtp" + mailTo + "\\");   
        file.mkdirs();   
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmssSSS");   
        String fileName = sdf.format(new Date());   
        mailToPath = new File(file.getPath() + ".txt");   
        mailToPath.createNewFile();   
    }   
   
    /**  
     * @param br  
     * @param pw  
     */   
    public void doData(BufferedReader br, PrintWriter pw) {   
        pw.println("354 Send from Rising mail proxy");   
        pw.flush();   
        String str = "";   
        try {   
            System.out.println("mail To path :="+mailToPath);   
            FileOutputStream fos = new FileOutputStream("mailToPath");   
            PrintStream ps = new PrintStream(fos);   
            while ((str = br.readLine()) != null) {   
                if (str.equals(".")) {   
                    break;   
                }   
                ps.print(str);   
                fos.flush();   
            }   
            fos.close();   
            pw.println("250 mail ok");   
        } catch (IOException e) {   
            // TODO Auto-generated catch block   
            e.printStackTrace();   
        }   
   
    }   
   
    public static void main(String[] args) {   
        try {   
            ServerSocket server = new ServerSocket(2025);   
            File file = new File("user.properties");   
            if (!file.exists()) {   
                file.createNewFile();   
            }   
            Properties props = new Properties();   
            props.load(new java.io.FileInputStream("user.properties"));   
            while (true) {   
                Socket socket = server.accept();   
                SmtpServer ss = new SmtpServer(socket,props);   
                Thread thread = new Thread(ss);   
                thread.start();   
            }   
        } catch (IOException e) {   
            // TODO Auto-generated catch block   
            e.printStackTrace();   
        }   
    }   
   
} 