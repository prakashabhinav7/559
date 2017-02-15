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
		
		//Key generation for the Client
		String absPath ="";
		
		PublicKey keyPublic = generatePublicKey(ALGORITHM); //Public component of the key pair
		PrivateKey keyPrivate = generatePrivateKey(ALGORITHM); //Private component of the key pair
		
		
		//Saving Client's Key Pair to File
		saveKeyPair(keyPrivate,keyPublic, absPath);

		
		//Loading Server's Public Key from file|||RECHECK THIS
		File filePublicKey = new File(absPath + "s_public.key");
		FileInputStream fis = new FileInputStream(absPath + "s_public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
		
		//Deserializing the public key
		PublicKey s_publicKey = deserializePublicKey(encodedPublicKey, ALGORITHM);
		System.out.print(s_publicKey.toString());
		
		//Encrypting the random number with public key of the SERVER
		byte[] cypherNumber = encrypt(r_A, s_publicKey); //// change keyPublic to the public key of server on file
		
		//Signing the random number
		byte[] signedNumber = sign(r_A, keyPrivate);
		
		//Send the encrypted number and the signed number to the server
		sendToServer(s_ip, s_port, cypherNumber,signedNumber);
		
		
	}
	
	
	
	public static byte[] randomNumber(){
		SecureRandom randNumber = new SecureRandom();
		byte[] ivInBytes = new byte[16]; // 128 bits converted to 16 bytes
		randNumber.nextBytes(ivInBytes);
		return ivInBytes;
		
	}
	
	
	public static PublicKey generatePublicKey(String ALGORITHM) throws NoSuchAlgorithmException, InvalidKeySpecException{
		
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM); //Specify the algorithm for key pair generation
		byte[] encodedPublicKey = null;
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		System.out.println(publicKey.toString());
		return publicKey;
	}
	
	public static PrivateKey generatePrivateKey(String ALGORITHM) throws NoSuchAlgorithmException, InvalidKeySpecException{
		
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM); //Specify the algorithm for key pair generation
		byte[] encodedPrivateKey = null;
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey =	keyFactory.generatePrivate(privateKeySpec);
		return privateKey;
	}
	
	public static void saveKeyPair(PrivateKey prikey, PublicKey pubkey, String path) throws IOException{
		FileOutputStream pbf = new FileOutputStream(path + "c_public.key"); //Writing client's public key to file
		byte[] kPub = pubkey.getEncoded();
		pbf.write(kPub);
		pbf.close();
		
		FileOutputStream prf = new FileOutputStream(path + "c_private.key"); //Writing client's private key to file
		byte[] kPri = prikey.getEncoded();
		prf.write(kPri);
		prf.close();
	}
	
	
    public static PublicKey deserializePublicKey(byte[] keyData, String algorithm) { 
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(keyData); 
        try { 
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm); 
            return keyFactory.generatePublic(pubSpec); 
        } catch (GeneralSecurityException e) { 
            throw new IllegalArgumentException("provided data could not be converted to a PublicKey for algorithm " 
                    + algorithm, e); 
        } 
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