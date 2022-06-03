package ch.hesso.chat_rmi.jvmuser.gui;

import ch.hesso.chat_rmi.SettingsRMI;
import ch.hesso.chat_rmi.jvmuser.gui.tools.AncestorAdapter;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JCenterH;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JComponents;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JFrameChat;
import ch.hesso.chat_rmi.jvmuser.moo.ChatController;
import ch.hesso.chat_rmi.jvmuser.moo.User;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class JMain extends Box
{

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public JMain(String username, char[] password)
    {
        super(BoxLayout.Y_AXIS);

        this.chatController = ChatController.getInstance();
        this.listAvailableUsers = new DefaultListModel<User>();

        geometry();
        control();
        appearance();

        SwingUtilities.invokeLater(() -> this.chatController.setParentFrame(this));
        login(username, password);

    }

	/*------------------------------------------------------------------*\
	|*							Public Methods							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Private Methods						    *|
	\*------------------------------------------------------------------*/

    private void updateListModel()
    {


        try
        {
            if (this.chatController.getListAvailableUsers().size() == this.listAvailableUsers.size())
            {
                for (int i = 0; i < this.listAvailableUsers.size(); i++)
                {
                    if (this.chatController.getListAvailableUsers().get(i).isEquals(this.listAvailableUsers.get(i)))
                    {
                        return;
                    }
                }
            }
            this.listAvailableUsers.clear();

            for (User user : this.chatController.getListAvailableUsers())
            {
                this.listAvailableUsers.addElement(user);
            }
        }
        catch (RemoteException e)
        {
            System.err.println("[JMain] : updateListModel : fail");
            e.printStackTrace();
        }
    }

    /*------------------------------*\
    |*	            GUI	         	*|
    \*------------------------------*/

    private void geometry()
    {
        this.jConnectedAs = new JLabel("Connected as ");
        this.jLabelChoice = new JLabel("Choose a user to chat with");
        this.jAvailableUsers = new JList<User>(this.listAvailableUsers);
        this.jAskChat = new JButton("> ASK FOR A CHAT <");
        this.jDisconnect = new JButton("Disconnect");

        add(createVerticalStrut(STRUT_BIG_SIZE));
        add(new JCenterH(this.jConnectedAs));
        add(createVerticalGlue());
        add(new JCenterH(this.jLabelChoice));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jAvailableUsers));
        add(createVerticalStrut(STRUT_BIG_SIZE));
        add(new JCenterH(this.jAskChat));
        add(createVerticalGlue());
        add(new JCenterH(this.jDisconnect));
    }

    private void control()
    {
        // Select Available User (JList<User>)
        jAvailableUsers.addListSelectionListener(e ->
        {
            this.jAskChat.setEnabled(!jAvailableUsers.isSelectionEmpty());
        });

        // Connect (Button)
        jAskChat.addActionListener(e ->
        {
            this.chatController.askConnection(jAvailableUsers.getSelectedValue());
        });

        // Adding a listener to the Ancestor
        addAncestorListener(new AncestorAdapter()
        {
            // Called once the Ancestor is made visible (so we are sure it exists and is instantiated)
            @Override
            public void ancestorAdded(AncestorEvent event)
            {
                JMain source = (JMain) event.getSource();

                // Ancestor JFrame's default close operation
                ((JFrame) SwingUtilities.getWindowAncestor(source)).setDefaultCloseOperation(EXIT_ON_CLOSE);

                // Ancestor Window "closed" behaviour (simply when exit is called upon the window)
                SwingUtilities.getWindowAncestor(source).addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        try
                        {
                            updateInterval.stop();
                            chatController.removeLocalUserFromRegistry();
                        }
                        catch (RemoteException ex)
                        {
                            System.err.println("[JMain] : AncestorWindow-windowClosing : fail");
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });

        jDisconnect.addActionListener(e ->
        {
            try
            {
                updateInterval.stop();
                chatController.removeLocalUserFromRegistry();
            }
            catch (RemoteException ex)
            {
                System.err.println("[JMain] : AncestorWindow-windowClosing : fail");
                ex.printStackTrace();
            }
            JFrameChat.mainJFrame.changePage(new JLogin());
        });

        updateInterval = new Timer(1000, e ->
        {
            updateListModel();
        });
        updateInterval.start();

    }

    private void appearance()
    {
        // Labels
        this.jConnectedAs.setHorizontalAlignment(SwingConstants.CENTER);
        this.jLabelChoice.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_TITLE_SIZE));

        // AvailableUsers (JList<User>)
        this.jAvailableUsers.setEnabled(false);
        this.jAvailableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jAvailableUsers.setLayoutOrientation(JList.VERTICAL_WRAP);
        this.jAvailableUsers.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_TEXT_FIELD_SIZE));
        JComponents.setHeight(this.jAvailableUsers, 100);
        JComponents.setWidth(this.jAvailableUsers, 400);

        // Connect (Button)
        this.jAskChat.setEnabled(false);
        this.jAskChat.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_BUTTON_SIZE));
        JComponents.setHeight(this.jAskChat, 50);
        JComponents.setWidth(this.jAskChat, 250);
    }

    private void login(String username, char[] password)
    {
        this.jLabelChoice.setEnabled(true);
        this.jAvailableUsers.setEnabled(true);

        try
        {
            this.chatController.prepareRMI(username, password);
            User u = chatController.getUserLocal();
            this.jConnectedAs.setText("<html><div style='text-align: center; font-size: 14px'>Connected as <b>" + u.getUsername() + "</b><span style='color: gray; font-size: 6px'>" + u.getId() + "</span></div></html>");
        }
        catch (Exception ex)
        {
            System.err.println("[JMain] : jCreate-actionListener : fail : " + SettingsRMI.REGISTRY_RMI_URL);
            System.err.println("[JMain] : jCreate-actionListener : Please verify that the Registry server is started !");
            ex.printStackTrace();
        }

        updateListModel();
    }

	/*------------------------------------------------------------------*\
	|*							Public Attributes						*|
	\*------------------------------------------------------------------*/

    // Inputs

    // Tools
    private final ChatController chatController;
    private final DefaultListModel<User> listAvailableUsers;

    private JLabel jConnectedAs;
    private JLabel jLabelChoice;
    private JList<User> jAvailableUsers;

    private JButton jAskChat;
    private JButton jDisconnect;

    private Timer updateInterval;

    /*------------------------------*\
    |*			  Static		   	*|
    \*------------------------------*/

    public static final int FONT_TITLE_SIZE = 24;
    public static final int FONT_TEXT_FIELD_SIZE = 20;
    public static final int FONT_BUTTON_SIZE = 14;

    private final static int STRUT_SMALL_SIZE = 4;
    private final static int STRUT_BIG_SIZE = 20;
}
