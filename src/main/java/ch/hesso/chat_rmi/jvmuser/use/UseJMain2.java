package ch.hesso.chat_rmi.jvmuser.use;

import ch.hesso.chat_rmi.jvmuser.gui.JMain;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JFrameChat;

public class UseJMain2
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
        new JFrameChat(new JMain(), "Chat RMI");
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods 						*|
    \*------------------------------------------------------------------*/
}
