
package ch.hesso.my_example.jvmuser.gui.tools;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;

public class JCenterH extends Box
	{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

	public JCenterH(JComponent jComponent)
		{
		super(BoxLayout.X_AXIS);

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
		add(Box.createHorizontalGlue());
		add(jComponent);
		add(createHorizontalGlue()); //Pas oblig� de mettre box car d�j� dans un classe Box
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

