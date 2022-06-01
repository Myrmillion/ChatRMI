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


    public Boolean askConnection(Sendable<User> userFrom) throws RemoteException {
        return askConnection(userFrom.decrypt(chatController.getPrivateKey()));
    }

    public Boolean askConnection(User userFrom) throws RemoteException
    {
        return this.chatController.acceptOrRefuseConnection(userFrom);
    }

    @Override
    public void setMessage(Sendable<User> userFrom, Sendable<Message> message) throws RemoteException
    {
        User user = userFrom.decrypt(chatController.getPrivateKey());
        setMessage(user, message.decrypt(chatController.getPrivateKey(), user.getPublicKey()));
    }

    public void setMessage(User userFrom, Message message) throws RemoteException
    {
        this.chatController.receiveMessage(userFrom, message);
    }

    @Override
    public void disconnectChat(Sendable<User> userFrom) throws RemoteException
    {
        disconnectChat(userFrom.decrypt(chatController.getPrivateKey()));
    }

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