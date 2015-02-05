/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Raido Seene
 */
public class Messenger implements Runnable {

    private final int type;
    private final Object object;
    private final String string;

    private Messenger(int type, Object object, String string) {
        this.object = object;
        this.string = string;
        this.type = type;
    }

    @Override
    public void run() {
        if (this.type == JOptionPane.ERROR_MESSAGE || this.type == JOptionPane.WARNING_MESSAGE) {
            String message = new String();

            if (this.string != null) {
                message = this.string;
            }

            if (this.object != null && this.object instanceof Throwable) {
                if (message != null) {
                    message = message + "\n" + ((Throwable) this.object).getMessage();
                } else {
                    message = ((Throwable) this.object).getMessage();
                }
            }

            String title = (this.type == JOptionPane.ERROR_MESSAGE) ? "Error" : "Warning";
            JOptionPane.showMessageDialog(null, message, title, this.type);
        }
    }

    public static void showError(Throwable t, String txt) {
        Messenger m = new Messenger(JOptionPane.ERROR_MESSAGE, t, txt);

        if (SwingUtilities.isEventDispatchThread()) {
            m.run();
        } else {
            SwingUtilities.invokeLater(m);
        }
    }

    public static void showWarning(Throwable t, String txt) {
        Messenger m = new Messenger(JOptionPane.WARNING_MESSAGE, t, txt);

        if (SwingUtilities.isEventDispatchThread()) {
            m.run();
        } else {
            SwingUtilities.invokeLater(m);
        }
    }
}
