package ch.hesso.chat_rmi.jvmuser.moo;

import ch.hesso.chat_rmi.SettingsRMI;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

public class Chat implements Chat_I
{

    /*------------------------------------------------------------------*\
    |*							Constructors							*|
    \*------------------------------------------------------------------*/

    public Chat()
    {
        this.chatController = ChatController.getInstance();
    }

    /*------------------------------------------------------------------*\
    |*							Public Methods 							*|
    \*------------------------------------------------------------------*/


    public Boolean askConnection(Sendable<User> userFrom) throws RemoteException {
        return askConnection(userFrom.decrypt(ChatController.getPrivateKey()));
    }

    @Override
    public Boolean askConnection(User userFrom) throws RemoteException
    {
        AtomicReference<Integer> n = new AtomicReference<Integer>(null);

        try
        {
            SwingUtilities.invokeAndWait(() ->
            {
                JDialog.setDefaultLookAndFeelDecorated(true);
                JOptionPane optionPane = new JOptionPane(userFrom + " wishes the start a chat with you?\nDo you agree ?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

                JDialog dialog = optionPane.createDialog("Chat request from " + userFrom);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

                Timer timer = new Timer(SettingsRMI.TIME_BEFORE_RMI_FAIL, e -> dialog.setVisible(false));
                timer.setRepeats(false);
                timer.start();

                dialog.setVisible(true); // [BLOCKING HERE]

                n.set(optionPane.getValue() instanceof Integer ? (Integer) optionPane.getValue() : -1); // if timer has passed, returned value is not an Integer

                dialog.dispose(); // [DISPOSING HERE]
            });
        }
        catch (InterruptedException | InvocationTargetException e)
        {
            System.err.println("[Chat] : askConnection : fail : invokeAndWait issue");
            e.printStackTrace();
        }

        if (n.get() == 0)
        {
            this.chatController.acceptRequestedConnection(userFrom);
        }

        return (n.get() == 0); // 0: yes, 1: no, -1: no button clicked
    }

    @Override
    public void setMessage(Message message) throws RemoteException
    {
        // Update the GUI
        this.chatController.updateGUI(message);
    }

    @Override
    public void disconnectChat(User userFrom) throws RemoteException
    {
        // Disconnect chat with userFom
        this.chatController.disconnectChat(userFrom);
    }

    /*------------------------------------------------------------------*\
    |*							Private Methods						    *|
    \*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*\
    |*							Private Attributes						*|
    \*------------------------------------------------------------------*/

    // Inputs

    // Outputs

    // Tools
    private final ChatController chatController;

}