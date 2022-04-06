package ch.hesso.my_example.jvmuser.moo;

import ch.hearc.tools.rmi.RmiURL;
import ch.hearc.tools.rmi.Rmis;
import ch.hesso.my_example.Message;
import ch.hesso.my_example.jvmuser.gui.JChat;
import ch.hesso.my_example.jvmuser.moo.ChatRemote_I;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Chat implements ChatRemote_I
{

    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public Chat(JChat jChat)
    {
        this.jChat = jChat;
        this.listMessage = new ArrayList<Message>();
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    @Override
    public void addMessage(Message message)
    {
        // Add the new message to the list
        this.listMessage.add(message);

        // Update the UI
        this.jChat.updateGUI(this.listMessage);
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs
    private JChat jChat;

    // Outputs

    // Tools
    private List<Message> listMessage;
}

