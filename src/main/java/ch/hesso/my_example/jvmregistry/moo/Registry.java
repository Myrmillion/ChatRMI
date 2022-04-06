package ch.hesso.my_example.jvmregistry.moo;

import ch.hearc.tools.rmi.RmiURL;
import ch.hesso.my_example.UserURL;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Registry implements RegistryRemote_I
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public Registry()
    {
        this.listUserURL = new ArrayList<UserURL>();
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    @Override
    public void addUserURL(UserURL userURL) throws RemoteException
    {
        listUserURL.add(userURL);
    }

    @Override
    public List<UserURL> getListUserURL() throws RemoteException
    {
        return this.listUserURL;
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
    List<UserURL> listUserURL;
}
