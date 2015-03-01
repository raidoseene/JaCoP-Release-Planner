/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.Groups;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Raido Seene
 */
public final class GroupManagerDialog extends JDialog {

    private final ScrollablePanel scrollable;

    private GroupManagerDialog() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(500, 500);

        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
        this.setSize(size);

        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setTitle("Manage groups");
        this.setModal(true);

        JPanel cont = new JPanel(new BorderLayout(10, 10));
        cont.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(cont);

        this.scrollable = new ScrollablePanel();
        cont.add(BorderLayout.CENTER, new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

        Groups groups = ProjectManager.getCurrentProject().getGroups();
        int count = groups.getGroupCount();
        for (int i = 0; i < count; i++) {
            Group g = groups.getGroup(i);
            GroupManagerDialog.GMDContent content = new GroupManagerDialog.GMDContent(g);
            ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);
            panel.addContentPanelListener(content);
            this.scrollable.add(panel);
        }

        Container c = new Container();
        c.setLayout(new GridLayout(2, 1, 10, 10));
        cont.add(BorderLayout.PAGE_END, c);

        Container c1 = new Container();
        c1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton btn = new JButton("Add new group");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    GroupManagerDialog.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        c1.add(btn);
        c.add(c1);

        Container c2 = new Container();
        c2.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btn = new JButton("OK");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    GroupManagerDialog.this.processOkEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        c2.add(btn);
        c.add(c2);
    }

    private void processAddEvent() {
        Group g = ProjectManager.getCurrentProject().getGroups().addGroup();
        GroupManagerDialog.GMDContent content = new GroupManagerDialog.GMDContent(g);
        ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);

        this.scrollable.add(panel);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private void processOkEvent() {
        this.dispose();
    }

    private final class GMDContent extends JPanel implements ContentPanelListener {

        private final Group group;
        private final JTextField name;

        private GMDContent(Group g) {
            this.setBorder(new EmptyBorder(10, 10, 10, 80));
            this.setLayout(new GridLayout(1, 1));

            this.group = g;
            this.name = new JTextField(g.getName());
            this.name.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent fe) {
                }

                @Override
                public void focusLost(FocusEvent fe) {
                    try {
                        GMDContent.this.group.setName(GMDContent.this.name.getText());
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    GMDContent.this.name.setText(GMDContent.this.group.getName());
                }
            });
            this.add(this.name);
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            Groups groups = ProjectManager.getCurrentProject().getGroups();
            groups.removeGroup(this.group);

            GroupManagerDialog.this.scrollable.remove(source);
            GroupManagerDialog.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
        }

        @Override
        public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
        }

    }

    public static void showGroupManagerDialog() {
        GroupManagerDialog dialog = new GroupManagerDialog();
        dialog.setVisible(true);
    }
}
