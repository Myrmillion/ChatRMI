package ch.hesso.chat_rmi.jvmuser.gui.tools;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class JFrameChat extends JFrame
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JFrameChat(JComponent jcomponent, boolean isFullScreen, String title)
	{
		super(title);

		this.jcomponent = jcomponent;

		geometry();
		control();
		appearance(isFullScreen);
	}

	public JFrameChat(JComponent jcomponent, String title)
	{
		this(jcomponent, false, "Chatting with " + title);
	}

	public JFrameChat(JComponent jcomponent)
	{
		this(jcomponent, false, "default name");
	}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometry()
	{
		//by default jframe use a borderlayout in the middle !
		{
			add(jcomponent);
		}
	}

	private void control()
	{
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void appearance(boolean isFullScreen)
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
		setVisible(true); // last!
	}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// Inputs
	private JComponent jcomponent;

	// Tools

}
