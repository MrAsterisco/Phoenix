package phoenix.base;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Rappresenta un parcheggio del sistema.
 * 
 * Possiede un Set di Car che contiene l'elenco delle auto attualmente parcheggiate.
 * Implementa Iterable per facilitarne la lettura.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class ParkingLot implements Serializable, Iterable<Car> {
	
	private static final long serialVersionUID = 2837364325162712456L;
	
	private Integer id;
	private String name;
	private String address;
	private GPSPosition position;
	private Integer lots;
	private Set<Car> cars;
	
	public ParkingLot(Integer id, String name, String address, Double latitude, Double longitude, Double altitude, Integer lots) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.position = new GPSPosition(latitude, longitude, altitude);
		this.lots = lots;
		this.cars = new HashSet<Car>();
	}
	
	public void add(Car car) {
		this.cars.add(car);
	}
	
	public void addAll(Set<Car> cars) {
		this.cars.addAll(cars);
	}
	
	public void remove(Car car) {
		this.cars.remove(car);
	}
	
	public void removeAll(Set<Car> cars) {
		this.cars.removeAll(cars);
	}
	
	public boolean isInRange(GPSPosition clientPosition, Double searchRange) {
		return this.position.getDistance(clientPosition) < searchRange;
	}
	
	public boolean contains(Car car) {
		return this.cars.contains(car);
	}
	
	@Override
	public String toString() {
		String result = this.name + " <" + this.address + "> (" + this.cars.size() + " auto - " + this.lots + " posti totali) {\n";
		
		for (Car car : this.cars) {
			result += "\n\t" + car.toString();
		}
		
		result += "\n}";
		
		return result;
	}
	
	public GPSPosition getPosition() {
		return this.position;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ParkingLot other = (ParkingLot) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public Iterator<Car> iterator() {
		return this.cars.iterator();
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
}
