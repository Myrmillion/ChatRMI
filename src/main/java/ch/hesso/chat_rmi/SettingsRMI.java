package ch.hesso.chat_rmi;

import ch.hearc.tools.rmi.IpMachines;
import ch.hearc.tools.rmi.Ports;
import ch.hearc.tools.rmi.RmiURL;
import ch.hesso.chat_rmi.jvmregistry.moo.Registry;

import java.net.InetAddress;

/**
 * <pre>
 * <b><u>USER GUIDE</u></b>
 *
 *    From eclipse:
 *
 *    		- Lancer UsePCHorloge  (choisir dans le laucher eclipse, fleche verte, jvm settings, true pour mono JVM)
 *
 *    		- Lancer UsePCSecret   (choisir dans le laucher eclipse, fleche verte, jvm settings, true pour mono JVM)
 *
 *    From Jar:
 *
 *    		1) Jarer (avec le script mis a disposition dans la trousse a outil d'eclipse)
 *
 *    		2) Lancer runHorloge.xxx (script dans deploy)
 *
 *   				Configuration (dans runHorloge.xxx):
 *
 *   						Choisir soit true pour IS_MONO_PC, soit false.-DIS_MONO_PC=true
 *   						Si false, specifier l'ip distante ou se trouve le secret avec -DIP_Secret=...
 *
 * 			3) Lancer runSecret.xxx  (script dans deploy)
 *   				�
 *   				Configuration (dans runSecret.xxx
 *   						Choisir soit true pour IS_MONO_PC, soit false.
 *   						Si false, specifier l'ip distante ou se trouve l'horloge, avec -DIP_Horloge=...
 *
 *   		ou xxx = .sh ou .cmd selon l'OS.
 *
 *   TIP 1
 *   		On peut croiser les OS entre  PCSecret et PCHorloge
 *
 *   TIP 2
 *
 * 			Imperativement avoir les meme version de jar des deux coter!
 *
 * 	 TIP 3
 *
 * 			C'est croiser:
 *
 * 				 Dans runHorloge on donne l'ip du secret
 * 				 Dans runSecret on donne l'ip de l'horloge
 * 	 TIP 4
 *
 * 		Si vous avez des problemes de security, lancer la version policy:
 *
 * 				- soit dans les laucher eclipse
 *
 * 				- soit en editant les scripts
 * 						- runHorloge.xxx
 *  					- runSecret.xxx
 *
 *  TIP 5
 *
 *  	Si vous employez un vpn, utiliser en mode <b>interne</b> quand vous le lancer!
 *
 * <b><u>SECURITY</u></b>
 *
 * si vous vez des probmes de permission, lancer la jvm avec
 *
 * 		-Djava.security.manager -Djava.security.policy=rmi.policy
 *
 * ou rmi.policy est un fichier texte a la racine du projet
 *
 *    grant {
 *          permission java.security.AllPermission;
 *          };
 *
 * </pre>
 */
public class SettingsRMI
{
	/*------------------------------------------------------------------*\
	|*							Private Methodes						*|
	\*------------------------------------------------------------------*/

    // TIME_OUT_MS : experimental
    //	static
    //		{
    //		try
    //			{
    //			final long TIME_OUT_MS = 2000;
    //			System.out.println("[SettingsRMI] : timeout : " + TIME_OUT_MS + "[ms]");
    //			Rmis.setTimeout(TIME_OUT_MS);
    //			}
    //		catch (IOException e)
    //			{
    //			e.printStackTrace();
    //			}
    //		}

	/*------------------------------*\
	|*				IP				*|
	\*------------------------------*/

    /**
     * <pre>
     * UsePcHorloge : either/either:
     * 		-DIP_Secret= ...
     * 		-DIS_MONO_PC=...
     * </pre>
     */
    private static InetAddress secretIP()
    {
        // soit on doit se connecter au secret
        // soit on doit le partager
        try
        {
            String ipSecret = System.getProperty("IP_Secret");

            if (isMonoPc())
            {
                return ipMonoPC();
            }
            else if (ipSecret != null)
            {
                return InetAddress.getByName(ipSecret);// ip use for remote connection to secret
            }
            else
            {
                // ip use for share secret
                {
                    //InetAddress thisMachine=InetAddress.getByName("192.168.0.24");		// IP standard, ko si vpn ouvert  	(warning : IP change a chaque session)
                    //InetAddress thisMachine= InetAddress.getByName("157.26.67.49");		// IP VPN 							(warning : change a chaque session)

                    InetAddress thisMachine = IpMachines.ipLucky();//choose VPN if VPN open

                    String thisIP = thisMachine.getHostAddress();
                    System.setProperty("java.rmi.server.hostname", thisIP);// usefull in linux

                    return thisMachine;// choose VPN if VPN open
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("[SettingsRMI] : secretIP");
            System.exit(-1); // 0 normal, -1 anormal
            return null;
        }
    }

    /**
     * <pre>
     * UsePcSecret : either/either:
     * 		-DIP_Horloge= ...
     * 		-DIS_MONO_PC=...
     * </pre>
     */
    private static InetAddress horlogeIP()
    {
        // soit on doit se connecter a horloge
        // soit on doit partager horloge
        try
        {
            String ipHorloge = System.getProperty("IP_Horloge");

            if (isMonoPc())
            {
                return ipMonoPC();
            }
            else if (ipHorloge != null)
            {
                return InetAddress.getByName(ipHorloge); // ip use for remote connection to horloge
            }
            else
            {
                // ip use for share horloge
                {
                    //InetAddress thisMachine=InetAddress.getByName("192.168.0.24");		// IP standard, ko si vpn ouvert  	(warning : IP change a chaque session)
                    //InetAddress thisMachine= InetAddress.getByName("157.26.67.49");		// IP VPN 							(warning : change a chaque session)

                    InetAddress thisMachine = IpMachines.ipLucky();//choose VPN if VPN open

                    String thisIP = thisMachine.getHostAddress();
                    System.setProperty("java.rmi.server.hostname", thisIP);// usefull in linux

                    return thisMachine;// choose VPN if VPN open
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("[SettingsRMI] : horlogeIP");
            System.exit(-1); // 0 normal, -1 anormal
            return null;
        }
    }

	/*------------------------------*\
	|*				isMonoPc		*|
	\*------------------------------*/

    private static InetAddress ipMonoPC() throws Exception
    {
        final boolean IS_V1 = false;

        if (IS_V1)// vrai localhost:
        {
            return InetAddress.getByName("localhost");

            //return InetAddress.getByName("127.0.0.1");
            //return InetAddress.getLocalHost();
        }
        else// en passant par la carte:
        {
            return IpMachines.ipLucky();//choose VPN if VPN open

            //return InetAddress.getByName("192.168.0.24");		// IP standard, ko si vpn ouvert  	(warning : IP change a chaque session)
            //return InetAddress.getByName("157.26.67.49"); // IP VPN 							(warning : IP change a chaque session)
        }
    }

    private static boolean isMonoPc()
    {
        String isMonoPc = System.getProperty("IS_MONO_PC");

        if (isMonoPc == null || (!isMonoPc.equals("true") && !isMonoPc.equals("false")))
        {
            System.err.println("\n[SettingsRMI] : isMonoPc : launch JVM with : -DIS_MONO_PC=true\n");
            System.exit(-1); // 0 normal, -1 anormal
            return false;
        }
        else
        {
            return Boolean.valueOf(isMonoPc);
        }
    }

	/*------------------------------------------------------------------*\
	|*							Private Attributes						*|
	\*------------------------------------------------------------------*/

    /*------------------------------*\
	|*				Chat     		*|
	\*------------------------------*/

    private static final int RMI_PORT = Ports.PORT_RMI_DEFAUT;
    private static final InetAddress RMI_INETADDRESS = horlogeIP();

    public static RmiURL CREATE_RMI_URL(String RMI_ID)
    {
        return new RmiURL(RMI_ID, RMI_INETADDRESS, RMI_PORT);
    }

    /*------------------------------*\
	|*			 Registry   	   	*|
	\*------------------------------*/

    private static final String REGISTRY_RMI_ID = Registry.class.getName();// Guarantee the unicity
    private static final int REGISTRY_RMI_PORT = Ports.PORT_RMI_DEFAUT;
    private static final InetAddress REGISTRY_RMI_INETADDRESS = horlogeIP();

    public static final RmiURL REGISTRY_RMI_URL = new RmiURL(REGISTRY_RMI_ID, REGISTRY_RMI_INETADDRESS, REGISTRY_RMI_PORT);

    /*------------------------------*\
	|*			 Registry   	   	*|
	\*------------------------------*/

    public static final int TIME_BEFORE_RMI_FAIL = 8000; // (in ms)
}
