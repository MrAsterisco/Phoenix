package phoenix.base;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Modella il callback da passare al Server durante un'operazione di ricerca o consegna di un'auto.
 * 
 * @author Alessio Moiso
 * @version 1.0
 */
public interface ClientCallback extends Remote {

	public Car getCar() throws RemoteException;
	
	public void setCar(Car car) throws RemoteException;
	
}
