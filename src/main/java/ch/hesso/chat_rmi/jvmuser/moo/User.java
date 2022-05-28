package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hearc.tools.rmi.RmiURL;

import java.io.Serializable;
import java.security.PublicKey;

public class User implements Serializable
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public User(String username, RmiURL rmiURL)
    {
        this.username = username;
        this.rmiURL = rmiURL;
        this.publicKey = null;
    }

    public User(String username, RmiURL rmiURL, PublicKey publicKey)
    {
        this.username = username;
        this.rmiURL = rmiURL;
        this.publicKey = null;
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

    @Override
    public int hashCode()
    {
        return username.concat(rmiURL.toString()).hashCode();
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
    private final String username;
    private final RmiURL rmiURL;

    // Outputs

    // Tools
    private final PublicKey publicKey;

}
