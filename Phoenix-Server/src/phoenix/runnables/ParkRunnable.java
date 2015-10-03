package phoenix.runnables;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import phoenix.base.CarRequest;
import phoenix.base.ParkRequest;
import phoenix.base.ParkingLot;
import phoenix.db.DbLayer;

/**
 * Implementa la procedura di conferma consegna di un'auto.
 * 
 * Tenta di aggiornare il Database: se l'operazione va a buon fine, rilascia l'attesa del Client impostando l'auto a null (tramite Callback).
 * Dopodiché, prova a soddisfare una delle richieste in attesa, se possibile, usando la macchina che è stata appena parcheggiata.
 * In caso non ci riesca, aggiunge definitvamente l'auto al Set di auto nel parcheggio.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class ParkRunnable extends GenericRunnable {

	private ParkRequest request;
	
	public ParkRunnable(DbLayer layer, ParkRequest request, ConcurrentHashMap<Integer, ParkingLot> parkingLots, ConcurrentHashMap<String, CarRequest> requests) {
		super(layer, parkingLots, requests);
		this.request = request;
	}

	@Override
	public void run() {
		try {
			this.dbLayer.executeUpdate("UPDATE \"Car\" SET currentParkingLot = ? WHERE id = ?", this.request.getParkingLotId(), this.request.getParkedCar().getId());
			this.dbLayer.executeUpdate("UPDATE \"User\" SET currentCar = NULL WHERE username = ?", this.request.getSessionToken().getUsername());
		} catch (SQLException e) {
			if (e.getErrorCode() == 19) {
				System.out.println("\n\nERRORE: non è stato possibile completare la richiesta (" + this.request + ") a causa di un problema durante l'aggiornamento del Database.");
			}
			else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.request.getCallback().setCar(null);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("\n\nERRORE: non è stato possibile completare la richiesta (" + this.request + ") a causa di un problema nella comunicazione con il client.");
		}
		
		ParkingLot park = this.parkingLots.get(this.request.getParkingLotId());
		
		for (CarRequest otherRequest : this.requests.values()) {
			Double distance = otherRequest.getCurrentPosition().getDistance(park.getPosition());
			
			if (distance < otherRequest.getSearchRange()) {
				if (otherRequest.getCarTypeId() == this.request.getParkedCar().getType().getId()) {
					System.out.println("\n\nINFO: è possibile soddisfare la richiesta (" + otherRequest +") considerate le mutate condizioni del sistema.");
					
					synchronized (this.requests) {
						if (this.requests.contains(otherRequest)) {
							
							try {
								this.request.getCallback().setCar(this.request.getParkedCar());
							} catch (RemoteException e) {
								e.printStackTrace();
								System.out.println("\n\nERRORE: non è stato possibile completare la richiesta (" + this.request + ") a causa di un problema nella comunicazione con il client.");
							}
							
							try {
								this.dbLayer.executeUpdate("UPDATE \"Car\" SET currentParkingLot = NULL WHERE id = ?", this.request.getParkedCar().getId());
								this.dbLayer.executeUpdate("UPDATE \"User\" SET currentCar = ? WHERE username = ?", this.request.getParkedCar().getId(), this.request.getSessionToken().getUsername());
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
		}
		
		park.add(this.request.getParkedCar());
	}

}
