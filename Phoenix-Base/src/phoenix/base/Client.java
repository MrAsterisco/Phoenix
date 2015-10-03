package phoenix.base;

import java.rmi.RemoteException;
import java.util.HashMap;

import phoenix.exceptions.InvalidCredentialsException;
import phoenix.exceptions.UnexistingSessionException;
import phoenix.exceptions.UnexistingUserException;
import phoenix.exceptions.UserAlreadyHaveCarException;
import phoenix.exceptions.UserAlreadyRegisteredException;
import phoenix.exceptions.UserHaveNoCarException;

/**
 * Modella il comportamento di un Client.
 * 
 * Il Client è l'utente del servizio, ovvero l'applicativo con cui il cliente reale accede al sistema.
 * 
 * Il Client si deve occupare di tenere collegamenti all'auto attualmente in uso dall'utente ed all'utente stesso.
 * Inoltre, deve offrire azioni per eseguire login, logout, registrazione, ricerca di auto e conferma consegna.
 * 
 * Fornisce anche alcuni metodi di convenience per ottenere i tipi di auto ed i parcheggi disponibili sul Server.
 * 
 * @author Alessio Moiso
 *
 */
public interface Client {
	
	/** == Dati == **/
	
	/**
	 * Restituisce l'utente attualmente collegato a questo Client.
	 * 
	 * @return Uno User se c'è un utente collegato, altrimenti Null se il Client non è autenticato
	 * @throws RemoteException
	 */
	public User getCurrentUser() throws RemoteException;
	
	/**
	 * Restituisce l'auto attuale dell'utente attualmente collegato a questo Client.
	 * 
	 * @return Un Car se c'è un utente collegato ed ha un auto, altrimenti Null
	 * @throws RemoteException
	 */
	public Car getCurrentCar() throws RemoteException;
	
	/** == Utenti & Sessioni == **/
	
	/**
	 * Controlla se l'utente è autenticato o meno.
	 * 
	 * @return true se l'utente si è già autenticato sul Server.
	 * @throws RemoteException
	 */
	public boolean isLoggedIn() throws RemoteException;
	
	/**
	 * Esegue il login sul Server, usando le credenziali legate a questo Client.
	 * 
	 * @discussion Le credenziali vengono inserite dal chiamante alla creazione dell'oggetto Client.
	 * 
	 * @throws RemoteException
	 * @throws InvalidCredentialsException in caso di credenziali errate
	 * @throws UnexistingUserException in caso di utente inesistente
	 */
	public void login() throws RemoteException, InvalidCredentialsException, UnexistingUserException;
	
	/**
	 * Esegue il logout dal Server dell'utente attualmente collegato.
	 * 
	 * @throws RemoteException
	 * @throws UnexistingSessionException in caso di sessione già scaduta o inesistente.
	 */
	public void logout() throws RemoteException, UnexistingSessionException;
	
	/**
	 * Invia una richiesta di registrazione al Server, usando le credenziali legate a questo Client.
	 * 
	 * @discussion Le credenziali vengono inserite dal chiamante alla creazione dell'oggetto Client.
	 * 
	 * @throws RemoteException
	 * @throws UserAlreadyRegisteredException in caso di utente già registrato
	 */
	public void register() throws RemoteException, UserAlreadyRegisteredException;
	
	/** == Azioni == **/
	
	/**
	 * Invia una richiesta di conferma consegna auto nel parcheggio specificato al Server.
	 * 
	 * @param parkingLotId: L'ID del parcheggio in cui si è consegnata l'auto.
	 * @throws RemoteException
	 * @throws UserHaveNoCarException in caso di utente che non ha nessun'auto collegata
	 * @throws UnexistingSessionException in caso di sessione già scaduta o inesistente
	 */
	public void parkCar(Integer parkingLotId) throws RemoteException, UserHaveNoCarException, UnexistingSessionException;
	
	/**
	 * Invia una richiesta di ricerca auto del tipo specificato entro il range specificato.
	 * 
	 * @param type: L'ID del tipo di auto che si sta cercando
	 * @param range: Il range di parcheggi entro cui cercare l'auto
	 * @throws RemoteException
	 * @throws UserAlreadyHaveCarException in caso di utente che ha già un'auto assegnata
	 * @throws UnexistingSessionException in caso di sessione già scaduta o inesistente
	 */
	public void searchCar(Integer type, Double range) throws RemoteException, UserAlreadyHaveCarException, UnexistingSessionException;
	
	/** == Dati == **/
	
	/**
	 * Restituisce l'elenco dei parcheggi caricati dal Server
	 * 
	 * @return Un'HashMap contentente tutti i parcheggi legati al loro ID.
	 * @throws RemoteException
	 */
	public HashMap<Integer, CarType> getCarTypes() throws RemoteException;
	
	public HashMap<Integer, ParkingLot> getParkingLots() throws RemoteException;
	
}
