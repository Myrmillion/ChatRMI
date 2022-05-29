package ch.hesso.chat_rmi.jvmuser.gui;

import ch.hesso.chat_rmi.jvmuser.moo.*;
import ch.hesso.chat_rmi.jvmuser.gui.tools.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class JChat extends Box
{

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public JChat(User userLocal, User userRemote, String firstMessage)
    {
        super(BoxLayout.Y_AXIS);

        this.userLocal = userLocal;
        this.userRemote = userRemote;

        this.chatController = ChatController.getInstance();

        geometry();
        control();
        appearance();

        displayFirstMessage(firstMessage);
        displaySavedMessages();
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods							*|
	\*------------------------------------------------------------------*/

    public void setStopCallback(boolean stopCallback)
    {
        this.stopCallback = stopCallback;
    }

    public void updateGUI(Message message)
    {
        insertTextCustomized(this.jDisplayRemote, message.getText(), FONT_CHAT_SMALL, Color.WHITE, NICE_BLUE, message.isImportant(), false);
        insertTextCustomized(this.jDisplayLocal, "", FONT_CHAT_SMALL, Color.WHITE, TRANSPARENT, message.isImportant(), false);
    }

	/*------------------------------------------------------------------*\
	|*							Private Methods						    *|
	\*------------------------------------------------------------------*/

    private void disconnectChat(boolean needsRemoving)
    {
        this.chatController.closeChat(this.userRemote);

        if (needsRemoving)
        {
            this.chatController.removeUserFromUserChattingWith(this.userRemote);
        }

        this.stopCallback = true; // very important !!!
    }

    private void displayFirstMessage(String firstMessage)
    {
        insertTextCustomized(this.jDisplayRemote, firstMessage, FONT_CHAT_BIG, NICE_BLUE, TRANSPARENT, false, true);
        insertTextCustomized(this.jDisplayLocal, "", FONT_CHAT_BIG, Color.WHITE, TRANSPARENT, false, true);
    }

    private void displaySavedMessages()
    {
        // TODO : À l'ouverture de JChat, on utilise le chatController pour aller récupérer les messages qu'on a avec
        // TODO : le remote user et on les ajoute directement dans les JTextPane !

        List<Message> listMessage = this.chatController.retrieveSavedMessages();
    }

    private void insertTextCustomized(JTextPane jTextPane, String message, int fontSize, Color fontColor, Color backColor, boolean isImportant, boolean underlined)
    {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontSize(set, fontSize);
        StyleConstants.setForeground(set, fontColor);
        StyleConstants.setBackground(set, backColor);
        StyleConstants.setUnderline(set, underlined);

        StyledDocument doc = jTextPane.getStyledDocument();

        try
        {
            if (!message.isEmpty())
            {
                doc.insertString(doc.getLength(), " " + message + " ", set);

                if (isImportant)
                {
                    SimpleAttributeSet importantSet = new SimpleAttributeSet();
                    StyleConstants.setFontSize(importantSet, FONT_CHAT_IMPORTANT);
                    StyleConstants.setForeground(importantSet, NICE_RED);

                    doc.insertString(doc.getLength(), " !", importantSet);
                }
            }

            doc.insertString(doc.getLength(), "\n\n", set);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() ->
        {
            this.jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getMaximum());
        });
    }

    /*------------------------------*\
    |*	            GUI	         	*|
    \*------------------------------*/

    private void geometry()
    {
        this.jDisplayRemote = new JTextPane();
        this.jDisplayLocal = new JTextPane();
        this.jScrollPane = new JScrollPane(new JAllSpaceH(this.jDisplayRemote, this.jDisplayLocal));

        this.jDisconnect = new JButton("Disconnect");
        this.jMessage = new JTextField();
        this.jSend = new JButton("Send");
        this.jImportant = new JCheckBox("Important ?");

        Box boxH = new Box(BoxLayout.X_AXIS);
        boxH.add(this.jDisconnect);
        boxH.add(this.jMessage);

        JCentersV jCentersV = new JCentersV(this.jSend, this.jImportant);
        JComponents.setHeight(jCentersV, 50);

        boxH.add(jCentersV);

        add(this.jScrollPane);
        add(boxH);
    }

    private void control()
    {
        // Important (CheckBox)
        jImportant.addItemListener(e ->
        {
            jMessage.requestFocusInWindow();
        });

        // Send (Button)
        jSend.addActionListener(e ->
        {
            send();
        });

        jMessage.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == 10) // 10 is the 'Enter' key code
                {
                    send(e.isControlDown()); // whether Ctrl is hold message is being sent
                }
            }
        });

        // Disconnect (Button)
        jDisconnect.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());

        // Adding a listener to the Ancestor
        addAncestorListener(new AncestorAdapter()
        {
            // Called once the Ancestor is made visible (so we are sure it exists and is instantiated)
            @Override
            public void ancestorAdded(AncestorEvent event)
            {
                JChat source = (JChat) event.getSource();

                // Ancestor JFrame's default close operation
                ((JFrame) SwingUtilities.getWindowAncestor(source)).setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                // Ancestor Window "closed" behaviour (simply when dispose is called upon the window)
                SwingUtilities.getWindowAncestor(source).addWindowListener(new WindowAdapter()
                {
                    public void windowClosed(WindowEvent e)
                    {
                        if (!stopCallback)
                        {
                            disconnectChat(true);

                            // just in case, we never know, don't want to lose this piece of code :
                            // SwingUtilities.getWindowAncestor(source).dispatchEvent(new WindowEvent(SwingUtilities.getWindowAncestor(source), WindowEvent.WINDOW_CLOSING));
                        }
                    }
                });

                Runtime.getRuntime().addShutdownHook(new Thread(() ->
                {
                    if (!stopCallback) // very important !!!
                    {
                        disconnectChat(false);
                    }
                }));
            }
        });
    }

    private void send()
    {
        send(false);
    }

    private void send(boolean isCtrlHold)
    {
        if (!this.jMessage.getText().isBlank())
        {
            String text = jMessage.getText();
            boolean isImportant = jImportant.isSelected() || isCtrlHold;

            // Sending message with chatController
            this.chatController.sendMessage(new Message(this.userLocal, text, isImportant), this.userRemote);

            insertTextCustomized(this.jDisplayRemote, "", FONT_CHAT_SMALL, Color.WHITE, TRANSPARENT, false, false);
            insertTextCustomized(this.jDisplayLocal, text, FONT_CHAT_SMALL, Color.WHITE, NICE_ORANGE, isImportant, false);

            // Resetting the message area
            this.jMessage.setText("");
            this.jMessage.requestFocusInWindow();
        }
    }

    private void appearance()
    {
        // Display remote (JTextPane)
        this.jDisplayRemote.setEditable(false);
        alignTextInPane(this.jDisplayRemote, StyleConstants.ALIGN_LEFT);
        this.jDisplayRemote.setFont(new Font("SansSerif", Font.BOLD, JMain.FONT_TEXT_FIELD_SIZE));

        // Display local (JTextPane)
        this.jDisplayLocal.setEditable(false);
        alignTextInPane(this.jDisplayLocal, StyleConstants.ALIGN_RIGHT);
        this.jDisplayLocal.setFont(new Font("SansSerif", Font.BOLD, JMain.FONT_TEXT_FIELD_SIZE));

        // Disconnect (Button)
        this.jDisconnect.setFont(new Font(Font.SANS_SERIF, Font.BOLD, JMain.FONT_BUTTON_SIZE));
        JComponents.setHeight(this.jDisconnect, 50);
        JComponents.setWidth(this.jDisconnect, BTN_BIG_WIDTH);

        // Message (JTextField)
        this.jMessage.setHorizontalAlignment(SwingConstants.CENTER);
        this.jMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, JMain.FONT_TEXT_FIELD_SIZE));
        JComponents.setHeight(this.jMessage, 50);

        // Send (Button)
        this.jSend.setFont(new Font(Font.SANS_SERIF, Font.BOLD, JMain.FONT_BUTTON_SIZE));
        JComponents.setWidth(this.jSend, BTN_SMALL_WIDTH);

    }

    private void alignTextInPane(JTextPane jTextPane, int alignment)
    {
        StyledDocument doc = jTextPane.getStyledDocument();
        SimpleAttributeSet alignmentAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignmentAttribute, alignment);
        doc.setParagraphAttributes(0, doc.getLength(), alignmentAttribute, false);
    }

	/*------------------------------------------------------------------*\
	|*							Public Attributes						*|
	\*------------------------------------------------------------------*/

    // Inputs
    private final User userLocal;
    private final User userRemote;

    // Tools
    private final ChatController chatController;
    private boolean stopCallback;

    private JScrollPane jScrollPane;
    private JTextPane jDisplayLocal;
    private JTextPane jDisplayRemote;

    private JButton jDisconnect;
    private JTextField jMessage;
    private JButton jSend;
    private JCheckBox jImportant;

    /*------------------------------*\
    |*			  Static		   	*|
    \*------------------------------*/

    private static final int BTN_SMALL_WIDTH = 100;
    private static final int BTN_BIG_WIDTH = 125;

    private static final int FONT_CHAT_SMALL = 25;
    private static final int FONT_CHAT_BIG = 28;
    private static final int FONT_CHAT_IMPORTANT = 28;

    private static final Color NICE_BLUE = new Color(81, 160, 213);
    private static final Color NICE_ORANGE = new Color(255, 149, 0);
    private static final Color NICE_RED = new Color(199, 55, 47);
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
}
