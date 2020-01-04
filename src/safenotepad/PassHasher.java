package safenotepad;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.midp.io.Base64;

public class PassHasher {
	
	public byte[] hashedPassBytes = new byte[32];
	
	public PassHasher(byte[] password) throws NoSuchAlgorithmException, DigestException
	{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(password, 0, password.length);
		int res = messageDigest.digest(hashedPassBytes, 0, hashedPassBytes.length);
	}

	// testing
	public static void main(String[] args) {	
		try {
			PassHasher pass = new PassHasher("keh".getBytes());
			String encoded = Base64.encode(pass.hashedPassBytes, 0, pass.hashedPassBytes.length);
			System.out.println("pass: " + encoded);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DigestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
