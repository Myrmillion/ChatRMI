package ch.hesso.chat_rmi.jvmregistry.use;

import ch.hearc.tools.rmi.RmiURL;
import ch.hesso.chat_rmi.SettingsRMI;

public class UsePCRegistry
{
    /*------------------------------------------------------------------*\
    |*							Public Methods							*|
    \*------------------------------------------------------------------*/

    public static void main(String[] args)
    {
        main();
    }

    public static void main()
    {
        RmiURL localURL = SettingsRMI.REGISTRY_RMI_URL;

        new PCRegistry(localURL);
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods 						*|
    \*------------------------------------------------------------------*/
}
