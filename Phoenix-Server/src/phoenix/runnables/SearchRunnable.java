package phoenix.runnables;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import phoenix.base.Car;
import phoenix.base.CarRequest;
import phoenix.base.ParkingLot;
import phoenix.db.DbLayer;

/**
 * Implementa la procedura di ricerca di un'auto.
 * 
 * Filtra tutti i parcheggi in base alla distanza, usando il raggio specificato nella CarRequest.
 * Dopodiché cerca un'auto che corrisponda ai requisiti di tipo: se la trova, blocca l'accesso all'HashMap
 * dei parcheggi e restituisce l'auto al Client (tramite Callback).
 * Infine, aggiorna il Database per riflettere la nuova situazione.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class SearchRunnable extends GenericRunnable {

	private CarRequest request;
	
	public SearchRunnable(DbLayer layer, CarRequest request, ConcurrentHashMap<Integer, ParkingLot> parkingLots, ConcurrentHashMap<String, CarRequest> requestsQueue) {
		super(layer, parkingLots, requestsQueue);
		this.request = request;
	}
	
	@Override
	public void run() {
		ConcurrentHashMap<Integer, ParkingLot> parkingLotsNearby = parkingLotsInRange();
		
		if (parkingLotsNearby.size() == 0) {
			System.out.println("\n\nINFO: non è stato possibile soddisfare la richiesta (" + this.request + ") perché l'utente si trova troppo distante da qualunque parcheggio.");
			return;
		}
		
		for (Map.Entry<Integer, ParkingLot> entry : parkingLotsNearby.entrySet()) {
			for (Car car : entry.getValue()) {
				if (car.getType().getId().equals(request.getCarTypeId())) {
					synchronized (entry) {
						if (parkingLotsNearby.get(entry.getKey()).contains(car)) {
							
							try {
								this.request.getCallback().setCar(car);
							} catch (RemoteException e) {
								e.printStackTrace();
								System.out.println("\n\nERRORE: non è stato possibile completare la richiesta (" + this.request + ") a causa di un problema nella comunicazione con il client.");
							}
							
							entry.getValue().remove(car);
							
							try {
								this.dbLayer.executeUpdate("UPDATE \"Car\" SET currentParkingLot = NULL WHERE id = ?", car.getId());
								this.dbLayer.executeUpdate("UPDATE \"User\" SET currentCar = ? WHERE username = ?", car.getId(), this.request.getSessionToken().getUsername());
							} catch (SQLException e) {
								if (e.getErrorCode() == 19) {
									System.out.println("\n\nERRORE: non è stato possibile completare la richiesta (" + this.request + ") a causa di un problema durante l'aggiornamento del Database.");
								}
								else {
									e.printStackTrace();
								}
							}
							return;
						}
					}
				}
			}
			
			System.out.println("\n\nINFO: non è stato possibile soddisfare la richiesta (" + this.request + ") perché non vi sono auto disponibili che soddisfano i requisiti di ricerca. L'utente verrà messo in attesa.");
			this.requests.put(this.request.getSessionToken().getToken(), this.request);
		}
	}
	
	private ConcurrentHashMap<Integer, ParkingLot> parkingLotsInRange() {
		ConcurrentHashMap<Integer, ParkingLot> parkingLotsNearby = new ConcurrentHashMap<Integer, ParkingLot>();
		
		for (ParkingLot parkingLot : this.parkingLots.values()) {
			if (parkingLot.isInRange(this.request.getCurrentPosition(), this.request.getSearchRange())) {
				parkingLotsNearby.put(parkingLot.getId(), parkingLot);
			}
		}
		
		return parkingLotsNearby;
	}
	
}
