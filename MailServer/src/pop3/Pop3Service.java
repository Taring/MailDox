package pop3;   
   
import java.io.*;
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
   
public class Pop3Service implements Runnable {   
   
    private Socket s;   
    private String username,password;   
    private BufferedReader br;   
    private PrintStream ps;     
    private Properties p;   
    private int num;   
    private long size;  
    private BufferedReader bbr;
       
       
    public Pop3Service(Socket s) {   
        super();   
        this.s = s;   
    }   
   
       
    public void init(){   
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
        ps.println("+OK Welcome to Mail Center");   
        try {   
            while(parseCommand(br.readLine())){   
            }   
        } catch (IOException e) {    
            e.printStackTrace();   
        }   
    }   
    
    private static String writeFile(String fileName, PrintStream ps)throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String s;
		StringBuilder sb = new StringBuilder();
		while((s = in.readLine()) != null) {
			sb.append(s + "\r\n");
			ps.println(s);
		}
		in.close();
		ps.println(".\r\n");
		return sb.toString() + ".\r\n";
    }
   
   
    private boolean parseCommand(String readLine) {   
        // TODO Auto-generated method stub   
        System.out.println("client:"+readLine);   
        if(readLine.startsWith("user")){   
            ps.println("+OK core mail");   
            username = readLine.substring(5, readLine.indexOf('@'));
        }else if(readLine.startsWith("pass")){   
            password = readLine;   
            File f = new File("mail/"+username); 
            System.out.println("Username:" + username);
            //System.out.println(f.getPath());
            File[] fs = f.listFiles();
            if (fs == null) {
            	System.out.println("Empty File of Usr");
            	return false;
            }
            num = fs.length;   
            System.out.print("File list: ");
            String tt = "";
            for (int i = 1; i < fs.length; i++) {   
                size =+ fs[i].length();
                tt += fs[i].getName() + " ";
    			System.out.print(fs[i].getName() + " ");
            }
    		System.out.println();
            ps.println("+OK "+num+"s "+size+" messages");
            ps.println(tt);
 
        }else if(readLine.startsWith("stat")){
        	//TO: Count the number of states on file f 
        	File f = new File("mail/" + username);
        	File[] fs = f.listFiles();
        	if (fs == null) {
        		ps.println("+OK " + 0);
        	} else {
        		System.out.println("stat-path: " + f.getPath() + " " + fs.length);
        		//for (int i = 0; i < fs.length; ++i)
        		ps.println("+OK " + fs.length);
        	}               
        }else if(readLine.startsWith("retr")){
        	//TO: Print the number-th file
        	int number = Integer.parseInt(readLine.substring(5));
        	File f = new File("mail/" + username);
        	File[] fs = f.listFiles();
        	System.out.println("retr-path: " + f.getPath() + " " + fs.length + " " + number);
        	if (number < fs.length) {
        		f = fs[number - 1];
        		ps.println("+OK");
        		//System.out.println("Transfer Begin");
        		try {
        			String tmpString;
        			//ps.println(tmpString = writeFile(f.getPath(), ps));
        			tmpString = writeFile(f.getPath(), ps);
        			//System.out.println("tmpString is:\n" + tmpString);
        		} catch (IOException e) {     
                    e.printStackTrace();   
                }
        		//System.out.println("Tranfer End");
        	} else 
        		ps.println("Insert wrong");   
        }else if(readLine.startsWith("dele")){  
        	//TO: DELETE the number-th file
        	int number = Integer.parseInt(readLine.substring(5));
        	File f = new File("mail/" + username);
        	File[] fs = f.listFiles();
        	if (number - 1 < fs.length) {
        		f = fs[number - 1];
        		f.delete();
                ps.println("+OK deleted"); 
        	} else {
        	//delete_file f
        		ps.println("Delete wrong");   
        	}
        }else if(readLine.equalsIgnoreCase("quit")){   
            ps.println("+OK cor mail");   
            return false;   
        }else {   
            ps.println("+OK wrong");   
               
        }   
           
        return true;   
    }   
   
       
       
}