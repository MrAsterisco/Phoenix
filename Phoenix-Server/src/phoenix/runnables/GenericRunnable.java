package phoenix.runnables;

import java.util.concurrent.ConcurrentHashMap;

import phoenix.base.CarRequest;
import phoenix.base.ParkingLot;
import phoenix.db.DbLayer;

/**
 * Rappresenta un generico Runnable per Phoenix.
 * 
 * Contiene un collegamento al Database, l'elenco dei parcheggi e l'elenco delle richieste in attesa.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public abstract class GenericRunnable implements Runnable {

	final DbLayer dbLayer;
	final ConcurrentHashMap<Integer, ParkingLot> parkingLots;
	final ConcurrentHashMap<String, CarRequest> requests;
	
	public GenericRunnable(DbLayer layer, ConcurrentHashMap<Integer, ParkingLot> parkingLots, ConcurrentHashMap<String, CarRequest> requests) {
		this.dbLayer = layer;
		this.parkingLots = parkingLots;
		this.requests = requests;
	}

}
