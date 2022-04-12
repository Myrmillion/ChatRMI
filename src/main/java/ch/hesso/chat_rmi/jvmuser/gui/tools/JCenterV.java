
package ch.hesso.chat_rmi.jvmuser.gui.tools;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

public class JCenterV extends Box
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JCenterV(JComponent jComponent)
		{
		super(BoxLayout.Y_AXIS);

		this.jComponent = jComponent;

		geometrie();
		controle();
		apparence();
		}

	/*------------------------------------------------------------------*\
	|*							Methodes Public							*|
	\*------------------------------------------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometrie()
		{
		add(createVerticalGlue());
		add(jComponent);
		add(createVerticalGlue());
		}

	private void controle()
		{
		// rien
		}

	private void apparence()
		{
		// rien
		}

	/*------------------------------------------------------------------*\
	|*							Attributs Private						*|
	\*------------------------------------------------------------------*/

	// inputs
	private JComponent jComponent;
	}

