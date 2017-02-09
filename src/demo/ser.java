package demo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;




/**
 * @author Abhinav Prakash<br />
 *         Date: Feb 8, 2017
 * @param:
 * args[0] = Server's IP address
 * args[1] = Server's port
 * args[2] = Client's IP address
 * args[3] = Clients port
 * args[4] = File containing Client's private key
 * args[5] = File containing Server's public key
 * args[5] = A text file
 * 
 * @throws IOException 
 */


public class ser {
	
	public static void main(String args[]) throws IOException
	{
		int number, temp, port;
		String s_ip, c_ip;
		
		
		s_ip = args[0];
		System.out.println("Server running at IP = " +  args[0]);
		
		port = Integer.parseInt(args[1]);
		System.out.println("Listening on port " +  port);
		ServerSocket s1 = new ServerSocket(port); //create a server socket and pass the port number to be used by the client
		
		c_ip = args[2];
		System.out.println("Waiting for client " +  args[2]);
		

		Socket ss = s1.accept(); //accepts incoming request
		System.out.println("Connected to client port: " +  args[3]);
		
		//-------------------------------------------------------------//
		
		
		
 
		
		
		
		Scanner sc = new Scanner(ss.getInputStream());//new scanner object to store the number passed by the user to the client
		number=sc.nextInt();
		
		temp = number*2;
		
		PrintStream p = new PrintStream(ss.getOutputStream());
		p.println(temp);
	}
}
