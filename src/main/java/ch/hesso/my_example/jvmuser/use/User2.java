package ch.hesso.my_example.jvmuser.use;

import ch.hearc.tools.rmi.RmiURL;
import ch.hesso.my_example.SettingsRMI;
import ch.hesso.my_example.jvmuser.gui.JChat;
import ch.hesso.my_example.jvmuser.gui.tools.JFrameBaseLine;

public class User2
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
        new JFrameBaseLine(new JChat(), "Chat RMI");
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods 						*|
    \*------------------------------------------------------------------*/
}
