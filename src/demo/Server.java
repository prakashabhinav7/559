package demo;
import java.io.*;
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
	
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException, NoSuchProviderException{
		int number, temp, s_port, c_port;
		String s_ip, c_ip;
		//Reading all args[]
		s_ip = args[0]; //Server IP
		s_port = Integer.parseInt(args[1]); //Server Port Number
		c_ip = args[2]; //Client IP
		c_port = Integer.parseInt(args[3]); //Client Port Number
//		FileInputStream privateKey = new FileInputStream(args[4]); // Client's private key    
//		FileInputStream publicKey = new FileInputStream(args[5]); // Server's public key
//		FileInputStream stuff = new FileInputStream(args[6]); // The text file
//		
		
		//Connecting to the client and reading the encrypted key and signature sent by the client
		clientConnect(s_port);
		
		
		//Random Number Generation
		byte[] r_B = randomNumber();
		
		
		//Key generation for the Server
		String absPath ="";
		KeyPair keys = generateKeyPair();
		PublicKey keyPublic = keys.getPublic( );//Public component of the key pair
		PrivateKey keyPrivate = keys.getPrivate( );//Private component of the key pair
		
		//Saving Key Pair to File
		saveKeyPair(keyPrivate,keyPublic, absPath);

		
		//Encrypting the random number with public key
		byte[] cypherNumber = encrypt(r_B, keyPublic); ////to change keyPublic to the public key of server on file
		
		//Signing the random number
		byte[] signedNumber = sign(r_B, keyPrivate);
		
		//Send the encrypted number and the signed number to the server
		sendToServer(s_ip, s_port, cypherNumber,signedNumber);
				
				
			}

	
	public static void clientConnect(int port) throws IOException{
		ServerSocket ss = new ServerSocket(port);//Opens a new server socket
		Socket serverSocket = ss.accept();//establishes connection(normal socket)
		
		DataInputStream in = new DataInputStream(serverSocket.getInputStream());//Create a new DataInputStream Object to read the encrypted message and the sign
		String cypherN = in.readUTF();
		//System.out.print(cypherN + "\n");
		String signedN = in.readUTF();
        //System.out.print(signedN);

	}
	
	public static byte[] randomNumber(){
		SecureRandom randNumber = new SecureRandom();
		byte[] ivInBytes = new byte[16]; // 128 bits converted to 16 bytes
		randNumber.nextBytes(ivInBytes);
		return ivInBytes;
		
	}
			
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException{

		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA"); //Specify the algorithm for key pair generation
		kpg.initialize(1024); //specify the length of key
		KeyPair kp = kpg.generateKeyPair( );//Generate the public-private keypair
		return kp; 
	}
	
	public static void saveKeyPair(PrivateKey prikey, PublicKey pubkey, String path) throws IOException{
		FileOutputStream pbf = new FileOutputStream(path + "s_public.key"); //Writing client's public key to file
		byte[] kPub = pubkey.getEncoded();
		pbf.write(kPub);
		pbf.close();
		
		FileOutputStream prf = new FileOutputStream(path + "s_private.key"); //Writing client's private key to file
		byte[] kPri = prikey.getEncoded();
		prf.write(kPri);
		prf.close();
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

	
	public static void sendToServer(String serverIP, int serverPort, byte[] cypherN, byte[] signedN) throws UnknownHostException, IOException{
		Socket clientSocket = new Socket(serverIP,serverPort); //Opens communication at the Server IP and Server Port as set in the argument list
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        String cypherNS = cypherN.toString();
        String signedNS = signedN.toString();
        
        out.writeUTF(cypherNS);
        //System.out.print(cypherNS + "\n");
        
        out.writeUTF(signedNS);
        //System.out.print(signedNS);
	}

}