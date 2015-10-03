package phoenix.base;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import phoenix.exceptions.InvalidCredentialsException;
import phoenix.exceptions.UnexistingSessionException;
import phoenix.exceptions.UnexistingUserException;
import phoenix.exceptions.UserAlreadyRegisteredException;

/**
 * Modella il comportamento di un Server.
 * 
 * Il Server è la macchina che si occupa di gestire il servizio e di comunicare con i Client.
 * 
 * Il Server si deve occupare di tenere collegamenti a tutte le sessioni attive, a tutti gli utenti registrati, ai parcheggi (ed il loro stato) ed a tutte le richieste di auto e consegne.
 * Inoltre, offre metodi per eseguire login, logout e registrazione di un'utente e consegna e ricerca di un'auto.
 * 
 * Considerato che il Server può decidere di ritardare le richieste, nel caso in cui non sia possibile soddisfarle, viene offerto anche un metodo che permette di annullare tutte le richieste in attesa.
 * E' sempre il Server ad occuparsi di rieseguire le richieste in attesa, quando possibile.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public interface Server extends Remote {
	
	/** == Utenti & Sessioni == **/
	
	/**
	 * Restituisce le sessioni attualmente attive.
	 * 
	 * @return Un'HashMap che contiene tutte le sessioni autenticate sul Server.
	 * @throws RemoteException
	 */
	public ConcurrentHashMap<String, SessionToken> getSessions() throws RemoteException;
	
	/**
	 * Restituisce tutti gli utenti registrati al Server.
	 * 
	 * @return Un'HashMap che contiene tutti gli utenti registrati sul Server.
	 * @throws RemoteException
	 */
	public HashMap<String, User> getAllUsers() throws RemoteException;
	
	/**
	 * Esegue il login di un utente, usando le credenziali fornite.
	 * 
	 * @param username: L'Username da utilizzare
	 * @param password: La password (in chiaro) da utilizzare
	 * @return Un User che rappresenta l'utente autenticato, se il login è riuscito
	 * @throws RemoteException
	 * @throws InvalidCredentialsException in caso di credenziali (username o password) non valide
	 * @throws UnexistingUserException in caso di utente non esistente
	 */
	public User login(String username, String password) throws RemoteException, InvalidCredentialsException, UnexistingUserException;
	
	/**
	 * Esegue il logout di un utente, invalidando tutte le sue richieste in attesa e cancellandone la sessione.
	 * 
	 * @param session Un token di sessione valido per l'utente
	 * @throws RemoteException
	 * @throws UnexistingSessionException in caso di sessione non valida o già scaduta
	 */
	public void logout(SessionToken session) throws RemoteException, UnexistingSessionException;
	
	/**
	 * Inizia la procedura di registrazione di un'utente con le informazioni fornite.
	 * 
	 * @param username: Lo username da utilizzare per l'utente
	 * @param password: La password da utilizzare per accedere
	 * @param email: Un indirizzo email
	 * @param name: Il nome dell'utente
	 * @param surname: Il cognome dell'utente
	 * @throws RemoteException
	 * @throws UserAlreadyRegisteredException in caso di utente già registrato (ovvero, utente con stesso username già presente nel sistema)
	 */
	public void register(String username, String password, String email, String name, String surname) throws RemoteException, UserAlreadyRegisteredException;
	
	/** == Comandi == **/
	
	/**
	 * Inizia la procedura per la conferma di consegna di un'auto da parte di un utente.
	 * 
	 * @param request Una ParkRequest che rappresenta la conferma di consegna auto
	 * @throws RemoteException
	 * @throws UnexistingSessionException in caso di sessione non valida o già scaduta
	 */
	public void parkCar(ParkRequest request) throws RemoteException, UnexistingSessionException;
	
	/**
	 * Inizia la procedura di ricerca di un'auto di un tipo specificato entro un certo raggio.
	 * 
	 * @param request Una CarRequest che rappresenta la richiesta di ricerca auto
	 * @throws RemoteException
	 * @throws UnexistingSessionException in caso di sessione non valida o già scaduta
	 */
	public void searchCar(CarRequest request) throws RemoteException, UnexistingSessionException;
	
	/** == Parcheggi == **/
	
	/**
	 * Ottiene un elenco di tutti i parcheggi disponibili, con allegata la loro situazione.
	 * 
	 * @return Un'HashMap con tutti i parcheggi.
	 * @throws RemoteException
	 */
	public ConcurrentHashMap<Integer, ParkingLot> getParkingLots() throws RemoteException;
	
	/** == Auto & Richieste == **/
	
	/**
	 * Ottiene un elenco di tutte le auto registrate nel sistema.
	 * 
	 * @discussion Questa funzione non tiene conto di auto attualmente in uso, ma riporta semplicemente tutte le auto registrate
	 * 
	 * @return Un'HashMap con tutte le auto registrate.
	 * @throws RemoteException
	 */
	public HashMap<Integer, Car> getCars() throws RemoteException;
	
	/**
	 * Ottiene un elenco di tutte le richieste attualmente in attesa.
	 * 
	 * @discussion Le CarRequest che vengono soddisfatte immediatamente non vengono aggiunte all'elenco di richieste.
	 * 
	 * @return Un'HashMap con tutte le richieste in attesa.
	 * @throws RemoteException
	 */
	public ConcurrentHashMap<String, CarRequest> getCarRequests() throws RemoteException;
	
	/**
	 * Annulla la richiesta eseguita dall'utente con un certo token di sessione.
	 * 
	 * @param session
	 * @throws RemoteException
	 */
	public void cancelRequest(SessionToken session) throws RemoteException;
	
	/** == Categorie == **/
	
	/**
	 * Ottiene un elenco dei tipi di auto disponibili nel sistema.
	 * 
	 * @return Un'HashMap con l'elenco dei tipi di auto.
	 * @throws RemoteException
	 */
	public HashMap<Integer, CarType> getTypes() throws RemoteException; 
	
}
