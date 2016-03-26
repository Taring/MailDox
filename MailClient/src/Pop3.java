//This class is the achievement of Pop3 protocol

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Pop3 {
	private String server;
	private String address;
	private String password;
	
	private static String Newline = "\r\n";
	
	private int port = 2110;//110;
	private Socket socket;
	BufferedReader in;
	DataOutputStream out;
	
	public String getServer() {
		return server;
	}
	public void setServer(String server){
		this.server = server;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address){
		this.address = address;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password){
		this.password = password;
	}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public String respond() throws IOException {
		String ans = in.readLine();
		return ans;
	}
	
	public String send(String text) throws IOException {
		out.writeBytes(text);
		out.flush();
		String ans = respond();
		return ans;
	}
	
	public boolean checkResponse(String response, String correctNumber, String Error) {
		if (!response.startsWith(correctNumber)) {
			System.out.println(Error + response);
			return false;
		} else
			return true;
	}

	public boolean login() {
		try {
			//Login -> Address
			String addressAnswer = "user " + address + Newline;
			String loginAddressAnswer = send(addressAnswer);
			if (loginAddressAnswer == null)
				System.out.println("Terrible Mistake");
			if (!checkResponse(loginAddressAnswer, "+OK", "Login Address failed :"))
				return false;
			System.out.println(loginAddressAnswer);
			
			//Login -> Password
			String passwordAnswer = "pass " + password + Newline;
			String loginPasswordAnswer = send(passwordAnswer);
			if (!checkResponse(loginPasswordAnswer, "+OK", "Login Password failed :"))
				return false;
			System.out.println(loginPasswordAnswer);
			/*
			String allName = respond();
			System.out.println(allName);
			 */
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean quit() {
		try {
			String quitAnswer = send("quit " + Newline);
			if (!checkResponse(quitAnswer, "+OK", "Quit failed :"))
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int countMail() {
		try {
			String countAnswer = null;
			try {
				countAnswer = send("stat " + Newline);
				System.out.println(countAnswer);
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
			if (!countAnswer.startsWith("+OK")) {
				System.out.println("CountMail Error: " + countAnswer);
				return -2;
			}
			
			countAnswer = countAnswer.substring(4);
			if (countAnswer.indexOf(" ") > 0)
				countAnswer = countAnswer.substring(0, countAnswer.indexOf(" "));
			return Integer.parseInt(countAnswer);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public String getText() throws IOException {
		String answer = "";
		for (String tmp = respond(); !tmp.equals("."); tmp = respond())
			answer += tmp + "\r\n";
		return answer;
	}
	
	public String getMail(int number) {
		String getAnswer = null;
		try {
			getAnswer = send("retr " + number + Newline);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!checkResponse(getAnswer, "+OK", "Get failed :"))
			return null;
		System.out.println("Get Succeed!");
		
		String textAnswer = null;
		try {
			textAnswer = getText();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return textAnswer;
	}
	
	public void deleteMail(int number) {
		String deleteAnswer = null;
		try {
			deleteAnswer = send("dele " + number + Newline);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!checkResponse(deleteAnswer, "+OK", "Delete failed :"))
			return;
		System.out.println("Delete Succeed!");
	}
	
	public void Init() {
		boolean flag = true;
		try { 
			socket = new Socket(server, port); 
			in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
			out = new DataOutputStream(socket.getOutputStream());
			String initAnswer = respond(); 
			if(!initAnswer.startsWith("+OK")) {
				System.out.println("Init failed: " + initAnswer);
				flag = false;
			} 
		} catch (UnknownHostException e) { 
			System.out.println("UnknownHostException Error");
			flag = false;
			e.printStackTrace(); 
		} catch (IOException e) { 
			System.out.println("IOException Error"); 
			flag = false;
			e.printStackTrace(); 
		} 
		if (flag)
			System.out.println("Init succeed!");		
	}
	
	public void Close() throws IOException {
		socket.close();
		in.close();
		out.close();	
	}
}