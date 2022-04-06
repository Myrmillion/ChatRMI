package ch.hesso.my_example.jvmuser.moo;

import ch.hesso.my_example.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatRemote_I extends Remote
{
    public void addMessage(Message message) throws RemoteException;
}
