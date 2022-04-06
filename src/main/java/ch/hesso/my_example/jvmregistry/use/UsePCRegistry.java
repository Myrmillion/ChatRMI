package ch.hesso.my_example.jvmregistry.use;

import ch.hearc.tools.rmi.RmiURL;
import ch.hesso.my_example.SettingsRMI;

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
