package ch.hesso.chat_rmi;

import ch.hearc.tools.rmi.Ports;
import ch.hearc.tools.rmi.RmiURL;
import ch.hesso.chat_rmi.jvmregistry.moo.Registry;
import ch.hesso.chat_rmi.jvmuser.gui.tools.Utils;
import ch.hesso.chat_rmi.jvmuser.moo.Sendable;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

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

	/*------------------------------*\
	|*				IP				*|
	\*------------------------------*/

    private static InetAddress chatIP()
    {
        try
        {
            String ipChat = System.getProperty("IP_Chat");

            if (isMonoPc())
            {
                return ipMonoPC();
            }
            else if (ipChat != null)
            {
                return InetAddress.getByName(ipChat); // ip use for remote connection to secret
            }
            else
            {
                System.err.println("\n[SettingsRMI] : chatIP : if -DIS_MONO_PC=false, you MUST set -DIP_Chat !");
                System.exit(0); // 0: ok, -1: ko
                return null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("[SettingsRMI] : chatIP");
            System.exit(0); // 0: ok, -1: ko
            return null;
        }
    }

    private static InetAddress registryIP()
    {
        try
        {
            String ipRegistry = System.getProperty("IP_Registry");

            if (isMonoPc())
            {
                return ipMonoPC();
            }
            else if (ipRegistry != null)
            {
                return InetAddress.getByName(ipRegistry); // ip use for remote connection to Registry
            }
            else
            {
                System.err.println("\n[SettingsRMI] : registryIP : if -DIS_MONO_PC=false, you MUST set -DIP_Registry !");
                System.exit(0); // 0: ok, -1: ko
                return null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("[SettingsRMI] : registryIP");
            System.exit(0); // 0: ok, -1: ko
            return null;
        }
    }

	/*------------------------------*\
	|*				isMonoPc		*|
	\*------------------------------*/

    private static InetAddress ipMonoPC() throws Exception
    {
        return InetAddress.getByName("localhost"); // or InetAddress.getByName("127.0.0.1") or InetAddress.getLocalHost()
    }

    private static boolean isMonoPc()
    {
        String isMonoPc = System.getProperty("IS_MONO_PC");

        if (isMonoPc == null || (!isMonoPc.equals("true") && !isMonoPc.equals("false")))
        {
            System.err.println("\n[SettingsRMI] : isMonoPc : launch JVM with : -DIS_MONO_PC=true\n");
            System.exit(0); // 0: ok, -1: ko
            return false;
        }
        else
        {
            return Boolean.parseBoolean(isMonoPc);
        }
    }

	/*------------------------------------------------------------------*\
	|*							Private Attributes						*|
	\*------------------------------------------------------------------*/

    /*------------------------------*\
	|*				Chat     		*|
	\*------------------------------*/

    private static final int CHAT_RMI_PORT = Ports.PORT_RMI_DEFAUT; // 1099

    public static RmiURL CHAT_RMI_URL(String username, byte[] encodedKey) // CHAT_RMI_ID is guaranteeing the unicity
    {
        StringBuilder macAddress = new StringBuilder("");
        StringBuilder hashedEncodedKey = new StringBuilder("");

        try
        {
            // Getting the MAC Address in String format
            Utils.byteStream(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress())//
                    .parallel()//
                    .forEachOrdered(aByte -> macAddress.append(String.format("%02X", aByte)));

            // Getting the encoded PublicKey in hashed SHA-256 String format
            Utils.byteStream(Sendable.hash(encodedKey))//
                    .parallel()//
                    .forEachOrdered(aByte -> hashedEncodedKey.append(String.format("%02X", aByte)));
        }
        catch (Exception e)
        {
            System.err.println("\n[SettingsRMI] : CHAT_RMI_URL : failed to obtain MAC Address\n");
            System.exit(0); // 0: ok, -1: ko
            return null;
        }

        String CHAT_RMI_ID = username + "-" + macAddress + "-" + hashedEncodedKey;

        return new RmiURL(CHAT_RMI_ID, chatIP(), CHAT_RMI_PORT);
    }

    /*------------------------------*\
	|*			 Registry   	   	*|
	\*------------------------------*/

    private static final String REGISTRY_RMI_ID = Registry.class.getName(); // Guarantee the unicity
    private static final InetAddress REGISTRY_RMI_INETADDRESS = registryIP(); // Is static because IP_Registry HAS to be set
    private static final int REGISTRY_RMI_PORT = Ports.PORT_RMI_DEFAUT; // 1099

    public static final RmiURL REGISTRY_RMI_URL = new RmiURL(REGISTRY_RMI_ID, REGISTRY_RMI_INETADDRESS, REGISTRY_RMI_PORT);

    /*------------------------------*\
	|*			 Registry   	   	*|
	\*------------------------------*/

    public static final int TIME_BEFORE_RMI_FAIL = 8000; // (in ms)
}
