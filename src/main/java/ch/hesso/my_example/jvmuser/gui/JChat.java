package ch.hesso.my_example.jvmuser.gui;

import ch.hearc.tools.rmi.RmiURL;
import ch.hearc.tools.rmi.Rmis;
import ch.hesso.my_example.SettingsRMI;
import ch.hesso.my_example.UserURL;
import ch.hesso.my_example.jvmregistry.moo.RegistryRemote_I;
import ch.hesso.my_example.jvmuser.gui.tools.JCenterH;
import ch.hesso.my_example.jvmuser.gui.tools.JCentersH;
import ch.hesso.my_example.jvmuser.gui.tools.JComponents;
import ch.hesso.my_example.Message;
import ch.hesso.my_example.jvmuser.gui.tools.JFrameBaseLine;
import ch.hesso.my_example.jvmuser.moo.Chat;
import ch.hesso.my_example.jvmuser.moo.ChatRemote_I;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public class JChat extends JPanel
{

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public JChat()
    {
        this.listOtherUsers = new DefaultListModel<UserURL>();

        geometry();
        control();
        appearance();
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods							*|
	\*------------------------------------------------------------------*/

    /*------------------------------*\
    |*	            GUI	         	*|
    \*------------------------------*/

    public void updateGUI(List<Message> listMessage)
    {
        jDisplay.setText("");
        Style style = jDisplay.addStyle("", null);
        StyledDocument doc = jDisplay.getStyledDocument();

        for (Message message : listMessage)
        {
            StyleConstants.setForeground(style, (message.getUsername().equals(this.username)) ? Color.BLUE : Color.RED);
            try
            {
                // Username
                StyleConstants.setBold(style, true);
                StyleConstants.setUnderline(style, true);
                doc.insertString(doc.getLength(), message.getUsername() + " :", style);

                // Text message
                StyleConstants.setBold(style, false);
                StyleConstants.setUnderline(style, false);
                doc.insertString(doc.getLength(), " " + message.getText() + "\n", style);
            }
            catch (BadLocationException e)
            {
                System.err.println("[JChat] : updateGUI : fail : string insertion error in TextPane");
                e.printStackTrace();
            }
        }
    }

	/*------------------------------------------------------------------*\
	|*							Private Methods						    *|
	\*------------------------------------------------------------------*/

    public void updateListModel()
    {
        try
        {
            this.listOtherUsers.clear();

            for(UserURL userURL : this.registry.getListUserURL().stream().parallel().filter(url -> !url.toString().equals(this.username)).toList())
            {
                this.listOtherUsers.addElement(userURL);
            }
        }
        catch (RemoteException e)
        {
            System.err.println("[JChat] : updateListModel : fail");
            e.printStackTrace();
        }
    }

    /*------------------------------*\
    |*	            GUI	         	*|
    \*------------------------------*/

    private void geometry()
    {
        this.jCreate = new JButton("Create user");
        this.jConnect = new JButton("Connect");
        this.jRefresh = new JButton("Refresh");
        this.jSend = new JButton("Send");
        this.jMessage = new JTextPane();
        this.jUsername = new JTextPane();
        this.jDisplay = new JTextPane();
        this.jOtherUsers = new JList<>(this.listOtherUsers);

        Box boxV = new Box(BoxLayout.Y_AXIS);
        boxV.add(Box.createVerticalStrut(15));
        boxV.add(new JCenterH(this.jUsername));
        boxV.add(new JCenterH(this.jCreate));
        boxV.add(Box.createVerticalGlue());
        boxV.add(new JCenterH(this.jOtherUsers));
        boxV.add(new JCentersH(this.jRefresh, this.jConnect));
        boxV.add(Box.createVerticalGlue());
        boxV.add(new JCenterH(this.jSend));
        boxV.add(Box.createVerticalStrut(15));

        setLayout(new BorderLayout());

        add(boxV, BorderLayout.EAST);
        add(this.jMessage, BorderLayout.SOUTH);
        add(new JScrollPane(this.jDisplay), BorderLayout.CENTER);
    }

    private void control()
    {
        // Create Button
        jCreate.addActionListener(e ->
        {
            this.username = this.jUsername.getText();
            this.jUsername.setEnabled(false);

            this.jCreate.setEnabled(false);
            this.jRefresh.setEnabled(true);

            shareAndInform();
            updateListModel();
        });

        // List Select
        jOtherUsers.addListSelectionListener(e -> {
            this.remoteURL = jOtherUsers.getSelectedValue().getRmiURL();
            this.jConnect.setEnabled(true);
        });

        // Refresh Button
        jRefresh.addActionListener(e ->
        {
            updateListModel();
        });

        // Connect Button
        jConnect.addActionListener(e ->
        {
            connectionChatRemote(this.remoteURL);

            this.jMessage.setEnabled(true);
            this.jSend.setEnabled(true);
        });

        // Send Button
        jSend.addActionListener(e ->
        {
            try
            {
                if (!this.jMessage.getText().isEmpty())
                {
                    // Creating a message instance
                    Message message = new Message(this.username, jMessage.getText());

                    // Updating both remote and local list of messages
                    this.chatRemote.addMessage(message);
                    this.chatLocal.addMessage(message);

                    // Resetting the message area
                    this.jMessage.setText("");
                    this.jMessage.requestFocusInWindow();
                }
            }
            catch (RemoteException ex)
            {
                System.err.println("[JChat] : jSend-actionListener : fail");
                ex.printStackTrace();
            }
        });
    }

    private void appearance()
    {
        this.jDisplay.setEditable(false);
        this.jConnect.setEnabled(false);
        this.jSend.setEnabled(false);
        this.jMessage.setEnabled(false);
        this.jRefresh.setEnabled(false);

        // Message
        JComponents.setHeight(this.jMessage, 50);
        this.jMessage.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.BLACK));
        this.jMessage.setFont(new Font("SansSerif", Font.BOLD, 15));

        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        this.jMessage.getStyledDocument().setParagraphAttributes(0, this.jMessage.getStyledDocument().getLength(), center, false);

        // Display
        this.jDisplay.setFont(new Font("SansSerif", Font.PLAIN, 20));

        // Username
        JComponents.setHeight(this.jUsername, 50);
        this.jUsername.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

        // List
        this.jOtherUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jOtherUsers.setLayoutOrientation(JList.VERTICAL);
    }

    /*------------------------------*\
    |*	            RMI	         	*|
    \*------------------------------*/

    private void shareAndInform()
    {
        // Share
        {
            // Create the localURL (time is for Unicity)
            this.localURL = SettingsRMI.CREATE_RMI_URL(this.username + (new Date()).getTime());

            // Create the local chat
            this.chatLocal = new Chat(this);

            // Share the chat with RMI on localURL
            shareChat();
        }

        // Inform
        {
            try
            {
                // Connect to registry
                this.registry = (RegistryRemote_I) Rmis.connectRemoteObjectSync(SettingsRMI.REGISTRY_RMI_URL);

                // Add localURL to the shared registry
                this.registry.addUserURL(new UserURL(this.username, this.localURL));
            }
            catch (RemoteException | MalformedURLException e)
            {
                System.err.println("[JChat] : shareAndInform : fail : " + SettingsRMI.REGISTRY_RMI_URL);
                e.printStackTrace();

                this.jUsername.setEnabled(true);
                this.jCreate.setEnabled(true);
                this.jRefresh.setEnabled(false);
            }
        }
    }

    /*-------------*\
    |*	 Server    *|
    \*-------------*/

    private void shareChat()
    {
        try
        {
            Rmis.shareObject(this.chatLocal, this.localURL);
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[JChat] : shareChat : fail : " + this.localURL);
            e.printStackTrace();
        }
    }

    /*-------------*\
    |*	 Client    *|
    \*-------------*/

    private void connectionChatRemote(RmiURL remoteURL)
    {
        try
        {
            this.chatRemote = (ChatRemote_I) Rmis.connectRemoteObjectSync(remoteURL);
        }
        catch (RemoteException | MalformedURLException e)
        {
            System.err.println("[JChat] : connectionChatRemote : fail : " + remoteURL);
            e.printStackTrace();
        }
    }

	/*------------------------------------------------------------------*\
	|*							Public Attributes						*|
	\*------------------------------------------------------------------*/

    // Inputs
    private RmiURL localURL;
    private RmiURL remoteURL;

    // Tools
    private String username;

    private RegistryRemote_I registry;

    private ChatRemote_I chatLocal;
    private ChatRemote_I chatRemote;

    private JList<UserURL> jOtherUsers;
    private DefaultListModel<UserURL> listOtherUsers;

    private JButton jCreate;
    private JButton jSend;
    private JButton jConnect;
    private JButton jRefresh;

    private JTextPane jDisplay;
    private JTextPane jMessage;
    private JTextPane jUsername;
}
