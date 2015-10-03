package phoenix.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import phoenix.base.Car;
import phoenix.base.CarRequest;
import phoenix.base.CarType;
import phoenix.base.Client;
import phoenix.base.ClientCallback;
import phoenix.base.ParkRequest;
import phoenix.base.ParkingLot;
import phoenix.base.Server;
import phoenix.base.User;
import phoenix.exceptions.InvalidCredentialsException;
import phoenix.exceptions.UnexistingSessionException;
import phoenix.exceptions.UnexistingUserException;
import phoenix.exceptions.UserAlreadyHaveCarException;
import phoenix.exceptions.UserAlreadyRegisteredException;
import phoenix.exceptions.UserHaveNoCarException;

/**
 * Implementazione di Client per Phoenix Car Sharing.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class PhoenixClient extends UnicastRemoteObject implements Client {

	private static final long serialVersionUID = 473281735438128L;
	
	/**
	 * Collegamento al Server remoto
	 */
	private Server remoteServer;
	
	/**
	 * Username dell'utente rappresentato
	 */
	private String username;
	/**
	 * Password dell'utente rappresentato
	 */
	private String password;
	
	/**
	 * Nome dell'utente rappresentato
	 */
	private String name;
	/**
	 * Cognome dell'utente rappresentato
	 */
	private String surname;
	/**
	 * Email dell'utente rappresentato
	 */
	private String email;
	
	/**
	 * Collegamento allo User autenticato
	 */
	private User currentUser;
	/**
	 * Collegamento al Callback per richieste asincrone al Server
	 */
	private ClientCallback callback;
	
	/**
	 * Instanzia un nuovo Client a partire da un Server remoto
	 * 
	 * @discussion Non è possibile utilizzare questo costruttore dall'esterno. Un utente deve contenere più informazioni.
	 * 
	 * @param remoteServer: Un Server remoto
	 * @throws RemoteException
	 */
	private PhoenixClient(Server remoteServer) throws RemoteException {
		super();
		
		this.remoteServer = remoteServer;
	}
	
	/**
	 * Istanzia un nuovo Client a partire dalle informazioni necessarie per eseguire il login dell'utente.
	 * 
	 * @param remoteServer: Un Server remoto
	 * @param username: L'username dell'utente
	 * @param password: La password (in chiaro) dell'utente
	 * @throws RemoteException
	 */
	protected PhoenixClient(Server remoteServer, String username, String password) throws RemoteException {
		this(remoteServer);
		
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Istanzia un nuovo Client a partire dalle informazioni necessarie per registrare un utente al sistema.
	 * 
	 * @param remoteServer: Un Server remoto
	 * @param username: L'username dell'utente
	 * @param password: La password dell'utente
	 * @param email: L'email dell'utente
	 * @param name: Il nome dell'utente
	 * @param surname: Il cognome dell'utente
	 * @throws RemoteException
	 */
	protected PhoenixClient(Server remoteServer, String username, String password, String email, String name, String surname) throws RemoteException {
		this(remoteServer, username, password);
		
		this.name = name;
		this.surname = surname;
		this.email = email;
	}
	
	/**
	 * Ottiene l'istanza di User che rappreenta l'utente collegato a questo Client
	 */
	public User getCurrentUser() {
		return this.currentUser;
	}

	/**
	 * Ottiene l'auto attualmente collegata a questo utente.
	 * 
	 * @discussion Se l'utente è stato caricato dal Database con un'auto già collegata
	 * questo metodo ritornerà tale valore, chiamando getLoadedCar() sull'istanza di User.
	 * In caso contrario, l'auto attualmente collegata (se esiste) si trova nel Callback
	 * che abbiamo inviato al Server durante la richiesta di ricerca auto.
	 * 
	 * @return null se non c'è nessun'auto collegata, altrimenti un'instanza di Car
	 * @throws RemoteException 
	 */
	@Override
	public Car getCurrentCar() throws RemoteException {
		if (this.currentUser == null) return null;
		if (this.callback == null) return this.currentUser.getLoadedCar();
		
		return this.callback.getCar();
	}

	/**
	 * Determina se questo Client è attualmente autenticato con il Server.
	 * 
	 * @discussion Questo metodo non effettua alcun controllo sulla validità della sessione.
	 * 
	 * @return true se il Client è autenticato.
	 */
	@Override
	public boolean isLoggedIn() {
		if (this.currentUser == null) return false;
		return this.currentUser.isLoggedIn();
	}

	/**
	 * Esegue il login dell'utente attualmente legato a questa istanza di Client.
	 *
	 * @discussion Il risultato del login lato Server è un'istanza di User, che viene impostata nella proprietà currentUser.
	 *
	 * @throws InvalidCredentialsException in caso di credenziali non valide
	 * @throws RemoteException
	 * @throws UnexistingUserException in caso di utente inesistente
	 */
	@Override
	public void login() throws InvalidCredentialsException, RemoteException, UnexistingUserException {
		try {
			this.currentUser = this.remoteServer.login(this.username, this.password);
		} catch (RemoteException e) {
			if (e.getCause() instanceof InvalidCredentialsException) {
				throw new InvalidCredentialsException();
			}
			else if (e.getCause() instanceof UnexistingUserException) {
				throw new UnexistingUserException();
			}
			else {
				throw e;
			}
		}
	}

	/**
	 * Esegue il logout dell'utente attualmente legato a questa istanza di Client.
	 * 
	 * @throws UnexistingSessionException in caso di sessione già invalidata o scaduta
	 * @throws RemoteException
	 */
	@Override
	public void logout() throws UnexistingSessionException, RemoteException {
		try {
			this.remoteServer.cancelRequest(this.currentUser.getSessionToken());
			this.remoteServer.logout(this.currentUser.getSessionToken());
			this.currentUser = null;
		} catch (RemoteException e) {
			if (e.getCause() instanceof UnexistingSessionException) {
				throw new UnexistingSessionException();
			}
			else {
				throw e;
			}
		}
	}

	/**
	 * Registra un utente al sistema, usando i dati dell'utente attualmente legato a questa istanza di Client.
	 * 
	 * @throws UserAlreadyRegisteredException in caso di utente con stesso username già registrato
	 * @throws RemoteException
	 */
	@Override
	public void register() throws UserAlreadyRegisteredException, RemoteException {
		try {
			this.remoteServer.register(this.username, this.password, this.email, this.name, this.surname);
		} catch (RemoteException e) {
			if (e.getCause() instanceof UserAlreadyRegisteredException) {
				throw new UserAlreadyRegisteredException();
			}
			else {
				throw e;
			}
		}
	}

	/**
	 * Invia una richiesta di conferma riconsegna auto.
	 * 
	 * @param parkingLotId: L'ID del parcheggio nel quale si è lasciata l'auto
	 * @throws RemoteException
	 * @throws UserHaveNoCarException in caso di utente che non ha alcuna auto collegata
	 */
	@Override
	public void parkCar(Integer parkingLotId) throws RemoteException, UserHaveNoCarException, UnexistingSessionException {
		Car currentCar = this.getCurrentCar();
		
		if (currentCar == null) {
			throw new UserHaveNoCarException();
		}
		
		this.callback = new PhoenixClientCallback(currentCar);
		try {
			this.remoteServer.parkCar(new ParkRequest(this.currentUser.getSessionToken(), this.callback, parkingLotId, currentCar));
		} catch (UnexistingSessionException e) {
			if (e.getCause() instanceof UnexistingSessionException) {
				throw new UnexistingSessionException();
			}
			else {
				throw e;
			}
		}
	}

	/**
	 * Invia una richiesta di ricerca di un'auto entro un certo raggio.
	 * 
	 * @param type: Il tipo di auto da cercare
	 * @param range: Il raggio entro cui cercare auto nei parcheggi disponibili
	 */
	@Override
	public void searchCar(Integer type, Double range) throws UserAlreadyHaveCarException, RemoteException, UnexistingSessionException {
		if (this.getCurrentCar() != null) {
			throw new UserAlreadyHaveCarException();
		}
		
		this.callback = new PhoenixClientCallback();
		
		try {
			this.remoteServer.searchCar(new CarRequest(this.currentUser.getSessionToken(), this.callback, type, this.currentUser.getCurrentPosition(), range));
		} catch (RemoteException e) {
			if (e.getCause() instanceof UnexistingSessionException) {
				throw new UnexistingSessionException();
			}
			else {
				e.printStackTrace();
				throw e;
			}
		}
		
	}

	/**
	 * Ottiene un elenco dei tipi di auto disponibili.
	 * 
	 * @throws RemoteException
	 * @return Un'HashMap con i tipi di auto.
	 */
	@Override
	public HashMap<Integer, CarType> getCarTypes() throws RemoteException {
		return this.remoteServer.getTypes();
	}

	/**
	 * Ottiene un elenco dei parcheggi registrati.
	 * 
	 * @throws RemoteException
	 * @return Un'HashMap con i parcheggi disponibili.
	 */
	@Override
	public HashMap<Integer, ParkingLot> getParkingLots() throws RemoteException {
		return new HashMap<Integer, ParkingLot>(this.remoteServer.getParkingLots());
	}

}
