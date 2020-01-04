package safenotepad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class StringCipher {

    PaddedBufferedBlockCipher encryptCipher;
    PaddedBufferedBlockCipher decryptCipher;

    byte[] buf = new byte[8];
    byte[] obuf = new byte[512];

    byte[] key = null;

    public StringCipher(){
        key = "SECRET_1SECRET_2SECRET_3".getBytes();
        InitCiphers();
    }
    public StringCipher(byte[] keyBytes){
        key = new byte[keyBytes.length];
        System.arraycopy(keyBytes, 0 , key, 0, keyBytes.length);
        InitCiphers();
    }

    private void InitCiphers(){
        encryptCipher = new PaddedBufferedBlockCipher(new DESedeEngine());
        encryptCipher.init(true, new KeyParameter(key));
        decryptCipher =  new PaddedBufferedBlockCipher(new DESedeEngine());
        decryptCipher.init(false, new KeyParameter(key));
    }

    public void ResetCiphers() {
        if(encryptCipher!=null)
            encryptCipher.reset();
        if(decryptCipher!=null)
            decryptCipher.reset();
    }

public void encrypt(InputStream in, OutputStream out)
throws ShortBufferException, 
        IllegalBlockSizeException,
        BadPaddingException,
        DataLengthException,
        IllegalStateException,
        InvalidCipherTextException
{
    try {
        int noBytesRead = 0;
        int noBytesProcessed = 0;

        while ((noBytesRead = in.read(buf)) >= 0) {
            noBytesProcessed =
                    encryptCipher.processBytes(buf, 0, noBytesRead, obuf, 0);
            out.write(obuf, 0, noBytesProcessed);
        }

        noBytesProcessed =
                 encryptCipher.doFinal(obuf, 0);

         out.write(obuf, 0, noBytesProcessed);

        out.flush();
    }
    catch (java.io.IOException e) {
        System.out.println(e.getMessage());
    }
}
    public void decrypt(InputStream in, OutputStream out)
    throws ShortBufferException, IllegalBlockSizeException,  BadPaddingException,
            DataLengthException, IllegalStateException, InvalidCipherTextException
    {
        try {

            int noBytesRead = 0;
            int noBytesProcessed = 0;

            while ((noBytesRead = in.read(buf)) >= 0) {
                    noBytesProcessed = decryptCipher.processBytes(buf, 0, noBytesRead, obuf, 0);
                    out.write(obuf, 0, noBytesProcessed);
            }
            noBytesProcessed = decryptCipher.doFinal(obuf, 0);
            out.write(obuf, 0, noBytesProcessed);

            out.flush();
        }
        catch (java.io.IOException e) {
             System.out.println(e.getMessage());
        }
    }
    
    // testing the class
    public static void main(String[] args) {
		try {
			String secretKey = "supersecret";
			byte[] secretKeyBytes = secretKey.getBytes("UTF-8");
	    	MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
	    	byte[] hashedKey = new byte[32];
	    	messageDigest.update(secretKeyBytes, 0, secretKey.length());
	    	messageDigest.update(hashedKey, 0, 32);
	    	
	    	byte[] hashedKey24 = Arrays.copyOfRange(hashedKey, 0, 24);
	    	StringCipher cs = new StringCipher(hashedKey24);
	    	String exampleString = "My super secret note";
	    	InputStream inputStream;
	    	System.out.println("Note: " + exampleString);
	    		    	
			// prepare
			inputStream = new ByteArrayInputStream(exampleString.getBytes("UTF-8"));
			OutputStream encodedOutputStream = new ByteArrayOutputStream();
			
			//encrypt
	    	cs.encrypt(inputStream, encodedOutputStream);
	    	System.out.println("Encoded note: " + encodedOutputStream.toString());
	    	
	    	//prepare
	    	ByteArrayOutputStream bais = (ByteArrayOutputStream) encodedOutputStream;
	    	byte[] outputBytes = bais.toByteArray();
	    	InputStream encodedInput = new ByteArrayInputStream(outputBytes);
	    	OutputStream decodedOutputStream = new ByteArrayOutputStream();
	    	
	    	//decrypt
	    	cs.decrypt(encodedInput, decodedOutputStream);
	    	System.out.println("Decoded note: " + decodedOutputStream.toString());
	    	
	    	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
