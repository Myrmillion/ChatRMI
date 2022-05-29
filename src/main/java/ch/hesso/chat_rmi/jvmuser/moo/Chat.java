package ch.hesso.chat_rmi.jvmuser.moo;

import java.rmi.RemoteException;

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

    @Override
    public Boolean askConnection(User userFrom) throws RemoteException
    {
        return this.chatController.acceptOrRefuseConnection(userFrom);
    }

    @Override
    public void setMessage(Message message) throws RemoteException
    {
        this.chatController.receiveMessage(message);
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