/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Raido Seene
 */
public class SolverOutputFrame extends JFrame {

    private final ScrollablePanel scrollable;
    //private final JTextField name;
    private final TextArea name;

    private SolverOutputFrame(String input) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension((int) (screen.width * 0.5f), (int) (screen.height * 0.6f));
        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
        this.setMinimumSize(new Dimension(800, 600));
        this.setSize(size);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Solver output");

        JPanel cont = new JPanel(new BorderLayout(10, 10));
        cont.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(cont);

        this.scrollable = new ScrollablePanel();
        cont.add(BorderLayout.CENTER, new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

        //this.name = new JTextField();
        this.name = new TextArea();
        this.name.setText(input);
        this.add(this.name);
    }

    public static void showSolverOutputFrame(String input) {
        SolverOutputFrame solverOutput = new SolverOutputFrame(input);
        solverOutput.setVisible(true);
    }
}
