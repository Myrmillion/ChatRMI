package ch.hesso.bilat_example.jvmhorloge.use;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import ch.hesso.bilat_example.SettingsRMI;
import ch.hesso.bilat_example.jvmhorloge.moo.Horloge;
import ch.hesso.bilat_example.jvmsecret.moo.SecretRemote_I;
import ch.hearc.tools.rmi.RmiURL;
import ch.hearc.tools.rmi.Rmis;

/**
 * singleton
 */
public class PCHorloge
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

    private PCHorloge()
    {
        server();
        client();
    }

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

    public static synchronized PCHorloge getInstance()
    {
        if (instance == null)
        {
            instance = new PCHorloge();
        }

        return instance;
    }

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	/*------------------------------*\
	|*				Server			*|
	\*------------------------------*/

    private void server()
    {
        this.horloge = new Horloge();
        shareHorloge();
    }

    private void shareHorloge()
    {
        RmiURL rmiURL = SettingsRMI.HORLOGE_RMI_URL;

        try
        {
            Rmis.shareObject(this.horloge, rmiURL);
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[PCHorloge] : shareHorloge : fail : " + rmiURL);
            e.printStackTrace();
        }
    }

	/*------------------------------*\
	|*				Client			*|
	\*------------------------------*/

    private void client()
    {
        connectionSecret();
        workSecret();
    }

    private void connectionSecret()
    {
        RmiURL rmiURL = SettingsRMI.SECRET_RMI_URL;

        try
        {
            this.secretRemote = (SecretRemote_I) Rmis.connectRemoteObjectSync(rmiURL);
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[PCHorloge] : connectionSecret : fail : " + rmiURL);
            e.printStackTrace();
        }
    }

    private void workSecret()
    {
        try
        {
            System.out.println("[PCHorloge] : " + this.secretRemote.getSecret());
        }
        catch (RemoteException e)
        {
            System.err.println("[PCHorloge] : workSecret : fail");
            e.printStackTrace();
        }
    }

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

    // Outputs

    // Tools
    private Horloge horloge;
    private SecretRemote_I secretRemote;

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

    private static PCHorloge instance = null;

}
