package ch.hesso.chat_rmi.jvmuser.moo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat_I extends Remote
{
    public Boolean askConnection(Sendable<User> user) throws RemoteException;

    public void setMessage(Sendable<User> user, Sendable<Message> message) throws RemoteException;

    public void disconnectChat(Sendable<User> user) throws RemoteException;
}
