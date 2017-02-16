package demo;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Server {
	public Server() {
	}
	
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException, NoSuchProviderException, InvalidKeySpecException{
		int number, temp, s_port, c_port;
		String s_ip, c_ip;
		//Reading all args[]
		s_ip = args[0]; //Server IP
		s_port = Integer.parseInt(args[1]); //Server Port Number
//		c_ip = args[2]; //Client IP
		c_port = Integer.parseInt(args[3]); //Client Port Number
//		FileInputStream privateKey = new FileInputStream(args[4]); // Client's private key    
//		FileInputStream publicKey = new FileInputStream(args[5]); // Server's public key
//		FileInputStream stuff = new FileInputStream(args[6]); // The text file
//		
		
		//Connecting to the client and reading the encrypted message and signature sent by the client for r_A
		c_ip = clientConnect(c_port);
		
		
		//Random Number Generation
		byte[] r_B = randomNumber();
		
		
		//Set path to the key here
		String absPath ="";
		
		//Loading Client's Public Key("c_public.key") from file	
		File filePublicKey = new File(absPath + args[5]);
		FileInputStream fiss = new FileInputStream(absPath + args[5]);
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fiss.read(encodedPublicKey);
		fiss.close();

		
		//Loading Server's Private Key("s_private.key") from file
		File filePrivateKey = new File(absPath + args[4]);
		FileInputStream fisc = new FileInputStream(absPath + args[4]);
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fisc.read(encodedPrivateKey);
		fisc.close();
		
		
		// Converting from byte arrays to keys
		KeyFactory kf = KeyFactory.getInstance("RSA"); 
		PublicKey c_publicKey = kf.generatePublic(new X509EncodedKeySpec(encodedPublicKey));
		PrivateKey s_privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));
		
		
		//Encrypting the random number with client's public key
		byte[] cypherNumber = encrypt(r_B, c_publicKey); 
		
		//Signing the random number with server's private key
		byte[] signedNumber = sign(r_B, s_privateKey);
		
		//Send the encrypted number and the signed number to the server
		sendToClient(c_ip, c_port, cypherNumber,signedNumber);
				
			}

	
	public static String clientConnect(int port) throws IOException{
		ServerSocket ss = new ServerSocket(port);//Opens a new server socket and waits for client
		
		Socket serverSocket = ss.accept();//establishes connection(normal socket) and reads the files sent by the client
		
		DataInputStream in = new DataInputStream(serverSocket.getInputStream());//Create a new DataInputStream Object to read the encrypted message and the sign
		System.out.print("\nMessage from Client: \n");
		String cypherN = in.readUTF();
		System.out.print(cypherN + "\n");//
		String signedN = in.readUTF();
		System.out.print(signedN);//
        
        InetAddress cipp = ss.getInetAddress();
		String cip = cipp.getHostName();
		return cip; //save the IPaddress of the client

	}
	
	public static byte[] randomNumber(){
		SecureRandom randNumber = new SecureRandom();
		byte[] ivInBytes = new byte[16]; // 128 bits converted to 16 bytes
		randNumber.nextBytes(ivInBytes);
		return ivInBytes;
		
	}
				
	public static byte[] encrypt(byte[] rNum,PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA"); //The algorithm to be used
		cipher.init(Cipher.ENCRYPT_MODE, pubKey); //Initialize the cipher with the public key 
	    return cipher.doFinal(rNum);//	Return the encrypted number
	}
	
	
	public static byte[] sign(byte[] rNum, PrivateKey priKey) throws SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException{

		Signature sigSender = Signature.getInstance("SHA512withRSA");//The algorithm to be used
		sigSender.initSign(priKey);//Initialize the cipher with the public key
		sigSender.update(rNum);
		return sigSender.sign();
		
	}

	
	public static void sendToClient(String clientIP, int clientPort, byte[] cypherNum, byte[] signedNum) throws UnknownHostException, IOException{
		Socket clientSocket = new Socket(clientIP,clientPort); //Opens communication at the Server IP and Server Port as set in the argument list
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        
        
        out.write(cypherNum);
        System.out.print("\nSending to Client: \n"+cypherNum + "\n");
        
        out.write(signedNum);
        System.out.print(signedNum);

	}

}