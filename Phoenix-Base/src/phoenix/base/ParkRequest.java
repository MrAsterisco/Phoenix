package phoenix.base;

import java.io.Serializable;

/**
 * Rappresenta una richiesta di conferma consegna auto da inviare al Server.
 * 
 * Contiene tutte le informazioni necessarie per ricostruire la richiesta lato Server, ovvero: il parcheggio in cui si è lasciata l'auto, la sessione dell'utente, un callback
 * da richiamare quando l'operazione è stata completata e l'ID dell'auto parcheggiata.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class ParkRequest implements Serializable {

	private static final long serialVersionUID = -2228775410123256892L;
	
	private SessionToken session;
	
	private Integer parkingLotId;
	private ClientCallback callback;
	private Car parkedCar;
	
	public ParkRequest(SessionToken session, ClientCallback callback, Integer parkingLotId, Car parkedCar) {
		this.session = session;
		this.callback = callback;
		this.parkingLotId = parkingLotId;
		this.parkedCar = parkedCar;
	}
	
	public SessionToken getSessionToken() {
		return this.session;
	}
	
	public Integer getParkingLotId() {
		return this.parkingLotId;
	}
	
	public Car getParkedCar() {
		return this.parkedCar;
	}
	
	public ClientCallback getCallback() {
		return this.callback;
	}
	
}
