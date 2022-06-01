package ch.hesso.chat_rmi.jvmuser.gui.tools;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class JFrameChat extends JFrame
{

	/*------------------------------------------------------------------*\
	|*							Constructors							*|
	\*------------------------------------------------------------------*/

    public JFrameChat(JComponent jComponent, boolean isFullScreen, String title, boolean isVisible)
    {
        super(title);

        this.jComponent = jComponent;

        geometry();
        control();
        appearance(isFullScreen, isVisible);
    }

    public JFrameChat(JComponent jcomponent, String userFrom, String userTo)
    {
        this(jcomponent, false, "[" + userFrom + "]" + " Chatting with : " + userTo, false);
    }

    public JFrameChat(JComponent jcomponent, String title)
    {
        this(jcomponent, false, title, true);
    }

	/*------------------------------------------------------------------*\
	|*							Public Methodes							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Private Methods							*|
	\*------------------------------------------------------------------*/

    private void geometry()
    {
        //by default jframe use a borderlayout in the middle !
        {
            add(jComponent);
        }
    }

    private void control()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void appearance(boolean isFullScreen, boolean isVisible)
    {
        if (isFullScreen)
        {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(dim);
            setUndecorated(true);
        }
        else
        {
            setSize(1920 / 2, 1080 / 2);
        }

        setLocationRelativeTo(null); // frame centrer
        setVisible(isVisible); // WILL BE MADE VISIBLE SOMEWHERE ELSE THAN INSIDE THE CLASS
    }

	/*------------------------------------------------------------------*\
	|*							Private Attributes						*|
	\*------------------------------------------------------------------*/

    // Inputs
    private final JComponent jComponent;

    // Tools
}
