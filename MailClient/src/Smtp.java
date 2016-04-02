//This class is the achievement of Smtp protocol
import java.io.*;
import java.util.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Smtp {
	private String server;
	private String address;
	private String password;
	private String from;
	private String to;
	private String subject;
	private String text;

	private static String Newline = "\r\n";

	private int port = 2025;//25;
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

	public String getFrom() {
		return from;
	}
	public void setFrom(String from){
		this.from = from;
	}

	public String getTo() {
		return to;
	}
	public void setTo(String to){
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}

	public String getText() {
		return text;
	}
	public void setText(String text){
		this.text = text;
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

	public void Init() {
		boolean flag = true;
		try {
			socket = new Socket(server, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
			String initAnswer = respond();
			if(!initAnswer.startsWith("220")) {
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

	/*
	 * A complete process to send an email
	*/
	public boolean sendMail() {
		Init();
		//check Necessity
		if (server == null) return false;
		if (address == null) return false;
		if (password == null) return false;
		if (from == null) return false;
		if (to == null) return false;

		try {
			//Connection
			String connectionAnswer= send("HELO " + server + Newline);
			System.out.println("C: HELO " + server + Newline);
			if (!checkResponse(connectionAnswer, "250", "Connection failed :"))
				return false;
			System.out.println("S: " + connectionAnswer);

			//Login
			String loginAnswer = send("AUTH LOGIN" + Newline);
			System.out.println("C: AUTH LOGIN" + Newline);
			if (!checkResponse(loginAnswer, "334", "Login failed :"))
				return false;
			System.out.println("S: " + loginAnswer);

			//Login -> Address
			String addressAnswer = new String(Base64.getEncoder().encode(address.getBytes())) + Newline;
			System.out.println("C: " + addressAnswer);
			String loginAddressAnswer = send(addressAnswer);
			if (!checkResponse(loginAddressAnswer, "334", "Login Address failed :"))
				return false;
			System.out.println("S: " + loginAddressAnswer);

			//Login -> Password
			String passwordAnswer = new String(Base64.getEncoder().encode(password.getBytes())) + Newline;
			System.out.println("C: " + passwordAnswer);
			String loginPasswordAnswer = send(passwordAnswer);
			if (!checkResponse(loginPasswordAnswer, "235", "Login Password failed :"))
				return false;
			System.out.println("S: " + loginPasswordAnswer);

			//Set From
			String fromAnswer = send("MAIL FROM: <" + from + ">" + Newline);
			System.out.println(fromAnswer);
			if (!checkResponse(fromAnswer, "250", "Set From failed :"))
				return false;
			System.out.println("S: " + fromAnswer);

			//Set To
			String toAnswer = send("RCPT TO: <" + to + ">" + Newline);
			System.out.println(toAnswer);
			if (!checkResponse(toAnswer, "250", "Set To failed :"))
				return false;
			System.out.println("S: " + toAnswer);

			//Send Beginning
			String dataAnswer = send("DATA" + Newline);
			System.out.println("C: DATA" + Newline);
			if (!checkResponse(dataAnswer, "354", "Send DATA Request failed :"))
				return false;
			System.out.println("S: " + dataAnswer);

			//Send All
			String fullText = "From: " + from + Newline;
			fullText += "To: " + to + Newline;
			fullText += "Subject: " + subject + Newline;
			fullText += Newline;
			fullText += text + Newline;
			fullText += "." + Newline;
			String textAnswer = send(fullText);
			System.out.println("C: " + fullText);
			if (!checkResponse(textAnswer, "250", "Send text failed :"))
				return false;

			try {
				System.out.println("Write a number");
				int a = System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Quit
			System.out.println("QUIT" + Newline);
			String quitAnswer = send("QUIT" + Newline);
			if (!checkResponse(quitAnswer, "221", "Quit failed :"))
				return false;
			Close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
