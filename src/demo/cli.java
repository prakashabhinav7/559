package demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.UUID;

public class cli {
	public static void main(String args[]) throws UnknownHostException, IOException
	{
		int number, temp, port;
		String s_ip, c_ip;
		
		s_ip = args[0];
		port = Integer.parseInt(args[1]);
		
		Scanner sc = new Scanner(System.in);//Reading input from the user
		Socket s = new Socket(s_ip,port);//Create client socket - Takes IP address and the port number of the machine and the application on the machine to connect to
		System.out.println("Connected to server:" + s_ip+ "\nTalking on port:" +  port);

		SecureRandom r_A = new SecureRandom(); //create a 128-bit random number r_A
		byte[] ivInBytes = new byte[16];
		r_A.nextBytes(ivInBytes);   
		System.out.println(ivInBytes);
		
		
		//-------------------------------------------------------------//
		

		
		
//		String file = args[2];
//		Reader r = new FileReader(args[2]);
		Scanner input = new Scanner(new File(args[2]));

//		while (input.hasNextLine())
//		{
//		   System.out.println(input.nextLine());
//		}
//		
		Scanner sc1 = new Scanner(s.getInputStream());
		System.out.println("\nEnter the number:");
		number = sc.nextInt();
		
		PrintStream p =new PrintStream(s.getOutputStream()); //pushing the number to the server
		p.println(number);
		
		temp=sc1.nextInt();
		System.out.println(temp);
	}
}
