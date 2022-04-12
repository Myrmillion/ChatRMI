package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hearc.tools.rmi.Rmis;
import ch.hesso.chat_rmi.SettingsRMI;
import ch.hesso.chat_rmi.jvmregistry.moo.Registry_I;
import ch.hesso.chat_rmi.jvmuser.gui.JChat;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JFrameChat;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

public class ChatController
{

    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public ChatController()
    {
        this.listConnectedUsers = new ArrayList<Map.Entry<User, JChat>>();
    }

    /*------------------------------*\
    |*			  Static			*|
    \*------------------------------*/

    public static synchronized ChatController getInstance()
    {
        if (instance == null)
        {
            instance = new ChatController();
        }

        return instance;
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/

    public void updateGUI(User userFrom, String message)
    {
        this.listConnectedUsers.stream().parallel()//
                .filter(entry -> entry.getKey().equals(userFrom))//
                .map(Map.Entry::getValue)//
                .findFirst().ifPresent(jChat -> jChat.updateGUI(message));
    }

    /*------------------------------*\
    |*	            RMI	         	*|
    \*------------------------------*/

    /*-------------*\
    |*	 Server    *|
    \*-------------*/

    public void create(String username) throws MalformedURLException, RemoteException
    {
        // Create the local user and the local chat
        this.userLocal = new User(username, SettingsRMI.CREATE_RMI_URL(username + (new Date()).getTime())); // time to guarantee unicity if users with same name
        this.chatLocal = new Chat();

        // Share the chatLocal on the local url (RMI)
        shareChat();

        // Fetch the registry and add the local user
        this.registry = (Registry_I) Rmis.connectRemoteObjectSync(SettingsRMI.REGISTRY_RMI_URL);
        this.registry.addUser(this.userLocal);
    }

    public void acceptRequestedConnection(User userFrom)
    {
        try
        {
            Chat_I chatRemote = (Chat_I) Rmis.connectRemoteObjectSync(userFrom.getRmiURL());

            String firstMessage = "You accepted to chat with " + userFrom + " !";

            JChat jChat = new JChat(this.userLocal, firstMessage, chatRemote);
            this.listConnectedUsers.add(new AbstractMap.SimpleEntry<User, JChat>(userFrom, jChat));
            new JFrameChat(jChat, userFrom.toString());
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[ChatController] : acceptRequestedConnection : fail : " + userFrom.getRmiURL());
            e.printStackTrace();
        }
    }

    public void disconnectChat(User userFrom)
    {
        this.listConnectedUsers.stream().parallel()//
                .filter(entry -> entry.getKey().equals(userFrom))//
                .findFirst().ifPresent(entry ->
                {
                    entry.getValue().setStopCallback(true);
                    SwingUtilities.getWindowAncestor(entry.getValue()).dispose(); // retrieve jChat Window Ancestor and dispose of it
                    this.listConnectedUsers.remove(entry); // remove disconnecting user from list of connected users
                });
    }

    /*-------------*\
    |*	 Client    *|
    \*-------------*/

    public List<User> getListAvailableUsers() throws RemoteException
    {
        List<User> listConnectedUsers = this.listConnectedUsers.stream().parallel().map(Map.Entry::getKey).toList();

        return this.registry.getListUser().stream().parallel().//
                filter(userAvailable -> !userAvailable.equals(this.userLocal) && !listConnectedUsers.contains(userAvailable)).//
                toList();
    }

    public void removeUserInRegistry() throws RemoteException
    {
        if (this.registry != null)
        {
            this.registry.removeUser(this.userLocal);
        }
    }

    public void askConnection(User userTo)
    {
        List<User> listConnectedUsers = this.listConnectedUsers.stream().parallel().map(Map.Entry::getKey).toList();

        if (!listConnectedUsers.contains(userTo))
        {
            new Thread(() ->
            {
                try
                {
                    Chat_I chatRemote = (Chat_I) Rmis.connectRemoteObjectSync(userTo.getRmiURL());

                    if (chatRemote.askConnection(this.userLocal)) // [CONNECTION ACCEPTED]
                    {
                        String firstMessage = userTo + " has accepted to chat with you !";

                        JChat jChat = new JChat(this.userLocal, firstMessage, chatRemote);
                        this.listConnectedUsers.add(new AbstractMap.SimpleEntry<User, JChat>(userTo, jChat));
                        new JFrameChat(jChat, userTo.toString());
                    }
                }
                catch (RemoteException | MalformedURLException e)
                {
                    System.err.println("[ChatController] : askConnection : fail : " + userTo.getRmiURL());
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void removeUserInConnectedUsers(JChat jChat)
    {
        this.listConnectedUsers.stream().parallel()//
                .filter(entry -> entry.getValue().equals(jChat))//
                .findFirst().ifPresent(entry -> this.listConnectedUsers.remove(entry));
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*	            RMI	         	*|
    \*------------------------------*/

    /*-------------*\
    |*	 Server    *|
    \*-------------*/

    private void shareChat()
    {
        try
        {
            Rmis.shareObject(this.chatLocal, this.userLocal.getRmiURL());
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[ChatController] : shareChat : fail : " + this.userLocal.getRmiURL());
            e.printStackTrace();
        }
    }

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs
    private User userLocal;

    // Outputs

    // Tools
    private Chat_I chatLocal;
    private Registry_I registry;

    private List<Map.Entry<User, JChat>> listConnectedUsers;

    /*------------------------------*\
    |*			  Static			*|
    \*------------------------------*/

    private static ChatController instance = null;

}
