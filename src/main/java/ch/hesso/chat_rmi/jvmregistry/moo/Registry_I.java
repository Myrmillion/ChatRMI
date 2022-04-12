package ch.hesso.chat_rmi.jvmregistry.moo;

import ch.hesso.chat_rmi.jvmuser.moo.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Registry_I extends Remote
{
    public List<User> getListUser() throws RemoteException;

    public void addUser(User user) throws RemoteException;

    public void removeUser(User user) throws RemoteException;
}
