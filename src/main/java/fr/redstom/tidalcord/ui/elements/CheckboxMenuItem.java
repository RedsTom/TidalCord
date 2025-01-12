package fr.redstom.tidalcord.ui.elements;

import java.awt.event.MouseEvent;

import javax.swing.*;

/** A checkbox menu item that does not close the menu when clicked */
public class CheckboxMenuItem extends JCheckBoxMenuItem {

    public CheckboxMenuItem(String text) {
        super(text);
    }

    /** Prevents the checkbox from closing the menu when clicked */
    @Override
    protected void processMouseEvent(MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
            doClick();
            setArmed(true);
        } else {
            super.processMouseEvent(evt);
        }
    }
}
