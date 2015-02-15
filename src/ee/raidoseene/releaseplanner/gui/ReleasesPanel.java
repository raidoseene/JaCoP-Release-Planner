/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import ee.raidoseene.releaseplanner.datamodel.Resource;
import ee.raidoseene.releaseplanner.datamodel.Resources;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ExtendableLayout;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author risto
 */
public final class ReleasesPanel extends ScrollablePanel {

    public static final String TITLE_STRING = "Releases";
    private final ScrollablePanel scrollable;
    private final JButton addButton;

    public ReleasesPanel() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new ScrollablePanel();
        this.add(new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

        Project project = ProjectManager.getCurrentProject();
        if (project != null) {
            Releases releases = project.getReleases();
            int count = releases.getReleaseCount();

            for (int i = 0; i < count; i++) {
                Release r = releases.getRelease(i);
                ReleasesPanel.RPContent content = new ReleasesPanel.RPContent(r);
                ContentPanel panel = new ContentPanel(content, true);
                panel.addContentPanelListener(content);
                this.scrollable.add(panel);
            }
        }

        this.addButton = new JButton("Add new release");
        this.addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    ReleasesPanel.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.scrollable.add(this.addButton);
    }

    private void processAddEvent() {
        Release r = ProjectManager.getCurrentProject().getReleases().addRelease();
        ReleasesPanel.RPContent content = new ReleasesPanel.RPContent(r);
        ContentPanel panel = new ContentPanel(content, true);

        this.scrollable.add(panel, this.scrollable.getComponentCount() - 1);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private final class RPContent extends JPanel implements ContentPanelListener {

        private final Release release;
        private final JPanel cont1;
        private final JTextField name;
        private final JSpinner importance;
        private final JPanel cont2;
        private final RPContent.RPCPanel panel;
        private final JRadioButton hours, days;

        private RPContent(Release r) {
            this.setLayout(new ExtendableLayout(ExtendableLayout.VERTICAL, 10));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));

            this.release = r;
            this.cont1 = new JPanel();
            this.cont1.setBorder(new EmptyBorder(0, 0, 0, 110));
            this.cont1.setLayout(new BorderLayout(25, 25));
            this.add(this.cont1);

            this.name = new JTextField(r.getName());
            this.name.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent fe) {
                }

                @Override
                public void focusLost(FocusEvent fe) {
                    try {
                        RPContent.this.release.setName(RPContent.this.name.getText());
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    RPContent.this.name.setText(RPContent.this.release.getName());
                }
            });
            this.cont1.add(BorderLayout.CENTER, this.name);

            Container c = new Container();
            c.setLayout(new BorderLayout(5, 5));
            this.cont1.add(BorderLayout.LINE_END, c);

            this.importance = new JSpinner(new SpinnerNumberModel(r.getImportance(), 1, 9, 1));
            this.importance.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    try {
                        Object o = RPContent.this.importance.getValue();
                        RPContent.this.release.setImportance((Integer) o);
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    RPContent.this.importance.setValue(RPContent.this.release.getImportance());
                }
            });
            c.add(BorderLayout.CENTER, this.importance);
            c.add(BorderLayout.LINE_END, new JLabel("Importance"));

            this.cont2 = new JPanel();
            this.cont2.setBorder(new TitledBorder("Resource capacity"));
            this.cont2.setLayout(new BorderLayout());

            this.panel = new RPContent.RPCPanel();
            this.cont2.add(BorderLayout.LINE_START, this.panel);
            this.addHierarchyListener(this.panel);

            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(new EmptyBorder(5, 5, 5, 5));
            this.cont2.add(BorderLayout.LINE_END, p);

            c = new Container();
            c.setLayout(new GridLayout(1, 2, 5, 5));
            p.add(BorderLayout.PAGE_START, c);

            Container c2 = new Container();
            c2.setLayout(new GridLayout(2, 1, 5, 5));
            c.add(c2);

            ButtonGroup bg = new ButtonGroup();
            this.hours = new JRadioButton("hours");
            this.days = new JRadioButton("days");
            c2.add(this.hours);
            bg.add(this.hours);
            c2.add(this.days);
            bg.add(this.days);

            c.add(new JLabel("Time format"));
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            try {
                Releases releases = ProjectManager.getCurrentProject().getReleases();
                releases.removeRelease(this.release);

                ReleasesPanel.this.scrollable.remove(source);
                ReleasesPanel.this.scrollable.contentUpdated();
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }

        @Override
        public void contentPanelExpanded(ContentPanel source) {
            if (this.getComponentCount() == 1) {
                this.add(this.cont2);

                ReleasesPanel.this.scrollable.contentUpdated();
            }
        }

        @Override
        public void contentPanelCompressed(ContentPanel source) {
            if (this.getComponentCount() > 1) {
                this.remove(this.cont2);

                ReleasesPanel.this.scrollable.contentUpdated();
            }
        }

        private final class RPCPanel extends JPanel implements HierarchyListener {

            private final ArrayList<JTextField> values;
            private final FocusListener flistener;

            private RPCPanel() {
                Resources resources = ProjectManager.getCurrentProject().getResources();
                int count = resources.getResourceCount();

                this.values = new ArrayList<>(count);
                this.setBorder(new EmptyBorder(5, 5, 5, 5));
                this.flistener = new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent fe) {
                    }

                    @Override
                    public void focusLost(FocusEvent fe) {
                        try {
                            Object source = fe.getSource();
                            int index = RPCPanel.this.values.indexOf(source);
                            Resource r = ProjectManager.getCurrentProject().getResources().getResource(index);
                            JTextField tf = RPCPanel.this.values.get(index);
                            try {
                                RPContent.this.release.setCapacity(r, Integer.parseInt(tf.getText()));
                            } catch (Exception e2) {
                                Messenger.showError(e2, null);
                            }
                            tf.setText(Integer.toString(RPContent.this.release.getCapacity(r)));
                        } catch (Exception ex) {
                            Messenger.showError(ex, null);
                        }
                    }
                };
            }

            @Override
            public void hierarchyChanged(HierarchyEvent he) {
                int mask = HierarchyEvent.SHOWING_CHANGED;
                if ((he.getChangeFlags() & mask) == mask && he.getChanged().isShowing()) {
                    Resources resources = ProjectManager.getCurrentProject().getResources();
                    int count = resources.getResourceCount();

                    this.removeAll();
                    this.values.clear();
                    this.setLayout(new GridLayout(count, 2, 10, 2));

                    for (int i = 0; i < count; i++) {
                        Resource r = resources.getResource(i);
                        int value = RPContent.this.release.getCapacity(r);
                        JTextField text = new JTextField(Integer.toString(value));
                        text.addFocusListener(this.flistener);
                        this.values.add(text);
                        this.add(text);

                        this.add(new JLabel(r.getName() + " (h)"));
                    }

                    this.values.trimToSize();
                    this.validate();
                }
            }

        }

    }
}
