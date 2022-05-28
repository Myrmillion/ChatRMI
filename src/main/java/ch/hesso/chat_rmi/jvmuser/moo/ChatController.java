package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hearc.tools.rmi.Rmis;
import ch.hesso.chat_rmi.SettingsRMI;
import ch.hesso.chat_rmi.jvmregistry.moo.Registry_I;
import ch.hesso.chat_rmi.jvmuser.gui.JChat;
import ch.hesso.chat_rmi.jvmuser.gui.JMain;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JFrameChat;
import org.javatuples.Pair;

import javax.swing.*;
import javax.swing.Timer;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        this.mapConnectedUser = new HashMap<User, Pair<Chat_I, JChat>>();
        this.listWait = new ArrayList<User>();
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

    public void messageReceived(Message message)
    {
        // TODO : Sauvegarder le message envoyé par message.getUserFrom() et reçu par nous (userLocal) dans la BDD !!!!
        // db.save(recvBy: userLocal, sentBy: message.getUserFrom(), message);

        // Update the GUI
        this.mapConnectedUser.entrySet().stream().parallel()//
                .filter(entry -> entry.getKey().equals(message.getUserFrom()))//
                .map(Map.Entry::getValue)//
                .map(Pair::getValue1)//
                .findFirst()//
                .ifPresent(jChat -> jChat.updateGUI(message));
    }

    public void messageSent(JChat jChat)
    {
        User userRemote = this.mapConnectedUser.entrySet().stream().parallel()//
                .filter(entry -> entry.getValue().getValue1().equals(jChat))//
                .map(Map.Entry::getKey)//
                .findFirst()//
                .get();

        // TODO : Sauvegarder le message envoyé par nous (userLocal) et qui sera reçu par userRemote dans la BDD !!!!
        // db.save(recvBy: userRemote, sentBy: userLocal, message);
    }

    public List<Message> retrieveSavedMessages()
    {
        // TODO : récupérer les messages que l'on partage avec le remote user donné

        return null;
    }

    /*------------------------------*\
    |*				Set				*|
    \*------------------------------*/

    public void setParentFrame(JMain parentFrame)
    {
        this.parentFrame = parentFrame;
    }

    /*------------------------------*\
    |*	            RMI	         	*|
    \*------------------------------*/

    /*------------------*\
    |*	     From       *|
    \*------------------*/

    public void disconnectChat(User userFrom)
    {
        this.mapConnectedUser.entrySet().stream().parallel()//
                .filter(entry -> entry.getKey().equals(userFrom))//
                .findFirst().ifPresent(entry ->
                {
                    entry.getValue().getValue1().setStopCallback(true);

                    // retrieve jChat Window Ancestor and dispose of it
                    SwingUtilities.getWindowAncestor(entry.getValue().getValue1()).dispose();

                    // remove disconnecting user from the map of connected users
                    this.mapConnectedUser.remove(entry.getKey());
                });
    }

    public boolean acceptOrRefuseConnection(User userFrom)
    {
        AtomicReference<Integer> n = new AtomicReference<Integer>(null);

        try
        {
            SwingUtilities.invokeAndWait(() ->
            {
                // Set the default look and feel of a dialog window (it's basic and nice)
                JDialog.setDefaultLookAndFeelDecorated(true);

                // Prepare the options that will be displayed in the dialog window
                JOptionPane optionPane = new JOptionPane(userFrom + " wishes the start a chat with you?\nDo you agree ?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

                // Create the dialog window and set its properties
                JDialog dialog = optionPane.createDialog("Chat request from " + userFrom);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.pack();
                dialog.setLocationRelativeTo(this.parentFrame);

                // Create and start the timer
                Timer timer = new Timer(SettingsRMI.TIME_BEFORE_RMI_FAIL, e -> dialog.setVisible(false));
                timer.setRepeats(false);
                timer.start();

                dialog.setVisible(true); // [BLOCKING HERE]
                dialog.toFront();

                // Set the value of the atomic variable : if timer is over, the returned value is not an Integer
                n.set(optionPane.getValue() instanceof Integer ? (Integer) optionPane.getValue() : -1);

                dialog.dispose(); // [DISPOSING HERE]
            });
        }
        catch (InterruptedException | InvocationTargetException e)
        {
            System.err.println("[ChatController] : acceptOrRefuseConnection : fail : invokeAndWait issue");
            e.printStackTrace();
        }

        if (n.get() == 0)
        {
            acceptRequestedConnection(userFrom);
        }

        return (n.get() == 0); // 0: yes, 1: no, -1: no button clicked
    }

    /*------------------*\
    |*	     To         *|
    \*------------------*/

    public void askConnection(User userTo)
    {
        if (!this.mapConnectedUser.containsKey(userTo) && !this.listWait.contains(userTo))
        {
            this.listWait.add(userTo); // makes sure one cannot spam another with multiple connection requests

            new Thread(() ->
            {
                try
                {
                    Chat_I chatRemote = (Chat_I) Rmis.connectRemoteObjectSync(userTo.getRmiURL());

                    if (chatRemote.askConnection(this.userLocal)) // [CONNECTION ACCEPTED]
                    {
                        String firstMessage = userTo + " has accepted to chat with you !";

                        JChat jChat = new JChat(this.userLocal, firstMessage, chatRemote);
                        this.mapConnectedUser.put(userTo, new Pair<Chat_I, JChat>(chatRemote, jChat));

                        new JFrameChat(jChat, userLocal.toString(), userTo.toString());
                    }
                }
                catch (RemoteException | MalformedURLException e)
                {
                    System.err.println("[ChatController] : askConnection : fail : " + userTo.getRmiURL());
                    e.printStackTrace();
                }

                this.listWait.remove(userTo); // makes sure one can send a new connection request now
            }).start();
        }
    }

    /*------------------*\
    |*	     Utils      *|
    \*------------------*/

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

    public void removeLocalUserFromRegistry() throws RemoteException
    {
        if (this.registry != null)
        {
            this.registry.removeUser(this.userLocal);
        }
    }

    public void removeUserFromChatting(JChat jChat)
    {
        this.mapConnectedUser.entrySet().stream().parallel()//
                .filter(entry -> entry.getValue().getValue1().equals(jChat))//
                .findFirst()//
                .map(Map.Entry::getKey)//
                .ifPresent(this.mapConnectedUser::remove);
    }

    public List<User> getListAvailableUsers() throws RemoteException
    {
        return this.registry.getListUser().stream().parallel().//
                filter(userAvailable -> !userAvailable.equals(this.userLocal) && !this.mapConnectedUser.containsKey(userAvailable)).//
                toList();
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------*\
    |*	            RMI	         	*|
    \*------------------------------*/

    /*------------------*\
    |*	     From       *|
    \*------------------*/

    private void acceptRequestedConnection(User userFrom)
    {
        try
        {
            String firstMessage = "You accepted to chat with " + userFrom + " !";

            Chat_I chatRemote = (Chat_I) Rmis.connectRemoteObjectSync(userFrom.getRmiURL());
            JChat jChat = new JChat(this.userLocal, firstMessage, chatRemote);
            this.mapConnectedUser.put(userFrom, new Pair<Chat_I, JChat>(chatRemote, jChat));

            new JFrameChat(jChat, this.userLocal.toString(), userFrom.toString());
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[ChatController] : acceptRequestedConnection : fail : " + userFrom.getRmiURL());
            e.printStackTrace();
        }
    }

    /*------------------*\
    |*	      To        *|
    \*------------------*/

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

    private final HashMap<User, Pair<Chat_I, JChat>> mapConnectedUser;
    private final List<User> listWait;

    private JMain parentFrame;

    /*------------------------------*\
    |*			  Static			*|
    \*------------------------------*/

    private static ChatController instance = null;
}
