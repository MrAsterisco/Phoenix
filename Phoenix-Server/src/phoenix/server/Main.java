package phoenix.server;

import phoenix.base.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

/**
 * Programma Server di Phoenix Car Sharing.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class Main {

	/**
	 * Il nome del servizio sul registro RMI
	 */
	private static String RegistryName = "PhoenixServer";
	
	/**
	 * Collegamento all'istanza Server rappresentata con questo processo.
	 */
	private static Server server = null;
	
	public static void main(String[] args) {
		System.out.println("== PHOENIX SERVER version 1.0 ==\n\nAvvio il server…");
		
		try {
			server = new PhoenixServer();
		} catch (RemoteException | ClassNotFoundException | SQLException e1) { 
			e1.printStackTrace();
			
			System.out.println("\n\nERRORE IRREVERSIBILE: non è stato possibile avviare il Server. L'applicazione verrà terminata.");
			return;
		}
		
		System.out.println("Registro il servizio Phoenix…");
		
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e1) { }
		
		try {
			LocateRegistry.getRegistry().rebind(RegistryName, server);
		} catch (RemoteException e) {
			e.printStackTrace();
			
			System.out.println("\n\nERRORE IRREVERSIBILE: non è stato possibile registrare il servizio Phoenix. L'applicazione verrà terminata.");
			return;
		}
		
		System.out.println("Servizio registrato ed attivo.\n\n");
		
		Scanner input = new Scanner(System.in);
		
		while (true) {
			System.out.println("== PHOENIX SERVER ==\n\n1. Mostra riepilogo parcheggi\n\n2. Mostra utenti collegati\n\n3. Mostra utenti registrati\n\n4. Mostra richieste\n\n5. Mostra parco auto\n\n6. Mostra tipi di auto\n\n7. Interrompi servizio");
			
			System.out.print("\n> ");
			int selection = input.nextInt();
			
			switch (selection) {
			case 1:
				printParkingLots();
				break;
				
			case 2:
				printConnectedUsers();
				break;
				
			case 3:
				printAllUsers();
				break;
				
			case 4:
				printRequests();
				break;
				
			case 5:
				printCars();
				break;
				
			case 6:
				printTypes();
				break;
				
			case 7:
				System.out.println("Servizio interrotto. L'applicazione verrà terminata.");
				input.close();
				return;
				
			default:
				System.out.println("Opzione non disponibile. Inserisci un numero compreso tra 1 e 5.");
				break;
			}
		}
	}
	
	public static void printTypes() {
		System.out.println("\n\n== PHOENIX SERVER: Riepilogo categorie ==");
		try {
			for (Map.Entry<Integer, CarType> type : server.getTypes().entrySet()) {
				System.out.println(type.getValue().getName() + " <" + type.getKey() + ">");
			}
		} catch (RemoteException e) { }
		
		waitForEnterKey();
	}
	
	public static void printAllUsers() {
		System.out.println("\n\n== PHOENIX SERVER: Riepilogo utenti registrati ==");
		try {
			for (Map.Entry<String, User> user : server.getAllUsers().entrySet()) {
				System.out.println(user.getValue().toString());
			}
		} catch (RemoteException e) { }
		
		waitForEnterKey();
	}
	
	public static void printRequests() {
		System.out.println("\n\n== PHOENIX SERVER: Riepilogo richieste in attesa ==");
		try {
			for (Map.Entry<String, CarRequest> request : server.getCarRequests().entrySet()) {
				System.out.println(request.getValue().toString());
			}
		} catch (RemoteException e) { }
		
		waitForEnterKey();
	}
	
	public static void printConnectedUsers() {
		System.out.println("\n\n== PHOENIX SERVER: Riepilogo utenti collegati ==");
		try {
			for (Map.Entry<String, SessionToken> session : server.getSessions().entrySet()) {
				System.out.println(session.getValue().toString());
			}
		} catch (RemoteException e) { }
		
		waitForEnterKey();
	}
	
	public static void printParkingLots() {
		System.out.println("\n\n== PHOENIX SERVER: Riepilogo parcheggi ==");
		try {
			for (Map.Entry<Integer, ParkingLot> type : server.getParkingLots().entrySet()) {
				System.out.println(type.getValue().toString());
			}
		} catch (RemoteException e) { }
		
		waitForEnterKey();
	}
	
	public static void printCars() {
		System.out.println("\n\n== PHOENIX SERVER: Parco auto ==");
		try {
			for (Map.Entry<Integer, Car> car : server.getCars().entrySet()) {
				System.out.println(car.getValue().toString());
			}
		} catch (RemoteException e) { }
		
		waitForEnterKey();
	}
	
	// Attende la pressione del tasto Invio prima di cancellare la schermata
	private static void waitForEnterKey() {
		System.out.println("\n\nPremi Invio per tornare al menù.");
		try {
			System.in.read();
		} catch (IOException e) { }
	}
}
