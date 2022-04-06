
package ch.hesso.bilat_example.jvmhorloge.moo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface HorlogeRemote_I extends Remote
	{
	public Date getDate() throws RemoteException;
	}
