
package ch.hesso.bilat_example.jvmsecret.moo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SecretRemote_I extends Remote
	{
	public StringCrypter getSecret() throws RemoteException;
	}

