package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hearc.tools.rmi.Rmis;
import ch.hesso.chat_rmi.SettingsRMI;
import ch.hesso.chat_rmi.jvmregistry.moo.Registry_I;
import ch.hesso.chat_rmi.jvmuser.gui.JChat;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JFrameChat;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.*;
import java.util.List;

/**
 * Singleton
 */
public class ChatController
{

    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public ChatController()
    {
        this.listCurrentChatting = new ArrayList<Map.Entry<User, JChat>>();
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

    public void updateGUI(Message message)
    {
        this.listCurrentChatting.stream().parallel()//
                .filter(entry -> entry.getKey().equals(message.getUserFrom()))//
                .map(Map.Entry::getValue)//
                .findFirst().ifPresent(jChat -> jChat.updateGUI(message));
    }

    /*------------------------------*\
    |*	            RMI	         	*|
    \*------------------------------*/

    public void prepareRMI(String username) throws MalformedURLException, RemoteException, NoSuchAlgorithmException
    {
        // Generate public and private key for this user
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        this.keyPair = generator.generateKeyPair();

        // Create the local user and the local chat
        this.userLocal = new User(username, SettingsRMI.CHAT_RMI_URL(username + (new Date()).getTime()), this.keyPair.getPublic()); // time guarantee unicity if users with same name
        this.chatLocal = new Chat();

        // Share the chatLocal on the local url (RMI)
        shareChat();

        // Fetch the registry and add the local user
        this.registry = (Registry_I) (Rmis.connectRemoteObjectSync(SettingsRMI.REGISTRY_RMI_URL, 250, 8));
        this.registry.addUser(this.userLocal);
    }

    public void acceptRequestedConnection(User userFrom)
    {
        try
        {
            Chat_I chatRemote = (Chat_I) Rmis.connectRemoteObjectSync(userFrom.getRmiURL());

            String firstMessage = "You accepted to chat with " + userFrom + " !";

            JChat jChat = new JChat(this.userLocal, firstMessage, chatRemote);
            this.listCurrentChatting.add(new AbstractMap.SimpleEntry<User, JChat>(userFrom, jChat));
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
        this.listCurrentChatting.stream().parallel()//
                .filter(entry -> entry.getKey().equals(userFrom))//
                .findFirst().ifPresent(entry ->
                {
                    entry.getValue().setStopCallback(true);
                    SwingUtilities.getWindowAncestor(entry.getValue()).dispose(); // retrieve jChat Window Ancestor and dispose of it
                    this.listCurrentChatting.remove(entry); // remove disconnecting user from list of connected users
                });
    }

    public List<User> getListAvailableUsers() throws RemoteException
    {
        List<User> listCurrentUsers = this.listCurrentChatting.stream().parallel().map(Map.Entry::getKey).toList();

        return this.registry.getListUser().stream().parallel().//
                filter(userAvailable -> !userAvailable.equals(this.userLocal) && !listCurrentUsers.contains(userAvailable)).//
                toList();
    }

    public static PrivateKey getPrivateKey() {
        return getInstance().keyPair.getPrivate();
    }

    public void removeLocalUserFromRegistry() throws RemoteException
    {
        if (this.registry != null)
        {
            this.registry.removeUser(this.userLocal);
        }
    }

    public void askConnection(User userTo)
    {
        List<User> listCurrentUsers = this.listCurrentChatting.stream().parallel().map(Map.Entry::getKey).toList();

        if (!listCurrentUsers.contains(userTo))
        {
            new Thread(() ->
            {
                try
                {
                    Chat_I chatRemote = (Chat_I) Rmis.connectRemoteObjectSync(userTo.getRmiURL());

                    if (chatRemote.askConnection(new Sendable<User>(this.userLocal, userTo))) // [CONNECTION ACCEPTED]
                    {
                        String firstMessage = userTo + " has accepted to chat with you !";

                        JChat jChat = new JChat(this.userLocal, firstMessage, chatRemote);
                        this.listCurrentChatting.add(new AbstractMap.SimpleEntry<User, JChat>(userTo, jChat));
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

    public void removeLocalUserFromCurrentChatting(JChat jChat)
    {
        this.listCurrentChatting.stream().parallel()//
                .filter(entry -> entry.getValue().equals(jChat))//
                .findFirst().ifPresent(this.listCurrentChatting::remove);
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*	            RMI	         	*|
    \*------------------------------*/

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
    private Chat chatLocal;
    private Registry_I registry;
    private KeyPair keyPair;

    private final List<Map.Entry<User, JChat>> listCurrentChatting;

    /*------------------------------*\
    |*			  Static			*|
    \*------------------------------*/

    private static ChatController instance = null;

}
