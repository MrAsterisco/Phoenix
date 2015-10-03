package phoenix.callables;

import java.util.concurrent.Callable;

import phoenix.db.DbLayer;

/**
 * Rappresenta un generico Callable per Phoenix.
 * 
 * Contiene un collegamento al Database e le credenziali di un utente, che sono richieste sia da registrazione che da login.
 * 
 * @author Alessio Moiso
 * @version 1.0
 * @param <T> Il tipo di ritorno del Callable
 */
public abstract class GenericCallable<T> implements Callable<T> {
	
	final DbLayer dbLayer;
	final String username;
	final String password;
	
	public GenericCallable(DbLayer layer, String username, String password) {
		this.dbLayer = layer;
		this.username = username;
		this.password = password;
	}
	
}
