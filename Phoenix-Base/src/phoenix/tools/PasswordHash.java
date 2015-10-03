package phoenix.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

/**
 * Esegue l'hash di una stringa, usando un certo sale.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class PasswordHash {
	
	private String password;
	private String salt;
	
	public PasswordHash(String password, String salt) {
		this.password = password;
		this.salt = salt;
	}
	
	public String hash() {
		MessageDigest sha256 = null;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
			byte[] passBytes = (this.password + this.salt).getBytes();
			byte[] passHash = sha256.digest(passBytes);
			return DatatypeConverter.printBase64Binary(passHash);
		} catch (NoSuchAlgorithmException e) { }
		return null;
	}
	
}
