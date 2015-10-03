package phoenix.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import phoenix.base.Car;
import phoenix.base.CarRequest;
import phoenix.base.CarType;
import phoenix.base.ParkRequest;
import phoenix.base.ParkingLot;
import phoenix.base.Server;
import phoenix.base.SessionToken;
import phoenix.base.User;
import phoenix.callables.LoginCallable;
import phoenix.callables.RegisterCallable;
import phoenix.db.DbLayer;
import phoenix.exceptions.InvalidCredentialsException;
import phoenix.exceptions.UnexistingSessionException;
import phoenix.exceptions.UnexistingUserException;
import phoenix.exceptions.UserAlreadyRegisteredException;
import phoenix.runnables.ParkRunnable;
import phoenix.runnables.SearchRunnable;

/**
 * Implementazione di Server per Phoenix Car Sharing.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class PhoenixServer extends UnicastRemoteObject implements Server {
	
	private static final long serialVersionUID = 183721374324L;
	
	/**
	 * Numero massimo di thread contemporanei.
	 */
	private static int ThreadsNumber = 4;
	
	/**
	 * Sessioni attualmente attive sul Server.
	 */
	private ConcurrentHashMap<String, SessionToken> sessions;
	
	/**
	 * Richieste per auto attualmente in attesa.
	 */
	private ConcurrentHashMap<String, CarRequest> requests;
	
	private ExecutorService pool;
	private DbLayer dbLayer;
	
	protected PhoenixServer() throws RemoteException, ClassNotFoundException, SQLException {
		super();
		
		this.pool = Executors.newFixedThreadPool(ThreadsNumber);
		this.dbLayer = new DbLayer();
		
		this.sessions = new ConcurrentHashMap<String, SessionToken>();
		this.requests = new ConcurrentHashMap<String, CarRequest>();
	}

	/**
	 * Restituisce l'elenco delle sessioni attualmente
	 */
	@Override
	public ConcurrentHashMap<String, SessionToken> getSessions() {
		return this.sessions;
	}

	/**
	 * Esegue il login di un utente, partendo dalle credenziali specificate.
	 * 
	 * @param username: L'username con cui eseguire il login
	 * @param password: La password con cui accedere
	 * @throws InvalidCredentialsException in caso di credenziali errate
	 * @throws UnexistingUserException in caso di utente inesistente
	 * @return Un'istanza di User, se il login è riuscito
	 */
	@Override
	public User login(String username, String password) throws InvalidCredentialsException, UnexistingUserException {
		HashMap<String, User> users = null;
		try {
			users = getAllUsers();
		} catch (RemoteException e1) { }
		
		Future<User> result = this.pool.submit(new LoginCallable(this.dbLayer, username, password, users));
		
		try {
			User user = result.get();
			this.sessions.put(user.getSessionToken().getToken(), user.getSessionToken());
			return user;
		} catch (InterruptedException e) { } catch (ExecutionException e) {
			if (e.getCause() instanceof InvalidCredentialsException) {
				throw new InvalidCredentialsException();
			}
			else if (e.getCause() instanceof UnexistingUserException) {
				throw new UnexistingUserException();
			}
			else {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * Esegue il logout di una sessione.
	 * 
	 * @param session: Un SessionToken valido
	 * @throws UnexistingSessionException in caso di sessione non valida o già scaduta
	 */
	@Override
	public void logout(SessionToken session) throws UnexistingSessionException {
		if (this.sessions.remove(session.getToken()) == null) {
			throw new UnexistingSessionException();
		}
	}

	/**
	 * Inizia la procedura di registrazione di un utente, usando i parametri specificati.
	 * 
	 * @param username: L'username dell'utente da registrare
	 * @param password: La password (in chiaro) dell'utente da registrare
	 * @param email: L'email dell'utente da registrare
	 * @param name: Il nome dell'utente da registrare
	 * @param surname: Il cognome dell'utente da registrare
	 * @throws UserAlreadyRegisteredException in caso di utente già registrato
	 */
	@Override
	public void register(String username, String password, String email,
			String name, String surname) throws UserAlreadyRegisteredException {
		Future<Void> result = this.pool.submit(new RegisterCallable(this.dbLayer, username, password, email, name, surname));
		
		try {
			result.get();
		} catch (InterruptedException e) { } catch (ExecutionException e) {
			if (e.getCause() instanceof UserAlreadyRegisteredException) {
				throw new UserAlreadyRegisteredException();
			}
			else {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inizia la procedura per la conferma di consegna di un'auto.
	 * 
	 * @param request: La ParkRequest che rappresenta la conferma di consegna
	 * @throws UnexistingSessionException in caso di sessione non valida o già scaduta
	 */
	@Override
	public void parkCar(ParkRequest request) throws UnexistingSessionException {
		if (!this.sessions.containsKey(request.getSessionToken().getToken())) throw new UnexistingSessionException();
		
		this.pool.execute(new ParkRunnable(this.dbLayer, request, getParkingLots(), this.requests));
	}

	/**
	 * Inizia la procedura di ricerca di un'auto.
	 * 
	 * @param request: La CarRequest che rappresenta la ricerca di un'auto
	 * @throws UnexistingSessionException in caso di sessione non valida o già scaduta
	 */
	@Override
	public void searchCar(CarRequest request) throws UnexistingSessionException {
		if (!this.sessions.containsKey(request.getSessionToken().getToken())) throw new UnexistingSessionException();
		
		this.pool.execute(new SearchRunnable(this.dbLayer, request, getParkingLots(), this.requests));
	}

	/**
	 * Ottiene un elenco dei parcheggi registrati nel sistema.
	 * 
	 * @return Un'HashMap con l'elenco dei parcheggi
	 */
	@Override
	public ConcurrentHashMap<Integer, ParkingLot> getParkingLots() {
		ConcurrentHashMap<Integer, ParkingLot> parkingLots = new ConcurrentHashMap<Integer, ParkingLot>();
		
		try {
			ResultSet results = this.dbLayer.executeQuery("SELECT id, name, address, latitude, longitude, altitude, lots FROM \"ParkingLot\"");
			while (results.next()) {
				ParkingLot lot = new ParkingLot(results.getInt("id"), results.getString("name"), results.getString("address"), results.getDouble("latitude"), results.getDouble("longitude"), results.getDouble("altitude"), results.getInt("lots"));
				lot.addAll(getParkedCars(results.getInt("id")));
				parkingLots.put(results.getInt("id"), lot);
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			System.out.println("ERRORE: si è verificato un problema durante il caricamento dei parcheggi.");
		}
		
		return parkingLots;
	}
	
	/**
	 * Ottiene il Set delle auto parcheggiate in un parcheggio specificato.
	 * 
	 * @param parkingLot: L'ID del parcheggio
	 * @return Un Set di Car con le auto parcheggiate in questo parcheggio
	 * @throws SQLException
	 */
	private Set<Car> getParkedCars(Integer parkingLot) throws SQLException {
		Set<Car> cars = new HashSet<Car>();

		ResultSet results = this.dbLayer.executeQuery("SELECT car.id AS carId, car.name AS carName, color, plate, type.id AS typeId, type.name AS typeName FROM \"Car\" LEFT JOIN \"Type\" ON \"Type\".id = \"Car\".type WHERE currentParkingLot = ?" , parkingLot);
		
		while (results.next()) {
			cars.add(new Car(results.getInt("carId"), results.getString("carName"), results.getString("color"), results.getString("plate"), new CarType(results.getInt("typeId"), results.getString("typeName"))));
		}
		
		return cars;
	}

	/**
	 * Ottiene l'elenco delle richieste per auto in attesa.
	 * 
	 * @return Un'HashMap con le richieste per auto in attesa.
	 */
	@Override
	public ConcurrentHashMap<String, CarRequest> getCarRequests() {
		return this.requests;
	}

	/**
	 * Annulla la richiesta legata ad una certa chiave di sessione.
	 */
	@Override
	public void cancelRequest(SessionToken session) {
		this.requests.remove(session.getToken());
	}

	/**
	 * Ottiene l'elenco dei tipi registrati nel sistema.
	 * 
	 * @return Un'HashMap con l'elenco dei tipi.
	 */
	@Override
	public HashMap<Integer, CarType> getTypes() {
		HashMap<Integer, CarType> types = new HashMap<Integer, CarType>();
		
		try {
			ResultSet results = this.dbLayer.executeQuery("SELECT id, name FROM \"Type\"");
			while (results.next()) {
				types.put(results.getInt("id"), new CarType(results.getInt("id"), results.getString("name")));
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			System.out.println("ERRORE: si è verificato un problema durante il caricamento delle categorie.");
		}
		
		return types;
	}

	/**
	 * Ottiene l'elenco delle auto registrate nel sistema.
	 * 
	 * @discussion Questo metodo non tiene conto delle auto attualmente in uso.
	 * 
	 * @return Un'HashMap con il parco auto.
	 */
	@Override
	public HashMap<Integer, Car> getCars() {
		HashMap<Integer, Car> cars = new HashMap<Integer, Car>();
		
		try {
			ResultSet results = this.dbLayer.executeQuery("SELECT car.id AS carId, car.name AS carName, color, plate, type.id AS typeId, type.name AS typeName FROM \"Car\" LEFT JOIN \"Type\" ON \"Type\".id = \"Car\".type");
			
			while (results.next()) {
				cars.put(results.getInt("carId"), new Car(results.getInt("carId"), results.getString("carName"), results.getString("color"), results.getString("plate"), new CarType(results.getInt("typeId"), results.getString("typeName"))));
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			System.out.println("ERRORE: si è verificato un problema durante il caricamento delle auto.");
		}
		
		return cars;
	}

	/**
	 * Ottiene l'elenco degli utenti registrati al sistema.
	 * 
	 * @return Un'HashMap con l'elenco degli utenti registrati.
	 */
	@Override
	public HashMap<String, User> getAllUsers() throws RemoteException {
		HashMap<String, User> users = new HashMap<String, User>();
		
		try {
			ResultSet results = this.dbLayer.executeQuery("SELECT user.id AS userId, user.name AS usrName, surname, username, email, password, salt, car.id AS carId, car.name AS carName, car.color AS color, car.plate AS plate, type.id AS typeId, type.name AS typeName FROM \"User\" LEFT JOIN \"Car\" ON \"User\".currentCar = \"Car\".id  LEFT JOIN \"Type\" ON \"Type\".id = \"Car\".type");
			while (results.next()) {
				User user = new User(results.getString("username"), results.getString("email"), results.getString("usrName"), results.getString("surname"), results.getString("password"), results.getString("salt"));
				
				if (results.getString("carId") != null) {
					user.setLoadedCar(new Car(results.getInt("carId"), results.getString("carName"), results.getString("color"), results.getString("plate"), new CarType(results.getInt("typeId"), results.getString("typeName"))));
				}
				
				users.put(results.getString("username"), user);
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			System.out.println("ERRORE: si è verificato un problema durante il caricamento degli utenti.");
		}
		
		return users;
	}

}
