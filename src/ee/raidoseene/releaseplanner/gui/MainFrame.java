/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Raido Seene
 */
public class MainFrame extends JFrame {

    private MainFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension winSize = new Dimension((int) (screen.getWidth() * 0.7), (int) (screen.getHeight() * 0.8));
        Dimension winMinSize = new Dimension(800, 600);

        this.setLocation((int) ((screen.getWidth() - winSize.getWidth()) * 0.5), (int) ((screen.getHeight() - winSize.getHeight()) * 0.5));
        this.setMinimumSize(winMinSize);
        this.setSize(winSize);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu bar
        this.setJMenuBar(null);

        // Buttons
        this.add(new Button("Vajuta!"));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    Messenger.showWarning(ex, null);
                }

                try {
                    MainFrame window = new MainFrame();
                    window.setVisible(true);
                } catch (Exception ex) {
                    Messenger.showError(ex, "Failed to initialize application:");
                }
            }
        });
    }
}
