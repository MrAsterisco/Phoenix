package phoenix.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import phoenix.base.Car;
import phoenix.base.ClientCallback;

/**
 * Rappresenta il callback che viene passato al Server durante le operazioni di ricerca a consegna auto.
 * 
 * Considerato che anche la classe User ha un collegamento all'auto, sarà compito del Client decidere quale auto tenere in considerazione.
 * 
 * @author Alessio Moiso
 * @version 1.0
 * @see PhoenixClient
 */
public class PhoenixClientCallback extends UnicastRemoteObject implements ClientCallback {

	private static final long serialVersionUID = -4848459072379098363L;
	
	private Car car;
	
	public PhoenixClientCallback() throws RemoteException {
		super();
	}
	
	public PhoenixClientCallback(Car car) throws RemoteException {
		this();
		this.car = car;
	}

	@Override
	public void setCar(Car car) throws RemoteException {
		this.car = car;
	}
	
	public Car getCar() throws RemoteException {
		while (this.car == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) { }
		}
		return this.car;
	}

}
