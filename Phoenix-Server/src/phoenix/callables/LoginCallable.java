package phoenix.callables;

import java.util.Date;
import java.util.HashMap;

import phoenix.base.SessionToken;
import phoenix.base.User;
import phoenix.db.DbLayer;
import phoenix.exceptions.InvalidCredentialsException;
import phoenix.exceptions.UnexistingUserException;

/**
 * Implementa la procedura di login.
 * 
 * Si fa passare le credenziali dell'utente e la lista di tutti gli utenti registrati, che viene caricata dal Server.
 * Usando l'istanza di User che corrisponde allo Username specificato, prova ad eseguire il login e, se ci riesce, restituisce tale istanza.
 * Si occupa, inoltre, di aggiornare il DB con la data dell'ultimo accesso (anche se tale valore non viene effettivamente utilizzato nel programma).
 * 
 * @author Alessio Moiso
 * @version 1.1
 */
public class LoginCallable extends GenericCallable<User> {

	private HashMap<String, User> allUsers;
	
	public LoginCallable(DbLayer layer, String username, String password, HashMap<String, User> allUsers) {
		super(layer, username, password);
		this.allUsers = allUsers;
	}
	
	@Override
	public User call() throws Exception {
		User selectedUser = this.allUsers.get(this.username);
		
		if (selectedUser == null) throw new UnexistingUserException();
		
		if (selectedUser.canLoginWithCredentials(this.password)) {
			this.dbLayer.executeUpdate("UPDATE \"User\" SET lastLogin = ? WHERE username = ?", new Date(), this.username);
			selectedUser.setSessionToken(new SessionToken(this.username));
			
			return selectedUser;
		}
		
		throw new InvalidCredentialsException();
	}

}
