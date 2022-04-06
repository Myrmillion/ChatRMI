package ch.hesso.my_example.jvmuser.gui.tools;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import java.awt.*;
import java.util.List;

public class JCentersH extends Box
{

	/*------------------------------------------------------------------*\
	|*							Constructeurs							*|
	\*------------------------------------------------------------------*/

    public JCentersH(JComponent... jComponents)
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
        add(Box.createHorizontalGlue());
        for(JComponent jComponent :this.jComponents)
        {
            add(jComponent);
        }
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
    private JComponent[] jComponents;

}

