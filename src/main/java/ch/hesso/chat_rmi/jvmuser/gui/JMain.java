package ch.hesso.chat_rmi.jvmuser.gui;

import ch.hesso.chat_rmi.SettingsRMI;
import ch.hesso.chat_rmi.jvmuser.moo.User;
import ch.hesso.chat_rmi.jvmuser.gui.tools.AncestorAdapter;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JCenterH;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JComponents;
import ch.hesso.chat_rmi.jvmuser.moo.ChatController;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class JMain extends Box
{

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public JMain()
    {
        super(BoxLayout.Y_AXIS);

        this.chatController = ChatController.getInstance();
        this.listAvailableUsers = new DefaultListModel<User>();

        geometry();
        control();
        appearance();
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Private Methods						    *|
	\*------------------------------------------------------------------*/

    private void updateListModel()
    {
        this.listAvailableUsers.clear();

        try
        {
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
        this.jLabelUsername = new JLabel("Choose a username");
        this.jLabelChoice = new JLabel("Choose a user to chat with");
        this.jUsername = new JTextField();
        this.jCreate = new JButton("Create");
        this.jResynchronize = new JButton("Re-Synchronize");
        this.jAskChat = new JButton("> ASK FOR A CHAT <");
        this.jAvailableUsers = new JList<User>(this.listAvailableUsers);

        add(createVerticalGlue());
        add(new JCenterH(this.jLabelUsername));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jUsername));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jCreate));
        add(createVerticalStrut(STRUT_BIG_SIZE));
        add(new JCenterH(this.jLabelChoice));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jAvailableUsers));
        add(createVerticalStrut(STRUT_SMALL_SIZE));
        add(new JCenterH(this.jResynchronize));
        add(createVerticalStrut(STRUT_BIG_SIZE));
        add(new JCenterH(this.jAskChat));
        add(createVerticalGlue());
    }

    private void control()
    {
        // Create Button
        jCreate.addActionListener(e ->
        {
            if (!this.jUsername.getText().isEmpty())
            {
                this.jLabelUsername.setEnabled(false);
                this.jUsername.setEnabled(false);
                this.jCreate.setEnabled(false);

                this.jLabelChoice.setEnabled(true);
                this.jAvailableUsers.setEnabled(true);
                this.jResynchronize.setEnabled(true);

                try
                {
                    this.chatController.create(this.jUsername.getText());
                }
                catch (RemoteException | MalformedURLException ex)
                {
                    System.err.println("[JMain] : shareAndUpdateRegistry : fail : " + SettingsRMI.REGISTRY_RMI_URL);
                    System.err.println("[JMain] : shareAndUpdateRegistry : Please verify that the Registry server is started !");
                    ex.printStackTrace();

                    this.jUsername.setEnabled(true);
                    this.jCreate.setEnabled(true);
                    this.jResynchronize.setEnabled(false);
                }

                updateListModel();
            }
            else
            {
                this.jUsername.requestFocusInWindow();
            }
        });

        // List Select
        jAvailableUsers.addListSelectionListener(e ->
        {
            this.jAskChat.setEnabled(!jAvailableUsers.isSelectionEmpty());
        });

        // Resynchronize Button
        jResynchronize.addActionListener(e ->
        {
            updateListModel();
        });

        // Connect Button
        jAskChat.addActionListener(e ->
        {
            this.chatController.askConnection(jAvailableUsers.getSelectedValue());
        });

        // To add a listener directly on the Ancestor
        addAncestorListener(new AncestorAdapter()
        {
            // Called once the Ancestor is made visible (so we are sure it exists and is instantiated)
            @Override
            public void ancestorAdded(AncestorEvent event)
            {
                JMain source = (JMain) event.getSource();

                // Ancestor JFrame's default close operation
                ((JFrame) SwingUtilities.getWindowAncestor(source)).setDefaultCloseOperation(EXIT_ON_CLOSE);

                // Ancestor Window "closed" behaviour (simply when dispose is called upon the window)
                SwingUtilities.getWindowAncestor(source).addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        try
                        {
                            chatController.removeUserInRegistry();
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
    }

    private void appearance()
    {
        // Labels
        this.jLabelUsername.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_TITLE_SIZE));

        this.jLabelChoice.setEnabled(false);
        this.jLabelChoice.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_TITLE_SIZE));

        // Username
        JComponents.setHeight(this.jUsername, 50);
        JComponents.setWidth(this.jUsername, 400);

        this.jUsername.setHorizontalAlignment(SwingConstants.CENTER);
        this.jUsername.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_TEXT_FIELD_SIZE));

        // Create
        this.jCreate.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_BUTTON_SIZE));

        JComponents.setHeight(this.jCreate, 50);
        JComponents.setWidth(this.jCreate, 250);

        // AvailableUsers
        this.jAvailableUsers.setEnabled(false);
        this.jAvailableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jAvailableUsers.setLayoutOrientation(JList.VERTICAL_WRAP);
        this.jAvailableUsers.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_TEXT_FIELD_SIZE));

        JComponents.setHeight(this.jAvailableUsers, 100);
        JComponents.setWidth(this.jAvailableUsers, 400);

        // Resynchronize
        this.jResynchronize.setEnabled(false);
        this.jResynchronize.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_BUTTON_SIZE));

        JComponents.setHeight(this.jResynchronize, 50);
        JComponents.setWidth(this.jResynchronize, 250);

        // Connect
        this.jAskChat.setEnabled(false);
        this.jAskChat.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_BUTTON_SIZE));

        JComponents.setHeight(this.jAskChat, 50);
        JComponents.setWidth(this.jAskChat, 250);
    }

	/*------------------------------------------------------------------*\
	|*							Public Attributes						*|
	\*------------------------------------------------------------------*/

    // Inputs

    // Tools
    private ChatController chatController;
    private DefaultListModel<User> listAvailableUsers;

    private JLabel jLabelUsername;
    private JTextField jUsername;
    private JButton jCreate;

    private JLabel jLabelChoice;
    private JList<User> jAvailableUsers;
    private JButton jResynchronize;

    private JButton jAskChat;

    /*------------------------------*\
    |*			  Static		   	*|
    \*------------------------------*/

    public static final int FONT_TITLE_SIZE = 22;
    public static final int FONT_TEXT_FIELD_SIZE = 20;
    public static final int FONT_BUTTON_SIZE = 14;

    private final static int STRUT_SMALL_SIZE = 6;
    private final static int STRUT_BIG_SIZE = 40;
}
