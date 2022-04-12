package ch.hesso.chat_rmi.jvmregistry.moo;

import ch.hesso.chat_rmi.jvmuser.moo.User;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Registry implements Registry_I
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public Registry()
    {
        this.listUser = new ArrayList<User>();
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    @Override
    public List<User> getListUser() throws RemoteException
    {
        return this.listUser;
    }

    @Override
    public void addUser(User user) throws RemoteException
    {
        listUser.add(user);
    }

    @Override
    public void removeUser(User user) throws RemoteException
    {
        listUser.remove(user);
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs


    // Outputs

    // Tools
    List<User> listUser;
}
