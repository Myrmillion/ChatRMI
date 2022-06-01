package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hearc.tools.rmi.RmiURL;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Arrays;

public class User implements Serializable
{
    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public User(String username, RmiURL rmiURL)
    {
        this(username, rmiURL, null); // Laoun qui a oublié les cours de Bilat :sad: :cry: :sob:

        //        this.username = username;
        //        this.rmiURL = rmiURL;
        //        this.publicKey = null;
    }

    public User(String username, RmiURL rmiURL, PublicKey publicKey)
    {
        this.username = username;
        this.rmiURL = rmiURL;
        this.publicKey = publicKey;
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
            //            return (this.rmiURL.toString().equals(obj2.rmiURL.toString())) && (this.username.equals(obj2.username));

            //Pour tester
            return (this.username.equals(obj2.username));
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
        return this.username.concat(this.rmiURL.toString()).hashCode();
    }

    /*------------------------------*\
    |*				Get				*|
    \*------------------------------*/

    public String getUsername()
    {
        return this.username;
    }

    public RmiURL getRmiURL()
    {
        return this.rmiURL;
    }

    public PublicKey getPublicKey()
    {
        return publicKey;
    }

    /*------------------------------*\
    |*			Converter			*|
    \*------------------------------*/

    public static User getUser(String string)
    {
        String[] tabSplit = string.split(",");
        return new User(tabSplit[0], getRmiURL(tabSplit[1]));
    }

    public static String getString(User user)
    {
        return user.getUsername() + "," + getString(user.getRmiURL());
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*			Converter			*|
    \*------------------------------*/

    private static RmiURL getRmiURL(String string)
    {
        try
        {
            String[] tabSplit = string.split(";");
            return new RmiURL(tabSplit[0], InetAddress.getByName(tabSplit[1]), Integer.parseInt(tabSplit[2]));
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            System.err.println("[User] : getRmiURL");
            System.exit(0); // 0: ok, -1: ko
            return null;
        }
    }

    private static String getString(RmiURL rmiUrl)
    {
        return rmiUrl.getObjectId() + ";" + rmiUrl.getInetAdressName() + ";" + rmiUrl.getPort();
    }

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
