/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Stakeholder;
import ee.raidoseene.releaseplanner.datamodel.Stakeholders;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author risto
 */
public final class UrgValPanel extends JPanel {

    public static final String TITLE_STRING = "Feature urgency & value";

    public UrgValPanel() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTabbedPane tabs = new JTabbedPane();
        this.add(tabs);

        UVTab<Stakeholder> spanel = new UVTab<>(Stakeholder.class);
        tabs.addTab("By stakeholder", spanel);
        this.addHierarchyListener(spanel);

        UVTab<Feature> fpanel = new UVTab<>(Feature.class);
        tabs.addTab("By feature", fpanel);
        this.addHierarchyListener(fpanel);
    }

    private final class UVTab<T> extends JPanel implements HierarchyListener {

        private final Class<T> type;
        private final JComboBox groupc, stakec;
        private final ScrollablePanel scrollable;
        private final ActionListener listener;
        private final String all = "All";

        private UVTab(Class<T> type) {
            this.setLayout(new BorderLayout(10, 10));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));
            this.type = type;
            
            this.listener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        UVTab.this.listFilteredContent();
                        UVTab.this.scrollable.contentUpdated();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            };

            Container ct, c = new Container();
            c.setLayout(new BorderLayout(10, 10));
            this.add(BorderLayout.PAGE_START, c);
            String[] sel = new String[]{all};

            ct = new Container();
            ct.setLayout(new GridLayout(1, 2, 10, 10));
            c.add(BorderLayout.LINE_START, ct);
            this.groupc = new JComboBox(sel);
            ct.add(this.groupc);
            ct.add(new JLabel("Select feature group"));

            ct = new Container();
            ct.setLayout(new GridLayout(1, 2, 10, 10));
            c.add(BorderLayout.LINE_END, ct);
            this.stakec = new JComboBox(sel);
            this.stakec.addActionListener(this.listener);
            ct.add(this.stakec);
            ct.add(new JLabel("Select stakeholder"));

            this.scrollable = new ScrollablePanel();
            this.add(BorderLayout.CENTER, new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
            this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
            this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        private void listFilteredContent() {
            this.scrollable.removeAll();
            try {
                int index = this.stakec.getSelectedIndex();
                Stakeholders stakeholders = ProjectManager.getCurrentProject().getStakeholders();
                Stakeholder filter = null;
                if (index > 0) {
                    filter = stakeholders.getStakeholder(index - 1);
                }

                if (this.type.equals(Stakeholder.class)) {
                    int count = stakeholders.getStakeholderCount();

                    for (int i = 0; i < count; i++) {
                        Stakeholder s = stakeholders.getStakeholder(i);
                        if (filter == null || s == filter) {
                            UVTab.UVSContent content = new UVTab.UVSContent(s);
                            ContentPanel panel = new ContentPanel(content, false);
                            panel.addContentPanelListener(content);
                            this.scrollable.add(panel);
                        }
                    }
                } else if (this.type.equals(Feature.class)) {
                    Features features = ProjectManager.getCurrentProject().getFeatures();
                    int count = features.getFeatureCount();

                    for (int i = 0; i < count; i++) {
                        Feature f = features.getFeature(i);
                        UVTab.UVFContent content = new UVTab.UVFContent(f, filter);
                        ContentPanel panel = new ContentPanel(content, false);
                        panel.addContentPanelListener(content);
                        this.scrollable.add(panel);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        @Override
        public void hierarchyChanged(HierarchyEvent he) {
            int mask = HierarchyEvent.SHOWING_CHANGED;
            if ((he.getChangeFlags() & mask) == mask && he.getChanged().isShowing()) {
                Project project = ProjectManager.getCurrentProject();

                try {
                    Object item = this.groupc.getSelectedItem();
                    // TODO:
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                try {
                    Object item = this.stakec.getSelectedItem();
                    Stakeholders stakeholders = project.getStakeholders();
                    int count = stakeholders.getStakeholderCount();
                    this.stakec.removeActionListener(this.listener);
                    this.stakec.removeAllItems();
                    this.stakec.addItem(this.all);
                    this.stakec.setSelectedIndex(0);
                    for (int i = 0; i < count; i++) {
                        this.stakec.addItem(stakeholders.getStakeholder(i).getName());
                    }

                    this.stakec.setSelectedItem(item);
                    this.stakec.addActionListener(this.listener);
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                this.listFilteredContent();
            }
        }

        private final class UVSContent extends JPanel implements ContentPanelListener {

            private UVSContent(Stakeholder s) {

            }

            @Override
            public void contentPanelClosed(ContentPanel source) {
            }

            @Override
            public void contentPanelExpanded(ContentPanel source) {
            }

            @Override
            public void contentPanelCompressed(ContentPanel source) {
            }

        }

        private final class UVFContent extends JPanel implements ContentPanelListener {

            private UVFContent(Feature f, Stakeholder filter) {

            }

            @Override
            public void contentPanelClosed(ContentPanel source) {
            }

            @Override
            public void contentPanelExpanded(ContentPanel source) {
            }

            @Override
            public void contentPanelCompressed(ContentPanel source) {
            }

        }

    }

}
