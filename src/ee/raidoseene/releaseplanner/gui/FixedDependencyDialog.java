/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author risto
 */
public class FixedDependencyDialog extends JDialog {

    private final Feature feature;
    private final JComboBox release;
    private boolean state = false;

    private FixedDependencyDialog(Feature f) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(400, 200);

        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
        this.setSize(size);

        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setTitle("Select Release");
        this.setModal(true);

        JPanel cont = new JPanel(new BorderLayout(10, 10));
        cont.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(cont);
        this.feature = f;

        Container c = new Container();
        c.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        Releases releases = ProjectManager.getCurrentProject().getReleases();
        int count = releases.getReleaseCount();
        this.release = new JComboBox();
        for (int i = 0; i < count; i++) {
            this.release.addItem(releases.getRelease(i).getName());
        }
        c.add(this.release);
        c.add(new JLabel("Release"));
        cont.add(BorderLayout.PAGE_START, c);

        c = new Container();
        c.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cont.add(BorderLayout.PAGE_END, c);

        JButton btn = new JButton("OK");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    Releases rels = ProjectManager.getCurrentProject().getReleases();
                    Release rel = rels.getRelease(FixedDependencyDialog.this.release.getSelectedIndex());
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    ids.addFixedDependency(FixedDependencyDialog.this.feature, rel);
                    FixedDependencyDialog.this.state = true;

                    FixedDependencyDialog.this.dispose();
                } catch (Exception ex) {
                    FixedDependencyDialog.this.dispose();
                    Messenger.showError(ex, null);
                }
            }
        });
        c.add(btn);

        btn = new JButton("Cancel");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    FixedDependencyDialog.this.dispose();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        c.add(btn);
    }

    public static boolean showFixedDependencyDialog(Feature f) {
        FixedDependencyDialog dialog = new FixedDependencyDialog(f);
        dialog.setVisible(true);
        return dialog.state;
    }
}
