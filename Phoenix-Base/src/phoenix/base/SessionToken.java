package phoenix.base;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Rappresenta una sessione con relativo token.
 * 
 * @discussion Per generare un token univoco, viene utilizzata la classe UUID di Java.
 * Tale generazione non viene generalmente considerata molto sicura, soprattutto per la
 * trasmissione di valori tramite rete (a causa del limite di caratteri nei parametri GET).
 * In questo progetto si è scelto di utilizzarla comunque, sia perché non abbiamo problemi di limiti,
 * sia perché è più semplice da utilizzare.
 * 
 * Inoltre, sempre in un'ottica di semplificazione, a discapito della sicurezza, ogni SessionToken contiene
 * un riferimento allo username dell'utente.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class SessionToken implements Serializable {
	
	private static final long serialVersionUID = 6489828149752324689L;
	
	private String token;
	private String username;
	private Date date;
	
	public SessionToken(String username) {
		this.token = UUID.randomUUID().toString();
		this.username = username;
		this.date = new Date();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		SessionToken other = (SessionToken) obj;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	public String getToken() {
		return this.token;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public boolean equals(SessionToken obj) {
		return (this.token.equals(obj.token));
	}
	
	@Override
	public String toString() {
		return this.token + " <" + this.username + "> (" + this.date + ")";
	}
	
}
