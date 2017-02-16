package demo;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Client {
	public Client() {
	}
	
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, SignatureException, NoSuchProviderException, InvalidKeySpecException{
		int number, temp, s_port, c_port;
		String s_ip, c_ip;
		String ALGORITHM = "RSA";
//		//Reading all args[]
		s_ip = args[0]; //Server IP
		s_port = Integer.parseInt(args[1]); //Server Port Number
//		c_ip = args[2]; //Client IP
//		c_port = Integer.parseInt(args[3]); //Client Port Number
//		FileInputStream privateKey = new FileInputStream(args[4]); // Client's private key    
//		FileInputStream publicKey = new FileInputStream(args[5]); // Server's public key
//		FileInputStream stuff = new FileInputStream(args[6]); // The text file
		
		//Random Number Generation
		byte[] r_A = randomNumber();
		
		//Set path to the key here
		String absPath ="";
		
		//Loading Server's Public Key("s_public.key") from file	
		File filePublicKey = new File(absPath + args[5]);
		FileInputStream fiss = new FileInputStream(absPath + args[5]);
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fiss.read(encodedPublicKey);
		fiss.close();

		
		//Loading Clients's Private Key("c_private.key") from file
		File filePrivateKey = new File(absPath + args[4]);
		FileInputStream fisc = new FileInputStream(absPath + args[4]);
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fisc.read(encodedPrivateKey);
		fisc.close();
		
		
		// Converting from byte arrays to keys
		KeyFactory kf = KeyFactory.getInstance("RSA"); 
		PublicKey s_publicKey = kf.generatePublic(new X509EncodedKeySpec(encodedPublicKey));
		PrivateKey c_privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));

		
		
		//Encrypting the random number with public key of the SERVER
		byte[] cypherNumber = encrypt(r_A, s_publicKey); //Encrypting with the server's public key
		
		
		//Signing the random number
		byte[] signedNumber = sign(r_A, c_privateKey);
		
		//Send the encrypted number and the signed number to the server and reading the encrypted number(r_B) and signature from server
		sendReadServer(s_ip, s_port, cypherNumber,signedNumber);
		
		
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

	
	public static void sendReadServer(String serverIP, int serverPort, byte[] cypherN, byte[] signedN) throws UnknownHostException, IOException{
		Socket clientSocket = new Socket(serverIP,serverPort); //Opens communication at the Server IP and Server Port as set in the argument list
        
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());//Create a new DataInputStream Object to read the encrypted message and the sign sent from server
        
        String cypherNS = cypherN.toString(); //generate string equivalents of cypherN and signedN to write to out data stream using UTF
        String signedNS = signedN.toString();
        System.out.print("\nSending to Server: \n");
        out.writeUTF(cypherNS); //Sending string to out Data Stream
        out.writeUTF(signedNS);//Sending string to out Data Stream
        
        String cypherSer = in.readUTF();//encrypted message from the server
        System.out.print("Message from client:" + cypherSer);
        String signedSer = in.readUTF();//signature from the server
        
	}
		
}