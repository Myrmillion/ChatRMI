package ch.hesso.my_example;

import ch.hearc.tools.rmi.RmiURL;

import java.io.Serializable;

public class UserURL implements Serializable
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public UserURL(String username, RmiURL rmiURL)
    {
        this.username = username;
        this.rmiURL = rmiURL;
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    @Override
    public String toString()
    {
        return this.username;
    }

    /*------------------------------*\
    |*				Get				*|
    \*------------------------------*/

    public RmiURL getRmiURL()
    {
        return rmiURL;
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs
    private String username;
    private RmiURL rmiURL;

    // Outputs

    // Tools
}
