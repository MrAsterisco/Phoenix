package phoenix.base;

import java.io.Serializable;
import java.util.Random;

import phoenix.tools.PasswordHash;

/**
 * Rappresenta un utente del sistema.
 * 
 * @discussion Questa classe viene generata dal Server e restituita al Client, quando si effettua un login.
 * E' possibile distinguere un User con sessione ed uno senza grazie al metodo isLoggedIn.
 * 
 * @author Alex
 *
 */
public class User implements Serializable {
	
	private static final long serialVersionUID = -6459826180481031959L;
	
	private String username;
	private String email;
	private String name;
	private String surname;
	private String hashedPassword;
	private String saltPassword;
	private SessionToken session = null;
	
	private GPSPosition currentPosition;
	
	/**
	 * La loadedCar contiene un collegamento ad un'istanza di Car
	 * se questo User aveva un'auto collegata già prima che si collegasse al sistema
	 * (presumibilmente il risultato di un'altra sessione eseguita in precedenza).
	 */
	private Car loadedCar = null;
	
	public static final GPSPosition bottomRight = new GPSPosition(44.389825, 9.010487);
	public static final GPSPosition topLeft = new GPSPosition(44.415457, 8.890324);
	
	public User(String username, String email, String name, String surname, String hashedPassword, String saltPassword) {
		this.username = username;
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.hashedPassword = hashedPassword;
		this.saltPassword = saltPassword;
		
		generateRandomPosition();
	}
	
	public User(String username, String email, String name, String surname, String hashedPassword, String saltPassword, SessionToken session) {
		this(username, email, name, surname, hashedPassword, saltPassword);
		this.session = session;
	}
	
	/**
	 * Genera una posizione random all'interno di un range.
	 */
	private void generateRandomPosition() {
		Random random = new Random();
		this.currentPosition = new GPSPosition(getRandomCoordinate(random, bottomRight.getLatitude(), topLeft.getLatitude()), getRandomCoordinate(random, bottomRight.getLongitude(), topLeft.getLongitude()));
	}
	
	public boolean isLoggedIn() {
		return (this.session != null);
	}
	
	/**
	 * Controlla la validità della password specificata.
	 * 
	 * @param password Una password in chiaro da provare
	 * @return true se la password è valida
	 */
	public boolean canLoginWithCredentials(String password) {
		PasswordHash hash = new PasswordHash(password, this.saltPassword);
		return (hash.hash().equals(this.hashedPassword));
	}
	
	@Override
	public String toString() {
		return this.username + " (" + this.name + " " + this.surname + " - " + this.email + ") <Attivo: " + isLoggedIn() + ">";
	}
	
	public String getFullName() {
		return this.name + " " + this.surname;
	}
	
	private Double getRandomCoordinate(Random random, Double min, Double max) {
		Double randomNum = random.nextDouble();
		randomNum *= (max - min);
		randomNum += min;
		return randomNum;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public SessionToken getSessionToken() {
		return this.session;
	}
	
	public void setSessionToken(SessionToken session) {
		this.session = session;
	}
	
	public GPSPosition getCurrentPosition() {
		return this.currentPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	public Car getLoadedCar() {
		return this.loadedCar;
	}
	
	public void setLoadedCar(Car car) {
		this.loadedCar = car;
	}
	
}
