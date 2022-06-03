package ch.hesso.chat_rmi.jvmuser.gui.tools;

import javax.swing.*;
import java.awt.*;

public class JAllSpaceH extends Box
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

    public JAllSpaceH(JComponent... jComponents)
    {
        super(BoxLayout.X_AXIS);

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
        add(createHorizontalStrut(50));

        for (JComponent jComponent : this.jComponents)
        {
            add(jComponent, BorderLayout.CENTER);
        }

        add(createHorizontalStrut(50));
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

