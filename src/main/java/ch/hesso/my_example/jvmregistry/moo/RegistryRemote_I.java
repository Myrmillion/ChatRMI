package ch.hesso.my_example.jvmregistry.moo;

import ch.hearc.tools.rmi.RmiURL;
import ch.hesso.my_example.UserURL;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RegistryRemote_I extends Remote
{
    public void addUserURL(UserURL userURL) throws RemoteException;

    public List<UserURL> getListUserURL() throws RemoteException;
}
