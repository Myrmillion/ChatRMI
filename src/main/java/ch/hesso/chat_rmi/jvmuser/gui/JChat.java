package ch.hesso.chat_rmi.jvmuser.gui;

import ch.hesso.chat_rmi.jvmuser.moo.User;
import ch.hesso.chat_rmi.jvmuser.gui.tools.*;
import ch.hesso.chat_rmi.jvmuser.moo.ChatController;
import ch.hesso.chat_rmi.jvmuser.moo.Chat_I;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class JChat extends Box
{

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public JChat(User userLocal, String firstMessage, Chat_I chatRemote)
    {
        super(BoxLayout.Y_AXIS);

        this.userLocal = userLocal;
        this.chatRemote = chatRemote;

        this.chatController = ChatController.getInstance();

        geometry();
        control();
        appearance();

        displayFirstMessage(firstMessage);
    }

	/*------------------------------------------------------------------*\
	|*							Public Methods							*|
	\*------------------------------------------------------------------*/

    public void setStopCallback(boolean stopCallback)
    {
        this.stopCallback = stopCallback;
    }

    public void updateGUI(String message)
    {
        insertTextCustomized(this.jDisplayRemote, message, TEXT_CHAT_SMALL, Color.WHITE, NICE_BLUE, false);
        insertTextCustomized(this.jDisplayLocal, "", TEXT_CHAT_SMALL, Color.WHITE, NICE_ORANGE, false);
    }

	/*------------------------------------------------------------------*\
	|*							Private Methods						    *|
	\*------------------------------------------------------------------*/

    private void disconnectChat()
    {
        try
        {
            chatRemote.disconnectChat(this.userLocal);

            this.stopCallback = true; // very important !!!
        }
        catch (RemoteException ex)
        {
            System.err.println("[JChat] : AncestorWindow-windowClosing : fail");
            ex.printStackTrace();
        }
    }

    private void displayFirstMessage(String firstMessage)
    {
        insertTextCustomized(this.jDisplayRemote, firstMessage, TEXT_CHAT_BIG, NICE_BLUE, Color.WHITE, true);
        insertTextCustomized(this.jDisplayLocal, "", TEXT_CHAT_BIG, Color.WHITE, NICE_ORANGE, true);
    }

    private void insertTextCustomized(JTextPane jTextPane, String message, int fontSize, Color fontColor, Color backColor, boolean underlined)
    {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontSize(set, fontSize);
        StyleConstants.setForeground(set, fontColor);
        StyleConstants.setBackground(set, backColor);
        StyleConstants.setUnderline(set, underlined);

        StyledDocument doc = jTextPane.getStyledDocument();
        try
        {
            doc.insertString(doc.getLength(), message + "\n\n", set);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }

        this.jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getMaximum());
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

        Box boxH = new Box(BoxLayout.X_AXIS);
        boxH.add(this.jDisconnect);
        boxH.add(this.jMessage);
        boxH.add(this.jSend);

        add(this.jScrollPane);
        add(boxH);
    }

    private void control()
    {
        // Send (Button)
        jSend.addActionListener(e ->
        {
            try
            {
                if (!this.jMessage.getText().isEmpty())
                {
                    String message = jMessage.getText();

                    // Sending message with chatRemote
                    this.chatRemote.setMessage(this.userLocal, message);
                    insertTextCustomized(this.jDisplayRemote, "", TEXT_CHAT_SMALL, Color.WHITE, NICE_BLUE, false);
                    insertTextCustomized(this.jDisplayLocal, message, TEXT_CHAT_SMALL, Color.WHITE, NICE_ORANGE, false);

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

        // Disconnect (Button)
        jDisconnect.addActionListener(e ->
        {
            SwingUtilities.getWindowAncestor(this).dispose();
        });

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
                            disconnectChat();
                            chatController.removeUserInConnectedUsers(source);

                            // just in case, we never know, don't want to lose this piece of code :
                            // SwingUtilities.getWindowAncestor(source).dispatchEvent(new WindowEvent(SwingUtilities.getWindowAncestor(source), WindowEvent.WINDOW_CLOSING));
                        }
                    }
                });

                Runtime.getRuntime().addShutdownHook(new Thread(() ->
                {
                    if (!stopCallback) // very important !!!
                    {
                        disconnectChat();
                    }
                }));
            }
        });
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

        // Message (JTextField)
        this.jMessage.setHorizontalAlignment(SwingConstants.CENTER);
        this.jMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, JMain.FONT_TEXT_FIELD_SIZE));
        JComponents.setHeight(this.jMessage, 50);

        // Send (Button)
        this.jSend.setFont(new Font(Font.SANS_SERIF, Font.BOLD, JMain.FONT_BUTTON_SIZE));
        JComponents.setHeight(this.jSend, 50);
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
    private User userLocal;
    private Chat_I chatRemote;

    // Tools
    private ChatController chatController;
    private boolean stopCallback;

    private JScrollPane jScrollPane;
    private JTextPane jDisplayLocal;
    private JTextPane jDisplayRemote;

    private JButton jDisconnect;
    private JTextField jMessage;
    private JButton jSend;

    /*------------------------------*\
    |*			  Static		   	*|
    \*------------------------------*/

    private static final int TEXT_CHAT_SMALL = 25;
    private static final int TEXT_CHAT_BIG = 28;

    private static final Color NICE_BLUE = new Color(81, 160, 213);
    private static final Color NICE_ORANGE = new Color(255, 149, 0);
}
