package ch.hesso.chat_rmi.jvmuser.moo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat_I extends Remote
{
    public Boolean askConnection(User user) throws RemoteException;
    public void setMessage(Message message) throws RemoteException;
    public void disconnectChat(User user) throws RemoteException;
}
