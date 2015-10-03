package phoenix.callables;

import java.sql.SQLException;

import phoenix.db.DbLayer;
import phoenix.exceptions.UserAlreadyRegisteredException;
import phoenix.tools.PasswordHash;
import phoenix.tools.RandomString;

/**
 * Implementa la procedura di registrazione di un utente.
 * 
 * Genera l'hash della password, quindi aggiunge l'utente al Database.
 * E' in grado di gestire eventuali errori sui vincoli di chiave: viene lanciata UserAlreadyRegisteredException.
 * 
 * @author Alessio Moiso
 * @version 1.2
 */
public class RegisterCallable extends GenericCallable<Void> {
	
	private String email;
	private String name;
	private String surname;
	
	public RegisterCallable(DbLayer layer, String username, String email, String password, String name, String surname) {
		super(layer, username, password);
		this.name = name;
		this.surname = surname;
		this.email = email;
	}
	
	@Override
	public Void call() throws Exception {
		RandomString randString = new RandomString(20);
		String salt = randString.nextString();
		
		PasswordHash hash = new PasswordHash(this.password, salt);
		
		try {
			this.dbLayer.executeUpdate("INSERT INTO \"User\" (username, name, surname, email, password, salt) VALUES (?, ?, ?, ?, ?, ?)", this.username, this.name, this.surname, this.email, hash.hash(), salt);
		}
		catch (SQLException e) {
			if (e.getErrorCode() == 19) {
				throw new UserAlreadyRegisteredException();
			}
			else {
				System.out.println("\n\nERRORE: non è stato possibile registrare l'utente.");
			}
		}
		
		return null;
	}

}
