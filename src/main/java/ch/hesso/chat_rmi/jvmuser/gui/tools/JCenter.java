
package ch.hesso.chat_rmi.jvmuser.gui.tools;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

public class JCenter extends Box
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JCenter(JComponent jComponent)
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

	/*------------------------------*\
	|*				Get				*|
	\*------------------------------*/

	/*------------------------------------------------------------------*\
	|*							Methodes Private						*|
	\*------------------------------------------------------------------*/

	private void geometrie()
		{
		boxH = createHorizontalBox();

		boxH.add(createHorizontalGlue());
		boxH.add(jComponent);
		boxH.add(createHorizontalGlue());

		add(createVerticalGlue());
		add(boxH);
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

	// Inputs
	private JComponent jComponent;

	// Tools
	private Box boxH; //this est boxV
	}
