package ch.hesso.chat_rmi.jvmuser.gui;

import ch.hesso.chat_rmi.SettingsRMI;
import ch.hesso.chat_rmi.jvmuser.db.MessageEntity;
import ch.hesso.chat_rmi.jvmuser.moo.User;
import ch.hesso.chat_rmi.jvmuser.gui.tools.AncestorAdapter;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JCenterH;
import ch.hesso.chat_rmi.jvmuser.gui.tools.JComponents;
import ch.hesso.chat_rmi.jvmuser.moo.ChatController;

import javax.persistence.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class JMain extends Box {

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public JMain() {
        super(BoxLayout.Y_AXIS);

        this.testDB();
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

    private void testDB() {
        // Open a database connection
        // (create a new database if it doesn't exist yet):
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("$objectdb/db/messages.odb");
        EntityManager em = emf.createEntityManager();

        // CLEAR ALL THE DATABASE
        em.getTransaction().begin();
        Query q = em.createQuery("DELETE FROM MessageEntity");
        q.executeUpdate();
        em.getTransaction().commit();

        em.getTransaction().begin();
        MessageEntity m1 = new MessageEntity(12, 16, "Hello world", LocalDate.now().toString(), LocalTime.now().toString());
        MessageEntity m2 = new MessageEntity(16, 12, "PTDR T KI?", LocalDate.now().toString(), LocalTime.now().toString());
        em.persist(m1);
        em.persist(m2);
        em.getTransaction().commit();

        // Find the number of Point objects in the database:
        Query q1 = em.createQuery("SELECT COUNT(m) FROM MessageEntity m");
        System.out.println("Total MessageEntity: " + q1.getSingleResult());

        // Find the average X value:
        Query q2 = em.createQuery("SELECT AVG(m.userFromId) FROM MessageEntity m");
        System.out.println("Average userFromId: " + q2.getSingleResult());

        // Retrieve all the Point objects from the database:
        TypedQuery<MessageEntity> query = em.createQuery("SELECT m FROM MessageEntity m", MessageEntity.class);
        List<MessageEntity> results = query.getResultList();
        for (MessageEntity m : results) {
            System.out.println(m);
        }

        // Close the database connection:
        em.close();
        emf.close();
    }

    private void updateListModel() {
        this.listAvailableUsers.clear();

        try {
            for (User user : this.chatController.getListAvailableUsers()) {
                this.listAvailableUsers.addElement(user);
            }
        } catch (RemoteException e) {
            System.err.println("[JMain] : updateListModel : fail");
            e.printStackTrace();
        }
    }

    /*------------------------------*\
    |*	            GUI	         	*|
    \*------------------------------*/

    private void geometry() {
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

    private void control() {
        // Create (Button)
        jCreate.addActionListener(e ->
        {
            if (!this.jUsername.getText().isEmpty()) {
                this.jLabelUsername.setEnabled(false);
                this.jUsername.setEnabled(false);
                this.jCreate.setEnabled(false);

                this.jLabelChoice.setEnabled(true);
                this.jAvailableUsers.setEnabled(true);
                this.jResynchronize.setEnabled(true);

                try {
                    this.chatController.prepareRMI(this.jUsername.getText());
                }
                catch (RemoteException | MalformedURLException | NoSuchAlgorithmException ex)
                {
                    System.err.println("[JMain] : jCreate-actionListener : fail : " + SettingsRMI.REGISTRY_RMI_URL);
                    System.err.println("[JMain] : jCreate-actionListener : Please verify that the Registry server is started !");
                    ex.printStackTrace();

                    this.jUsername.setEnabled(true);
                    this.jCreate.setEnabled(true);
                    this.jResynchronize.setEnabled(false);
                }

                updateListModel();
            } else {
                this.jUsername.requestFocusInWindow();
            }
        });

        // Select Available User (JList<User>)
        jAvailableUsers.addListSelectionListener(e ->
        {
            this.jAskChat.setEnabled(!jAvailableUsers.isSelectionEmpty());
        });

        // Resynchronize (Button)
        jResynchronize.addActionListener(e -> updateListModel());

        // Connect (Button)
        jAskChat.addActionListener(e ->
        {
            this.chatController.askConnection(jAvailableUsers.getSelectedValue());
        });

        // Adding a listener to the Ancestor
        addAncestorListener(new AncestorAdapter() {
            // Called once the Ancestor is made visible (so we are sure it exists and is instantiated)
            @Override
            public void ancestorAdded(AncestorEvent event) {
                JMain source = (JMain) event.getSource();

                // Ancestor JFrame's default close operation
                ((JFrame) SwingUtilities.getWindowAncestor(source)).setDefaultCloseOperation(EXIT_ON_CLOSE);

                // Ancestor Window "closed" behaviour (simply when dispose is called upon the window)
                SwingUtilities.getWindowAncestor(source).addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        try {
                            chatController.removeLocalUserFromRegistry();
                        } catch (RemoteException ex) {
                            System.err.println("[JMain] : AncestorWindow-windowClosing : fail");
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void appearance() {
        // Labels
        this.jLabelUsername.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_TITLE_SIZE));
        this.jLabelChoice.setEnabled(false);
        this.jLabelChoice.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_TITLE_SIZE));

        // Username (JTextField)
        JComponents.setHeight(this.jUsername, 50);
        JComponents.setWidth(this.jUsername, 400);
        this.jUsername.setHorizontalAlignment(SwingConstants.CENTER);
        this.jUsername.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_TEXT_FIELD_SIZE));

        // Create (Button)
        this.jCreate.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_BUTTON_SIZE));
        JComponents.setHeight(this.jCreate, 50);
        JComponents.setWidth(this.jCreate, 250);

        // AvailableUsers (JList<User>)
        this.jAvailableUsers.setEnabled(false);
        this.jAvailableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jAvailableUsers.setLayoutOrientation(JList.VERTICAL_WRAP);
        this.jAvailableUsers.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, FONT_TEXT_FIELD_SIZE));
        JComponents.setHeight(this.jAvailableUsers, 100);
        JComponents.setWidth(this.jAvailableUsers, 400);

        // Resynchronize (Button)
        this.jResynchronize.setEnabled(false);
        this.jResynchronize.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_BUTTON_SIZE));
        JComponents.setHeight(this.jResynchronize, 50);
        JComponents.setWidth(this.jResynchronize, 250);

        // Connect (Button)
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
    private final ChatController chatController;
    private final DefaultListModel<User> listAvailableUsers;

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
