package ch.hesso.bilat_example.jvmsecret.use;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import ch.hesso.bilat_example.SettingsRMI;
import ch.hesso.bilat_example.jvmhorloge.moo.HorlogeRemote_I;
import ch.hesso.bilat_example.gui.JHorloge;
import ch.hesso.bilat_example.jvmsecret.moo.Secret;
import ch.hearc.tools.rmi.RmiURL;
import ch.hearc.tools.rmi.Rmis;
import ch.hesso.my_example.jvmuser.gui.tools.JFrameBaseLine;

/**
 * singleton
 */
public class PCSecret
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

    private PCSecret()
    {
        server();
        client();
    }

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

    public static synchronized PCSecret getInstance()
    {
        if (instance == null)
        {
            instance = new PCSecret();
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
        this.secret = new Secret();
        shareSecret();
    }

    private void shareSecret()
    {
        RmiURL rmiURL = SettingsRMI.SECRET_RMI_URL;

        try
        {
            Rmis.shareObject(secret, rmiURL);
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[PCSecret] : shareSecret : fail : " + rmiURL);
            e.printStackTrace();
        }
    }

	/*------------------------------*\
	|*				Client			*|
	\*------------------------------*/

    private void client()
    {
        connectionHorloge();
        workHorloge();
    }

    private void connectionHorloge()
    {
        RmiURL rmiURL = SettingsRMI.HORLOGE_RMI_URL;

        try
        {
            this.horlogeRemote = (HorlogeRemote_I) Rmis.connectRemoteObjectSync(rmiURL);
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[PCSecret] : connectionHorloge : fail : " + rmiURL);
            e.printStackTrace();
        }
    }

    private void workHorloge()
    {
        try
        {
            System.out.println("[PCSecret] : " + this.horlogeRemote.getDate());
            new JFrameBaseLine(new JHorloge(horlogeRemote));
        }
        catch (RemoteException e)
        {
            System.err.println("[PCSecret] : workHorloge : fail");
            e.printStackTrace();
        }
    }

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

    // Outputs

    // Tools
    private HorlogeRemote_I horlogeRemote;
    private Secret secret;

	/*------------------------------*\
	|*			  Static			*|
	\*------------------------------*/

    private static PCSecret instance = null;

}
