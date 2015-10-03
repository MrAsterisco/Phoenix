package phoenix.base;

import java.io.Serializable;

/**
 * Rappresenta un'auto del servizio Car Sharing.
 * 
 * Contiene le stesse informazioni contenute nel database, ma fornisce implementazioni custom per hashCode e equals, in modo da consentirne l'utilizzo all'interno delle ConcurrentHashMap.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class Car implements Serializable {
	
	private static final long serialVersionUID = 5608354117035728504L;
	
	private Integer id;
	private String name;
	private String color;
	private String plate;
	private CarType type;
	
	public Car(Integer id, String name, String color, String plate, CarType type) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.plate = plate;
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public String getPlate() {
		return this.plate;
	}
	
	public CarType getType() {
		return this.type;
	}
	
	public ParkingLot getCurrentParkingLot() {
		return null;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plate == null) ? 0 : plate.hashCode());
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
		Car other = (Car) obj;
		if (plate == null) {
			if (other.plate != null)
				return false;
		} else if (!plate.equals(other.plate))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.name + " (" + this.color + ") <" + this.plate + "> ";
	}
}
