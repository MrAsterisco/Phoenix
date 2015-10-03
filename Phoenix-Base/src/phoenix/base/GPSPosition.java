package phoenix.base;

import java.io.Serializable;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;

/**
 * Rappresenta un punto su una mappa, con latitudine, longitudine e altitudine.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public class GPSPosition implements Serializable {
	
	private static final long serialVersionUID = 8006800713668899013L;
	
	private Double latitude;
	private Double longitude;
	private Double altitude;
	
	public GPSPosition(Double latitude, Double longitude) {
		this(latitude, longitude, 0.0);
	}
	
	public GPSPosition(Double latitude, Double longitude, Double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public Double getLatitude() {
		return this.latitude;
	}
	
	public Double getLongitude() {
		return this.longitude;
	}
	
	public Double getAltitude() {
		return this.altitude;
	}
	
	public Double getDistance(GPSPosition otherPosition) {
		GeodeticCalculator geoCalc = new GeodeticCalculator();

		Ellipsoid reference = Ellipsoid.WGS84;
		
		GlobalPosition currentGlobal = new GlobalPosition(this.latitude, this.longitude, this.altitude);
		GlobalPosition otherGlobal = new GlobalPosition(otherPosition.latitude, otherPosition.longitude, otherPosition.altitude);

		double distance = geoCalc.calculateGeodeticCurve(reference, otherGlobal, currentGlobal).getEllipsoidalDistance();
		return distance / 1000;
	}
	
	@Override
	public String toString() {
		return new GlobalPosition(this.latitude, this.longitude, this.altitude).toString();
	}
}
