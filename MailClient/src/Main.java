//This class is the test class of basic Server and Client

import java.util.*;
import java.io.*;

public class Main {
	
	private static Scanner in = new Scanner(System.in);
	private static String[] SmtpServer = {"localhost", "smtp.sjtu.edu.cn"};
	private static String[] Pop3Server = {"localhost", "pop3.sjtu.edu.cn"};
	private static String Address = "taringlee@sjtu.edu.cn";
	private static String FromAddress = "arrianna@sjtu.edu.cn";
	private static String Password = "ljj1220";
	private static String To = "379710956@qq.com";
	private static String Subject = "Test Mail";
	private static String Text = "I miss U... \n Dreamaker";
	
	public static void testSmtp(int choice) {
		System.out.println("Test Smtp");
		
		Smtp mail = new Smtp();
		mail.setServer(SmtpServer[choice]);
		mail.setAddress(Address);
		mail.setPassword(Password);
		mail.setFrom(FromAddress);
		mail.setTo(To);
		mail.setSubject(Subject);
		mail.setText(Text);
		if (choice != 0)
			mail.setPort(25);
		
		boolean state = mail.sendMail();
		if (state)
			System.out.println("Send Email Succeed");
		else
			System.out.println("Send Email Failed");
	}
	
	public static void testPop3(int choice) {
		System.out.println("Test Pop3");
		
		Pop3 mail = new Pop3();
		mail.setServer(Pop3Server[choice]);
		//mail.setAddress(To);
		mail.setAddress(Address);
		mail.setPassword(Password);
		if (choice != 0)
			mail.setPort(110);
		
		mail.Init();

		if (!mail.login())
			return;
		System.out.println("The number of mails: " + mail.countMail());
		
		for (String input = in.next(); !input.startsWith("q"); input = in.next()) {
			if (input.startsWith("g")) {
				//Action: Get
				int number = in.nextInt();
				String answer = mail.getMail(number);
				if (answer != null)
					System.out.println(answer);
			} else if (input.startsWith("d")) {
				//Action Delete
				int number = in.nextInt();
				mail.deleteMail(number);
			} else if (input.startsWith("c")) {
				System.out.println("The number of mails: " + mail.countMail());
			}
		}
	
		mail.quit();
		
		try {
			mail.Close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Close Error!");
		}
	}
	
	public static void main(String[] args) {
		int choice = 1;
		/*
		 If (args[1].equalsIgnareCase("localhost"))
		 	choice = 0;
		 else
		 	choice = 1;
		 */
		//if (Integer.parseInt(args[0]) == '0' || args[0].equalsIgnoreCase("Smtp")) {
		//testSmtp(choice);
		//} else {
		testPop3(choice);
		//}
	}
	
}