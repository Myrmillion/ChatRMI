package ch.hesso.chat_rmi.jvmregistry.use;

import ch.hearc.tools.rmi.RmiURL;
import ch.hearc.tools.rmi.Rmis;
import ch.hesso.chat_rmi.jvmregistry.moo.Registry;
import ch.hesso.chat_rmi.jvmregistry.moo.Registry_I;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class PCRegistry
{

    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public PCRegistry(RmiURL localURL)
    {
        System.out.println(11);
        this.localURL = localURL;

        server();
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/
    
    /*------------------------------*\
    |*		    	RMI				*|
    \*------------------------------*/

    private void server()
    {
        System.out.println(0);
        this.registry = new Registry();
        shareRegistry();
        System.out.println(1);
    }

    private void shareRegistry()
    {
        try
        {
            Rmis.shareObject(this.registry, this.localURL);
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[PCRegistry] : shareRegistry : fail : " + this.localURL);
            e.printStackTrace();
        }
    }

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs
    private final RmiURL localURL;

    // Outputs

    // Tools
    private Registry_I registry;
}
