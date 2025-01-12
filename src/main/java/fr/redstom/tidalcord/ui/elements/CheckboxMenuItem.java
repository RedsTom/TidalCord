package fr.redstom.tidalcord.ui.elements;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class CheckboxMenuItem extends JCheckBoxMenuItem {

    public CheckboxMenuItem() {
    }

    public CheckboxMenuItem(Icon icon) {
        super(icon);
    }

    public CheckboxMenuItem(String text) {
        super(text);
    }

    public CheckboxMenuItem(Action a) {
        super(a);
    }

    public CheckboxMenuItem(String text, Icon icon) {
        super(text, icon);
    }

    public CheckboxMenuItem(String text, boolean b) {
        super(text, b);
    }

    public CheckboxMenuItem(String text, Icon icon, boolean b) {
        super(text, icon, b);
    }

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
