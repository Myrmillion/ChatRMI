package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hearc.tools.rmi.RmiURL;

import java.io.Serializable;

public class User implements Serializable
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public User(String username, RmiURL rmiURL)
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
    |*			  equals			*|
    \*------------------------------*/

    public boolean isEquals(User obj2)
    {
        if (this == obj2)
        {
            return true;
        }
        else
        {
            return (this.rmiURL.toString().equals(obj2.rmiURL.toString())) && (this.username.equals(obj2.username));
        }
    }

    @Override
    public boolean equals(Object obj2)
    {
        if (obj2 instanceof User)
        {
            return isEquals((User) obj2);
        }
        else
        {
            return false;
        }
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
