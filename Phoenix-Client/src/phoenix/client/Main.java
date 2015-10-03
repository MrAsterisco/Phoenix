package phoenix.client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

import phoenix.base.Car;
import phoenix.base.CarType;
import phoenix.base.Client;
import phoenix.base.ParkingLot;
import phoenix.base.Server;
import phoenix.exceptions.InvalidCredentialsException;
import phoenix.exceptions.UnexistingSessionException;
import phoenix.exceptions.UnexistingUserException;
import phoenix.exceptions.UserAlreadyHaveCarException;
import phoenix.exceptions.UserAlreadyRegisteredException;
import phoenix.exceptions.UserHaveNoCarException;

/**
 * Programma Client di Phoenix Car Sharing.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class Main {

	public static String host = "localhost";
	
	public static void main(String[] args) {
		if (args.length == 1) {
			host = args[0];
		}
		
		Server server = connectToServer();
		if (server == null) {
			logStandardConnectionError(null);
			System.out.println("L'applicazione verrà terminata.");
			return;
		}
		
		Scanner input = new Scanner(System.in);
		
		// Menù principale
		while (true) {
			System.out.println("== PHOENIX CAR SHARING ==\n\n1. Login\n\n2. Registrati\n\n3. Esci");
			
			System.out.print("\n> ");
			int selection = input.nextInt();
			
			switch (selection) {
			case 1: // Login
				System.out.println("\n\n== PHOENIX CAR SHARING: Login ==\n\n");
				
				System.out.print("Username: ");
				String username = input.next();
				
				System.out.print("\nPassword: ");
				String password = input.next();
				
				Client client;
				try {
					client = new PhoenixClient(server, username, password);
					client.login();

					switchToLoggedInMenu(input, client);
				} catch (RemoteException e) {
					logStandardConnectionError(e);
				} catch (InvalidCredentialsException e) {
					System.out.println("\n\nERRORE: lo username o la password inseriti non sono corretti. Ricontrolla i dati e riprova.");
				} catch (UnexistingUserException e) {
					System.out.println("\n\nERRORE: non è stato trovato nessun utente con questo username.");
				}
				
				waitForEnterKey();
				break;
				
			case 2: // Registrazione
				System.out.println("\n\n== PHOENIX CAR SHARING: Registrazione ==\n\n");
				
				System.out.print("Username: ");
				String username1 = input.next();
				
				System.out.print("\nPassword: ");
				String password1 = input.next();
				
				System.out.print("\nEmail: ");
				String email = input.next();
				
				System.out.print("\nNome: ");
				String name = input.next();
				
				System.out.print("\nCognome: ");
				String surname = input.next();
				
				Client client1;
				try {
					client1 = new PhoenixClient(server, username1, password1, email, name, surname);
					client1.register();
					System.out.println("OPERAZIONE CONCLUSA CON SUCCESSO: l'utente è ora registrato a Phoenix Car Sharing.");
				} catch (RemoteException e) { 
					logStandardConnectionError(e);
				} catch (UserAlreadyRegisteredException e) {
					System.out.println("\n\nERRORE: lo username o l'email inseriti risultano già registrati. Controlla i dati e riprova.");
				}
				finally {
					client1 = null;
				}
				
				waitForEnterKey();
				break;
				
			case 3: // Esci
				System.out.println("A presto!");
				input.close();
				return;
				
			default:
				System.out.println("Opzione non disponibile. Inserisci un numero compreso tra 1 e 3.");
				break;
			}
		}
	}
	
	// Menù utente autenticato
	private static void switchToLoggedInMenu(Scanner input, Client client) throws RemoteException {
		while (true) {
			
			// Mostra il nome completo dell'utente.
			System.out.println("\n\n== PHOENIX CAR SHARING - " + client.getCurrentUser().getFullName() + " ==");
			try {
				// Se l'utente ha un'auto assegnata, la mostra
				Car currentCar = client.getCurrentCar();
				if (currentCar != null) {
					System.out.println("\n== AUTO ATTUALE: " + currentCar + " ==");
				}
			} catch (RemoteException e2) { }
			
			System.out.println("\n1. Cerca auto\n\n2. Conferma restituzione auto\n\n3. Logout");
			
			System.out.print("\n> ");
			int selection = input.nextInt();
			switch (selection) {
			case 1: // Cerca auto
				System.out.println("\n\n== PHOENIX CAR SHARING: Cerca un'auto - " + client.getCurrentUser().getFullName() + " ==\n\n Che tipo di auto stai cercando?\n\n");
				
				try {
					for (Map.Entry<Integer, CarType> type : client.getCarTypes().entrySet()) {
						System.out.println(type.getKey() + ". " + type.getValue().toString());
					}
					
				} catch (RemoteException e1) {
					e1.printStackTrace();
					logStandardConnectionError(e1);
				}
				
				System.out.print("\n> ");
				int selectedType = input.nextInt();
				
				System.out.print("\n\n Inserisci il raggio entro cui vuoi cercare le auto: ");
				Double range = input.nextDouble();
				
				System.out.println("\n\n Benissimo! Stiamo cercando un auto del tipo selezionato nel raggio selezionato…");
				
				try {
					client.searchCar(selectedType, range);
					
					// Attende finché il Server non ha contattato il callback, impostando il collegamento all'auto
					client.getCurrentCar();
					
					System.out.println("\n\n L'auto ti è stata assegnata con successo! Buon viaggio!");
				} catch (RemoteException e1) {
					e1.printStackTrace();
					logStandardConnectionError(e1);
				} catch (UserAlreadyHaveCarException e1) {
					System.out.println("\n\nERRORE: Ci risulta che tu sia già in possesso di un'auto. Non è al momento possibile noleggiare più di un'auto contemporaneamente. \nPer proseguire premi Invio. Nel menù che apparirà, scegli \"Conferma restituzione auto\" per segnalare la restituzione dell'auto attualmente in tuo possesso.");
				} catch (UnexistingSessionException e1) {
					System.out.println("\n\nERRORE: Questa sessione non è autorizzata ad eseguire operazioni. Chiudi il programma e riprova.");
					return;
				}
				
				waitForEnterKey();
				break;
				
			case 2: // Restituisci auto
				try {
					System.out.println("\n\n== PHOENIX CAR SHARING: Conferma restituzione auto - " + client.getCurrentUser().getFullName() + " ==\n\n Stai restituendo " + client.getCurrentCar() + "!\n\n");
				} catch (RemoteException e1) { }
				
				System.out.println(" Seleziona il parcheggio in cui hai lasciato l'auto:\n");
				
				try {
					for (Map.Entry<Integer, ParkingLot> parkingLot : client.getParkingLots().entrySet()) {
						System.out.println(parkingLot.getKey() + ". " + parkingLot.getValue().getName());
					}
					
				} catch (RemoteException e1) {
					e1.printStackTrace();
					logStandardConnectionError(e1);
				}
				
				System.out.print("\n> ");
				int selectedPark = input.nextInt();
				
				System.out.println("\n\n Benissimo! Abbiamo preso in carico la tua richiesta, ti preghiamo di attendere.");
				
				try {
					client.parkCar(selectedPark);
					
					// Attendiamo finché il Server non ha contattato il callback, impostando l'auto collegata a Null
					while (client.getCurrentCar() != null) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) { }
					}
					
					System.out.println("\n\n Hai confermato la consegna dell'auto! Grazie per aver viaggiato con Phoenix Car Sharing!");
				} catch (RemoteException e1) {
					e1.printStackTrace();
					logStandardConnectionError(e1);
				} catch (UserHaveNoCarException e1) {
					System.out.println("\n\nERRORE: Ci risulta che tu non sia in possesso di alcuna auto. Se ciò non è corretto, ti preghiamo di rivolgerti ad uno dei nostri punti d'assistenza.");
				} catch (UnexistingSessionException e1) {
					System.out.println("\n\nERRORE: Questa sessione non è autorizzata ad eseguire operazioni. Chiudi il programma e riprova.");
				}
				
				waitForEnterKey();
				
				break;
				
			case 3: // Logout
				try {
					client.logout();
					System.out.println("\nA presto!");
				} catch (RemoteException e) {
					e.printStackTrace();
					logStandardConnectionError(e);
				} catch (UnexistingSessionException e) {
					System.out.println("\n\nERRORE: Questa sessione non è autorizzata ad eseguire operazioni. Il logout verrà eseguito comunque.");
				}
				return;
				
			default:
				System.out.println("Opzione non disponibile. Inserisci un numero compreso tra 1 e 3.");
				break;
			}
		}
	}
	
	// Connessione statica al Server
	private static Server connectToServer() {
		Server server = null;
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			server = (Server)registry.lookup("PhoenixServer");
		} catch (RemoteException | NotBoundException e) { }
		return server;
	}
	
	// Attende la pressione del tasto Invio prima di cancellare la schermata
	private static void waitForEnterKey() {
		System.out.println("\n\nPremi Invio per tornare al menù.");
		try {
			System.in.read();
		} catch (IOException e) { }
	}
	
	// Scrive sulla Console il messaggio standard in caso di errore di connessione
	private static void logStandardConnectionError(RemoteException e) {
		String log = "\n\nERRORE: si è verificato un problema durante la comunicazione con il server.";
		if (e != null) {
			log += " " + e.getLocalizedMessage();
		}
		System.out.println(log);
	}

}
