package ch.hesso.my_example.jvmregistry.use;

import ch.hearc.tools.rmi.RmiURL;
import ch.hearc.tools.rmi.Rmis;
import ch.hesso.my_example.jvmregistry.moo.Registry;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class PCRegistry
{

    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public PCRegistry(RmiURL localURL)
    {
        this.localURL = localURL;

        server();
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*				Get				*|
    \*------------------------------*/

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/
    
    /*------------------------------*\
    |*			Server				*|
    \*------------------------------*/

    private void server()
    {
        this.registry = new Registry();
        shareRegistry();
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
    private RmiURL localURL;

    // Outputs

    // Tools
    private Registry registry;
}
