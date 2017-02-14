package demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.math.BigInteger;
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
		
		
		//-------------------------------------------------------------//
		

		
		
//		String file = args[2];
//		Reader r = new FileReader(args[2]);
		Scanner input = new Scanner(new File(args[6]));

//		while (input.hasNextLine())
//		{
//		   System.out.println(input.nextLine());
//		}
//		
		
//		Random number generation
		SecureRandom sr = new SecureRandom();
		byte[] ivInBytes = new byte[16];
		sr.nextBytes(ivInBytes); 
		
		
		BigInteger r_A = new BigInteger(ivInBytes); //storing the 128 bits
		System.out.println(r_A.bitLength()); 
		
		
		Scanner sc1 = new Scanner(s.getInputStream());
		System.out.println("\nEnter the number:");
		number = sc.nextInt();
		
		PrintStream p = new PrintStream(s.getOutputStream()); //pushing the number to the server
		p.println(number);
		
		temp=sc1.nextInt();
		System.out.println(temp);
	}
}
