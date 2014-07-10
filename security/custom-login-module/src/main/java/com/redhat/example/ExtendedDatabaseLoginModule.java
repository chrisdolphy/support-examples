package com.redhat.example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import org.jboss.logging.Logger;

import org.jboss.security.Base64Utils;
import org.jboss.security.auth.spi.DatabaseServerLoginModule;

 
public class ExtendedDatabaseLoginModule extends DatabaseServerLoginModule {

    private Logger log = Logger.getLogger(getClass());

	private static final String ENCRYPTION_KEY = "encryptionKey";
	private static final String ENCRYPTION_IV = "encryptionIV";
	private static final String ENCRYPTION_SALT = "encryptionSalt";
	
	protected String key;
	protected byte[] iv;
	protected String salt;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		// TODO Auto-generated method stub
		addValidOptions(new String[]{ ENCRYPTION_KEY, ENCRYPTION_IV, ENCRYPTION_SALT });
		super.initialize(subject, callbackHandler, sharedState, options);
		
		key = (String) options.get(ENCRYPTION_KEY);		
		String ivString = (String) options.get(ENCRYPTION_IV);
		iv = Base64Utils.fromb64(ivString);
		
		System.out.println("iv" + iv.length + " " + iv.toString());
		salt = (String) options.get(ENCRYPTION_SALT);
	}
	
	@Override
	protected String convertRawPassword(String rawPassword) {
		try {
			SecretKey skey = getSecretKey();
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));
	        //getting error here
	        byte[] original = cipher.doFinal(Base64Utils.fromb64(rawPassword));
	        return new String(original);
	    } catch (IllegalBlockSizeException e) {
	        log.error("Error with ExtendedDatabaseLoginModule", e);
	    } catch (BadPaddingException e) {
	        log.error("Error with ExtendedDatabaseLoginModule", e);
	    } catch (InvalidKeyException e) {
	        log.error("Error with ExtendedDatabaseLoginModule", e);
	    } catch (NoSuchAlgorithmException e) {
	        log.error("Error with ExtendedDatabaseLoginModule", e);
	    } catch (NoSuchPaddingException e) {
	        log.error("Error with ExtendedDatabaseLoginModule", e);
	    } catch (InvalidKeySpecException e) {
	        log.error("Error with ExtendedDatabaseLoginModule", e);
		} catch (InvalidAlgorithmParameterException e) {
	        log.error("Error with ExtendedDatabaseLoginModule", e);
		}
		return "";
	}
	
	protected byte[] encrypt(String plaintext) throws GeneralSecurityException, UnsupportedEncodingException {
			SecretKey skey = getSecretKey();

	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(iv));
			byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));

	        return ciphertext;
	}

	private SecretKey getSecretKey() throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 128);
		SecretKey tmp = factory.generateSecret(spec);			
		
		SecretKey skey = new SecretKeySpec(tmp.getEncoded(), "AES");
		return skey;
	}
	
	public static void main(String[] args) throws GeneralSecurityException, IOException {
		ExtendedDatabaseLoginModule module = new ExtendedDatabaseLoginModule();
		module.key = args[0];
		module.salt = args[1];
		module.iv = Base64Utils.fromb64(args[2]);
		byte[] encrypted = module.encrypt(args[3]);
		
		System.out.println("encrypted = [" + Base64Utils.tob64(encrypted) + "]");
		System.out.println("iv = [" + Base64Utils.tob64(module.iv) + "]");
		
	}

}