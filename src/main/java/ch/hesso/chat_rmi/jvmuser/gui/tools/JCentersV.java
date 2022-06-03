package ch.hesso.chat_rmi.jvmuser.gui.tools;

import javax.swing.*;

public class JCentersV extends Box
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

    public JCentersV(JComponent... jComponents)
    {
        super(BoxLayout.Y_AXIS);

        this.jComponents = jComponents;

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
        add(Box.createVerticalGlue());
        for (JComponent jComponent : this.jComponents)
        {
            add(jComponent);
        }
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
    private JComponent[] jComponents;

}

