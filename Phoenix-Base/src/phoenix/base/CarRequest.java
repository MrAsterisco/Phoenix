package phoenix.base;

import java.io.Serializable;

/**
 * Rappresenta una richiesta per un'auto da inviare al Server.
 * 
 * Contiene tutte le informazioni necessarie per ricostruire la richiesta lato Server, ovvero: il tipo di auto che si sta cercando, la sessione dell'utente, un callback
 * da richiamare quando l'operazione è stata completata, la posizione dell'utente al momento della ricerca ed il range entro cui cercare auto.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class CarRequest implements Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((session == null) ? 0 : session.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarRequest other = (CarRequest) obj;
		if (session == null) {
			if (other.session != null)
				return false;
		} else if (!session.equals(other.session))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1302432870279844744L;
	
	private Integer type;
	
	private SessionToken session;
	private ClientCallback callback;
	
	private GPSPosition currentPosition;
	private Double searchRange;
	
	public CarRequest(SessionToken session, ClientCallback callback, Integer type, GPSPosition currentPosition, Double searchRange) {
		this.session = session;
		this.callback = callback;
		this.type = type;
		this.currentPosition = currentPosition;
		this.searchRange = searchRange;
	}
	
	public SessionToken getSessionToken() {
		return this.session;
	}
	
	public GPSPosition getCurrentPosition() {
		return this.currentPosition;
	}
	
	public Double getSearchRange() {
		return this.searchRange;
	}
	
	public Integer getCarTypeId() {
		return this.type;
	}
	
	public ClientCallback getCallback() {
		return this.callback;
	}
	
	@Override
	public String toString() {
		return this.session.toString() + ": " + this.type + ", " + this.currentPosition + ", " + this.searchRange;
	}

}
