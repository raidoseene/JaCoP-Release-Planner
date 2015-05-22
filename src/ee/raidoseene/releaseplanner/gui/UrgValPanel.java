/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.Groups;
import ee.raidoseene.releaseplanner.datamodel.ModifyingInterdependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import ee.raidoseene.releaseplanner.datamodel.Stakeholder;
import ee.raidoseene.releaseplanner.datamodel.Stakeholders;
import ee.raidoseene.releaseplanner.datamodel.Urgency;
import ee.raidoseene.releaseplanner.datamodel.Value;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Raido Seene
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
                int gindex = this.groupc.getSelectedIndex();
                int sindex = this.stakec.getSelectedIndex();
                Project project = ProjectManager.getCurrentProject();
                Stakeholders stakeholders = project.getStakeholders();
                Groups groups = project.getGroups();
                Stakeholder sfilter = null;
                Group gfilter = null;
                if (sindex > 0) {
                    sfilter = stakeholders.getStakeholder(sindex - 1);
                }
                if (gindex > 0) {
                    gfilter = groups.getGroup(gindex - 1);
                }

                if (this.type.equals(Stakeholder.class)) {
                    int count = stakeholders.getStakeholderCount();

                    for (int i = 0; i < count; i++) {
                        Stakeholder s = stakeholders.getStakeholder(i);
                        if (sfilter == null || s == sfilter) {
                            UVTab.UVSContent content = new UVTab.UVSContent(s, gfilter);
                            ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_EXPANDABLE);
                            panel.addContentPanelListener(content);
                            this.scrollable.add(panel);
                        }
                    }
                } else if (this.type.equals(Feature.class)) {
                    Features features = ProjectManager.getCurrentProject().getFeatures();
                    int count = features.getFeatureCount();

                    for (int i = 0; i < count; i++) {
                        Feature f = features.getFeature(i);
                        if (gfilter == null || gfilter == groups.getGroupByFeature(f)) {
                            UVTab.UVFContent content = new UVTab.UVFContent(f, sfilter);
                            ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_EXPANDABLE);
                            panel.addContentPanelListener(content);
                            this.scrollable.add(panel);
                        }
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
                    Groups groups = project.getGroups();
                    int count = groups.getGroupCount();
                    this.groupc.removeActionListener(this.listener);
                    this.groupc.removeAllItems();
                    this.groupc.addItem(this.all);
                    this.groupc.setSelectedIndex(0);
                    for (int i = 0; i < count; i++) {
                        this.groupc.addItem(groups.getGroup(i).getName());
                    }

                    this.groupc.setSelectedItem(item);
                    this.groupc.addActionListener(this.listener);
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

        private final class UVSContent extends JPanel implements ContentPanelListener, HierarchyListener {

            private final Group filter;
            private final Stakeholder stakeholder;
            private final ScrollablePanel scrollable;
            private final JScrollPane scroller;

            private UVSContent(Stakeholder s, Group filter) {
                this.setBorder(new EmptyBorder(10, 10, 10, 10));
                this.setLayout(new BorderLayout(10, 10));

                this.filter = filter;
                this.stakeholder = s;
                this.add(BorderLayout.PAGE_START, new JLabel(s.getName()));

                this.scrollable = new ScrollablePanel();
                this.scroller = new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
                this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

                this.scrollable.addHierarchyListener(this);
            }

            @Override
            public void contentPanelClosed(ContentPanel source) {
            }

            @Override
            public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
                if (expanded && this.getComponentCount() == 1) {
                    this.add(this.scroller);
                } else if (!expanded && this.getComponentCount() > 1) {
                    this.remove(this.scroller);
                }
                UVTab.this.scrollable.contentUpdated();
            }

            @Override
            public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
            }

            @Override
            public void hierarchyChanged(HierarchyEvent he) {
                int mask = HierarchyEvent.SHOWING_CHANGED;
                if ((he.getChangeFlags() & mask) == mask && he.getChanged().isShowing()) {
                    Features features = ProjectManager.getCurrentProject().getFeatures();
                    Groups groups = ProjectManager.getCurrentProject().getGroups();
                    int count = features.getFeatureCount();
                    this.scrollable.removeAll();

                    for (int i = 0; i < count; i++) {
                        Feature f = features.getFeature(i);
                        if (this.filter == null || this.filter == groups.getGroupByFeature(f)) {
                            UVSContent.UVSFContent content = new UVSContent.UVSFContent(f);
                            ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_EXPANDABLE);
                            panel.addContentPanelListener(content);
                            this.scrollable.add(panel);
                        }
                    }
                }
            }

            private final class UVSFContent extends JPanel implements ContentPanelListener {

                private final Feature feature;
                private final JPanel cont1;
                private final JSpinner value;
                private final UVTab.UrgencyPanel cont2;
                private final UVTab.ChangePanel cont3;

                private UVSFContent(Feature f) {
                    this.setBorder(new EmptyBorder(10, 10, 10, 10));
                    this.setLayout(new BorderLayout());
                    this.feature = f;

                    this.cont1 = new JPanel(new BorderLayout(10, 10));
                    this.cont1.setBorder(new EmptyBorder(0, 0, 0, 80));
                    this.cont1.add(BorderLayout.CENTER, new JLabel(f.getName()));
                    this.add(BorderLayout.PAGE_START, this.cont1);

                    Container c = new Container();
                    c.setLayout(new BorderLayout(10, 10));
                    this.cont1.add(BorderLayout.LINE_END, c);

                    ValueAndUrgency vus = ProjectManager.getCurrentProject().getValueAndUrgency();
                    this.value = new JSpinner(new SpinnerNumberModel(vus.getValue(UVSContent.this.stakeholder, f), 0, 9, 1));
                    this.value.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent ce) {
                            ValueAndUrgency vus = ProjectManager.getCurrentProject().getValueAndUrgency();
                            try {
                                Object o = UVSFContent.this.value.getValue();
                                vus.setValue(UVSContent.this.stakeholder, UVSFContent.this.feature, (Integer) o);
                            } catch (Exception ex) {
                                Messenger.showError(ex, null);
                            }
                            UVSFContent.this.value.setValue(vus.getValue(UVSContent.this.stakeholder, UVSFContent.this.feature));
                        }
                    });
                    c.add(BorderLayout.LINE_START, this.value);
                    c.add(BorderLayout.CENTER, new JLabel("Value to stakeholder"));

                    this.cont2 = new UVTab.UrgencyPanel(UVSContent.this.stakeholder, this.feature);
                    this.cont3 = new UVTab.ChangePanel(UVSContent.this.stakeholder, this.feature);
                }

                @Override
                public void contentPanelClosed(ContentPanel source) {
                }

                @Override
                public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
                    if (expanded && this.getComponentCount() == 1) {
                        this.add(BorderLayout.CENTER, this.cont2);
                        this.add(BorderLayout.PAGE_END, this.cont3);
                    } else if (!expanded && this.getComponentCount() > 1) {
                        this.remove(this.cont2);
                        this.remove(this.cont3);
                    }
                    UVTab.this.scrollable.contentUpdated();
                }

                @Override
                public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
                }
            }
        }

        private final class UVFContent extends JPanel implements ContentPanelListener, HierarchyListener {

            private final Feature feature;
            private final Stakeholder filter;
            private final ScrollablePanel scrollable;
            private final JScrollPane scroller;

            private UVFContent(Feature f, Stakeholder filter) {
                this.setBorder(new EmptyBorder(12, 10, 10, 10));
                this.setLayout(new BorderLayout(10, 12));

                this.feature = f;
                this.filter = filter;
                this.add(BorderLayout.PAGE_START, new JLabel(f.getName()));

                this.scrollable = new ScrollablePanel();
                this.scroller = new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
                this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

                this.scrollable.addHierarchyListener(this);
            }

            @Override
            public void contentPanelClosed(ContentPanel source) {
            }

            @Override
            public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
                if (expanded && this.getComponentCount() == 1) {
                    this.add(this.scroller);
                } else if (!expanded && this.getComponentCount() > 1) {
                    this.remove(this.scroller);
                }
                UVTab.this.scrollable.contentUpdated();
            }

            @Override
            public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
            }

            @Override
            public void hierarchyChanged(HierarchyEvent he) {
                int mask = HierarchyEvent.SHOWING_CHANGED;
                if ((he.getChangeFlags() & mask) == mask && he.getChanged().isShowing()) {
                    Stakeholders stakeholders = ProjectManager.getCurrentProject().getStakeholders();
                    int count = stakeholders.getStakeholderCount();
                    this.scrollable.removeAll();

                    for (int i = 0; i < count; i++) {
                        Stakeholder s = stakeholders.getStakeholder(i);
                        if (this.filter == null || s == this.filter) {
                            UVFContent.UVFSContent content = new UVFContent.UVFSContent(s);
                            ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_EXPANDABLE);
                            panel.addContentPanelListener(content);
                            this.scrollable.add(panel);
                        }
                    }
                }
            }

            private final class UVFSContent extends JPanel implements ContentPanelListener {

                private final Stakeholder stakeholder;
                private final JPanel cont1;
                private final JSpinner value;
                private final UVTab.UrgencyPanel cont2;
                private final UVTab.ChangePanel cont3;

                private UVFSContent(Stakeholder s) {
                    this.setBorder(new EmptyBorder(10, 10, 10, 10));
                    this.setLayout(new BorderLayout());
                    this.stakeholder = s;

                    this.cont1 = new JPanel(new BorderLayout(10, 10));
                    this.cont1.setBorder(new EmptyBorder(0, 0, 0, 80));
                    this.cont1.add(BorderLayout.CENTER, new JLabel(s.getName()));
                    this.add(BorderLayout.PAGE_START, this.cont1);

                    Container c = new Container();
                    c.setLayout(new BorderLayout(10, 10));
                    this.cont1.add(BorderLayout.LINE_END, c);

                    ValueAndUrgency vus = ProjectManager.getCurrentProject().getValueAndUrgency();
                    this.value = new JSpinner(new SpinnerNumberModel(vus.getValue(s, UVFContent.this.feature), 0, 9, 1));
                    this.value.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent ce) {
                            ValueAndUrgency vus = ProjectManager.getCurrentProject().getValueAndUrgency();
                            try {
                                Object o = UVFSContent.this.value.getValue();
                                vus.setValue(UVFSContent.this.stakeholder, UVFContent.this.feature, (Integer) o);
                            } catch (Exception ex) {
                                Messenger.showError(ex, null);
                            }
                            UVFSContent.this.value.setValue(vus.getValue(UVFSContent.this.stakeholder, UVFContent.this.feature));
                        }
                    });
                    c.add(BorderLayout.LINE_START, this.value);
                    c.add(BorderLayout.CENTER, new JLabel("Value to stakeholder"));

                    this.cont2 = new UVTab.UrgencyPanel(this.stakeholder, UVFContent.this.feature);
                    this.cont3 = new UVTab.ChangePanel(this.stakeholder, UVFContent.this.feature);
                }

                @Override
                public void contentPanelClosed(ContentPanel source) {
                }

                @Override
                public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
                    if (expanded && this.getComponentCount() == 1) {
                        this.add(BorderLayout.CENTER, this.cont2);
                        this.add(BorderLayout.PAGE_END, this.cont3);
                    } else if (!expanded && this.getComponentCount() > 1) {
                        this.remove(this.cont2);
                        this.remove(this.cont3);
                    }
                    UVTab.this.scrollable.contentUpdated();
                }

                @Override
                public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
                }
            }
        }

        private final class UrgencyPanel extends JPanel {

            private final Feature feature;
            private final Stakeholder stakeholder;
            private final JComboBox urgency, release;
            private final JRadioButton exact, earliest, latest, hard, soft;
            //private final ActionListener listener;

            private UrgencyPanel(Stakeholder s, Feature f) {
                this.setBorder(new EmptyBorder(10, 0, 0, 0));
                this.setLayout(new BorderLayout());
                this.stakeholder = s;
                this.feature = f;
                
                Container c = new Container();
                c.setLayout(new BorderLayout(10, 10));
                this.add(BorderLayout.CENTER, c);

                Container content = new Container();
                content.setLayout(new GridLayout(2, 1, 10, 10));
                c.add(BorderLayout.LINE_START, content);

                Project project = ProjectManager.getCurrentProject();
                ValueAndUrgency vus = project.getValueAndUrgency();
                Releases releases = project.getReleases();
                /* FocusListener listener = new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent fe) {
                    }

                    @Override
                    public void focusLost(FocusEvent fe) {
                        try {
                            JTextField tf = (JTextField) fe.getSource();
                            Project proj = ProjectManager.getCurrentProject();
                            Release rel = null;

                            if (tf != UrgencyPanel.this.vpostpone) {
                                JTextField[] tfs = UrgencyPanel.this.vreleases;
                                Releases releases = proj.getReleases();
                                for (int i = 0; i < tfs.length; i++) {
                                    if (tf == tfs[i]) {
                                        rel = releases.getRelease(i);
                                        break;
                                    }
                                }
                            }

                            ValueAndUrgency vus = proj.getValueAndUrgency();
                            try {
                                String txt = tf.getText();
                                int val = (txt.length() > 0) ? Integer.parseInt(txt) : 0;
                                vus.setUrgency(UrgencyPanel.this.stakeholder, UrgencyPanel.this.feature, rel, val);
                            } catch (Exception ex2) {
                                Messenger.showError(ex2, null);
                            }

                            int val = vus.getUrgency(UrgencyPanel.this.stakeholder, UrgencyPanel.this.feature, rel);
                            tf.setText((val > 0) ? Integer.toString(val) : "");
                        } catch (Exception ex) {
                            Messenger.showError(ex, null);
                        }
                    }
                };*/
                
                Container controls = new Container();
                controls.setLayout(new BorderLayout(32, 32));
                content.add(controls);
                
                Container combos = new Container();
                combos.setLayout(new BorderLayout());
                controls.add(BorderLayout.LINE_START, combos);
                Container c2;

                c2 = new Container();
                c2.setLayout(new GridLayout(1, 2));
                c2.add(new JLabel("Urgency"));
                c2.add(this.urgency = new JComboBox());
                combos.add(BorderLayout.PAGE_START, c2);
                
                c2 = new Container();
                c2.setLayout(new GridLayout(1, 2));
                c2.add(new JLabel("Release"));
                c2.add(this.release = new JComboBox());
                combos.add(BorderLayout.PAGE_END, c2);
                
                Container grid = new Container();
                grid.setLayout(new GridLayout(3, 2, 32, 4));
                controls.add(BorderLayout.CENTER, grid);
                ButtonGroup deadline = new ButtonGroup();
                ButtonGroup curve = new ButtonGroup();
                
                this.exact = new JRadioButton("exact");
                deadline.add(this.exact);
                grid.add(this.exact);
                
                grid.add(new JLabel());
                
                this.earliest = new JRadioButton("earliest");
                deadline.add(this.earliest);
                grid.add(this.earliest);
                
                this.hard = new JRadioButton("hard");
                curve.add(this.hard);
                grid.add(this.hard);
                
                this.latest = new JRadioButton("latest");
                deadline.add(this.latest);
                grid.add(this.latest);
                
                this.soft = new JRadioButton("soft");
                curve.add(this.soft);
                grid.add(this.soft);
                
                this.updateUrgencySelections();
                /*for (int i = 0; i < this.vreleases.length; i++) {
                    int urg = vus.getUrgency(s, f, releases.getRelease(i));
                    String val = (urg > 0) ? Integer.toString(urg) : "";

                    Container ge = new Container();
                    ge.setLayout(new GridLayout(2, 1, 2, 2));
                    ge.add(this.vreleases[i] = new JTextField(val));
                    ge.add(new JLabel(str + Integer.toString(i + 1) + str, JLabel.CENTER));
                    this.vreleases[i].addFocusListener(listener);
                    grid.add(ge);
                }
                
                {
                    int urg = vus.getUrgency(s, f, null);
                    String val = (urg > 0) ? Integer.toString(urg) : "";

                    Container ge = new Container();
                    ge.setLayout(new GridLayout(2, 1, 2, 2));
                    ge.add(this.vpostpone = new JTextField(val));
                    ge.add(new JLabel("  postpone  ", JLabel.CENTER));
                    this.vpostpone.addFocusListener(listener);
                    grid.add(ge);

                    Container ce = new Container();
                    ce.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
                    JButton btn = new JButton("Clear urgency");
                    btn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            try {
                                UrgencyPanel.this.clearUrgency();
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });
                    ce.add(BorderLayout.PAGE_START, btn);

                    c.add(ce);
                }*/

                Container btns = new Container();
                btns.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
                c.add(BorderLayout.PAGE_END, btns);
            }

            /* private void clearUrgency() {
                Project project = ProjectManager.getCurrentProject();
                ValueAndUrgency vus = project.getValueAndUrgency();
                Releases releases = project.getReleases();
                int rcount = releases.getReleaseCount();

                for (int i = 0; i < rcount; i++) {
                    Release release = releases.getRelease(i);
                    vus.setUrgency(this.stakeholder, this.feature, release, 0);
                    this.vreleases[i].setText("");
                }

                vus.setUrgency(this.stakeholder, this.feature, null, 0);
                this.vpostpone.setText("");
            }*/

            private void updateUrgencySelections() {
                try {
                    Releases releases = ProjectManager.getCurrentProject().getReleases();
                    Release orelease = null;//ProjectManager.getCurrentProject().getValueAndUrgency().getUrgencyRelease(stakeholder, feature);
                    int count = releases.getReleaseCount();

                    //this.release.removeActionListener(this.listener);
                    this.release.removeAllItems();
                    this.release.addItem("None");
                    this.release.setSelectedIndex(0);

                    for (int i = 0; i < count; i++) {
                        Release r = releases.getRelease(i);
                        this.release.addItem(r.getName());
                        if (orelease == r) {
                            this.release.setSelectedIndex(i + 1);
                        }
                    }

                    //this.release.addActionListener(this.listener);
                } catch (Exception ex) {
                    Messenger.showError(ex, null);
                }
            }
            
        }

        private final class ChangePanel extends JPanel {

            private final Feature feature;
            private final Stakeholder stakeholder;
            private final JButton chval, churg;
            private final Container chpanel;

            private ChangePanel(Stakeholder s, Feature f) {
                this.setBorder(new EmptyBorder(10, 0, 0, 0));
                this.setLayout(new BorderLayout(10, 10));
                this.stakeholder = s;
                this.feature = f;

                Container btns = new Container();
                btns.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
                this.add(BorderLayout.CENTER, btns);

                ActionListener listener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        try {
                            JButton btn = (JButton) ae.getSource();
                            ChangePanel.this.openChangePanel(btn);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                };

                this.chval = new JButton("Change in value");
                this.chval.addActionListener(listener);
                btns.add(this.chval);

                this.churg = new JButton("Change in urgency");
                this.churg.addActionListener(listener);
                btns.add(this.churg);

                this.chpanel = new Container();
                this.chpanel.setLayout(new ContentListLayout(Container.class));

                Dependencies deps = ProjectManager.getCurrentProject().getDependencies();
                ModifyingInterdependency[] mds = deps.getTypedDependencies(ModifyingInterdependency.class, null);
                for (ModifyingInterdependency md : mds) {
                    if (md.getSecondary() == ChangePanel.this.feature) {
                        if (md.type == Dependency.CV) {
                            this.openChangePanel(this.chval);
                        } else if (md.type == Dependency.CU) {
                            this.openChangePanel(this.churg);
                        }
                    }
                }
            }

            private void openChangePanel(JButton button) {
                button.setEnabled(false);

                if (button == this.chval) {
                    ChangePanel.CPVContent content = new ChangePanel.CPVContent();
                    ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);
                    panel.addContentPanelListener(content);
                    this.chpanel.add(panel);
                } else if (button == this.churg) {
                    ChangePanel.CPUContent content = new ChangePanel.CPUContent();
                    ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);
                    panel.addContentPanelListener(content);
                    this.chpanel.add(panel);
                } else {
                    return;
                }

                if (this.chpanel.getComponentCount() == 1) {
                    this.add(BorderLayout.PAGE_END, this.chpanel);
                }

                UVTab.this.scrollable.contentUpdated();
            }

            private void closeChangePanel(JButton button, ContentPanel panel) {
                this.chpanel.remove(panel);
                button.setEnabled(true);

                if (this.chpanel.getComponentCount() == 0) {
                    this.remove(this.chpanel);
                }

                UVTab.this.scrollable.contentUpdated();
            }

            private class CPVContent extends JPanel implements ContentPanelListener {

                private ModifyingInterdependency dependency;
                private final JComboBox feature;
                private final JSpinner value;

                private CPVContent() {
                    this.setBorder(new EmptyBorder(5, 10, 10, 80));
                    this.setLayout(new GridLayout(3, 1, 10, 10));
                    this.add(new JLabel("Change in value"));
                    Container c;

                    c = new Container();
                    c.setLayout(new BorderLayout(10, 10));
                    c.add(BorderLayout.LINE_END, new JLabel("Preceding feature"));
                    this.add(c);

                    Project project = ProjectManager.getCurrentProject();
                    Dependencies deps = project.getDependencies();
                    Features features = project.getFeatures();

                    ModifyingInterdependency[] mds = deps.getTypedDependencies(ModifyingInterdependency.class, Dependency.CV);
                    for (ModifyingInterdependency md : mds) {
                        if (md.getSecondary() == ChangePanel.this.feature) {
                            this.dependency = md;
                            break;
                        }
                    }

                    if (this.dependency == null) {
                        Value val = new Value(ChangePanel.this.feature, ChangePanel.this.stakeholder);
                        this.dependency = deps.addModifyingInterdependency(features.getFeature(0), val.getFeature(), val);
                    }

                    this.feature = new JComboBox();
                    c.add(BorderLayout.CENTER, this.feature);
                    int fcount = features.getFeatureCount();
                    for (int i = 0; i < fcount; i++) {
                        Feature f = features.getFeature(i);
                        this.feature.addItem(f.getName());

                        if (f == this.dependency.getPrimary()) {
                            this.feature.setSelectedIndex(i);
                        }
                    }
                    this.feature.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            CPVContent.this.changeFeature();
                        }
                    });

                    c = new Container();
                    c.setLayout(new BorderLayout(10, 10));
                    c.add(BorderLayout.CENTER, new JLabel("Value to stakeholder"));
                    this.add(c);

                    int val = this.dependency.getChange(Value.class).getValue();
                    this.value = new JSpinner(new SpinnerNumberModel(val, 0, 9, 1));
                    this.value.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent ce) {
                            try {
                                Value value = CPVContent.this.dependency.getChange(Value.class);
                                value.setValue((Integer) CPVContent.this.value.getValue());
                            } catch (Exception ex) {
                                Messenger.showError(ex, null);
                            }
                            try {
                                int val = CPVContent.this.dependency.getChange(Value.class).getValue();
                                CPVContent.this.value.setValue(val);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });
                    c.add(BorderLayout.LINE_START, this.value);
                }

                private void changeFeature() {
                    try {
                        Project project = ProjectManager.getCurrentProject();
                        Dependencies deps = project.getDependencies();
                        Features feats = project.getFeatures();

                        deps.removeInterdependency(this.dependency);

                        int index = this.feature.getSelectedIndex();
                        Feature f = feats.getFeature(index);

                        Value val = new Value(ChangePanel.this.feature, ChangePanel.this.stakeholder);
                        this.dependency = deps.addModifyingInterdependency(f, val.getFeature(), val);
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                }

                @Override
                public void contentPanelClosed(ContentPanel source) {
                    ProjectManager.getCurrentProject().getDependencies().removeInterdependency(this.dependency);
                    ChangePanel.this.closeChangePanel(ChangePanel.this.chval, source);
                }

                @Override
                public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
                }

                @Override
                public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
                }
            }

            private class CPUContent extends JPanel implements ContentPanelListener {
                // Commented out for testing
                private ModifyingInterdependency dependency;
                private final JComboBox feature;
                private final JTextField[] vreleases;
                private final JTextField vpostpone;

                private CPUContent() {
                    this.setBorder(new EmptyBorder(5, 10, 10, 80));
                    this.setLayout(new BorderLayout(10, 10));
                    Container c, c2;

                    c = new Container();
                    c.setLayout(new GridLayout(2, 1, 10, 10));
                    c.add(new JLabel("Change in urgency"));
                    this.add(BorderLayout.PAGE_START, c);

                    c2 = new Container();
                    c2.setLayout(new BorderLayout(10, 10));
                    c2.add(BorderLayout.LINE_END, new JLabel("Preceding feature"));
                    c.add(c2);

                    Project project = ProjectManager.getCurrentProject();
                    Dependencies deps = project.getDependencies();
                    Features features = project.getFeatures();

                    ModifyingInterdependency[] mds = deps.getTypedDependencies(ModifyingInterdependency.class, Dependency.CU);
                    for (ModifyingInterdependency md : mds) {
                        if (md.getSecondary() == ChangePanel.this.feature) {
                            this.dependency = md;
                            break;
                        }
                    }

                    if (this.dependency == null) {
                        Urgency urg = ValueAndUrgency.createStandaloneUrgency();
                        this.dependency = deps.addModifyingInterdependency(features.getFeature(0), ChangePanel.this.feature, urg);
                    }

                    this.feature = new JComboBox();
                    c2.add(BorderLayout.CENTER, this.feature);
                    int fcount = features.getFeatureCount();
                    for (int i = 0; i < fcount; i++) {
                        Feature f = features.getFeature(i);
                        this.feature.addItem(f.getName());

                        if (f == this.dependency.getPrimary()) {
                            this.feature.setSelectedIndex(i);
                        }
                    }
                    this.feature.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            CPUContent.this.changeFeature();
                        }
                    });

                    c = new Container();
                    c.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
                    this.add(BorderLayout.CENTER, c);

                    Releases releases = project.getReleases();
                    Urgency urgs = this.dependency.getChange(Urgency.class);
                    FocusListener listener = new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent fe) {
                        }

                        @Override
                        public void focusLost(FocusEvent fe) {
                            try {
                                JTextField tf = (JTextField) fe.getSource();
                                Project proj = ProjectManager.getCurrentProject();
                                Release rel = null;

                                if (tf != CPUContent.this.vpostpone) {
                                    JTextField[] tfs = CPUContent.this.vreleases;
                                    Releases releases = proj.getReleases();
                                    for (int i = 0; i < tfs.length; i++) {
                                        if (tf == tfs[i]) {
                                            rel = releases.getRelease(i);
                                            break;
                                        }
                                    }
                                }

                                Urgency urgs = CPUContent.this.dependency.getChange(Urgency.class);
                                try {
                                    String txt = tf.getText();
                                    int val = (txt.length() > 0) ? Integer.parseInt(txt) : 0;
                                    urgs.setUrgency(rel, val); // Commented out for testing
                                    /* added for test
                                    urgs.setUrgency(val);
                                    urgs.setRelease(rel);
                                    // added for test */
                                } catch (Exception ex2) {
                                    Messenger.showError(ex2, null);
                                }

                                int val = urgs.getUrgency(rel); // Commented out for testing
                                /* added for test
                                int val = urgs.getUrgency();
                                // added for test */
                                tf.setText((val > 0) ? Integer.toString(val) : "");
                            } catch (Exception ex) {
                                Messenger.showError(ex, null);
                            }
                        }
                    };

                    String str = "        ";
                    this.vreleases = new JTextField[releases.getReleaseCount()];
                    for (int i = 0; i < this.vreleases.length; i++) {
                        int urg = urgs.getUrgency(releases.getRelease(i));
                        String val = (urg > 0) ? Integer.toString(urg) : "";

                        c2 = new Container();
                        c2.setLayout(new GridLayout(2, 1, 2, 2));
                        c2.add(this.vreleases[i] = new JTextField(val));
                        c2.add(new JLabel(str + Integer.toString(i + 1) + str, JLabel.CENTER));
                        this.vreleases[i].addFocusListener(listener);
                        c.add(c2);
                    }

                    {
                        int urg = urgs.getUrgency(null);
                        String val = (urg > 0) ? Integer.toString(urg) : "";

                        c2 = new Container();
                        c2.setLayout(new GridLayout(2, 1, 2, 2));
                        c2.add(this.vpostpone = new JTextField(val));
                        c2.add(new JLabel("  postpone  ", JLabel.CENTER));
                        this.vpostpone.addFocusListener(listener);
                        c.add(c2);
                    }
                }
                //

                private void changeFeature() {
                    // Commented out for testing
                    try {
                        Project project = ProjectManager.getCurrentProject();
                        Dependencies deps = project.getDependencies();
                        Features feats = project.getFeatures();
                        Releases rels = project.getReleases();

                        deps.removeInterdependency(this.dependency);

                        int index = this.feature.getSelectedIndex();
                        Feature f = feats.getFeature(index);

                        Urgency urg = ValueAndUrgency.createStandaloneUrgency();
                        for (int i = 0; i < this.vreleases.length; i++) {
                            String txt = this.vreleases[i].getText();
                            if (txt.length() > 0) {
                                try {
                                    int val = Integer.parseInt(txt);
                                    urg.setUrgency(rels.getRelease(i), val);
                                    
                                } catch (Exception ex) {
                                    this.vreleases[i].setText("");
                                }
                            }
                        }
                        if (this.vpostpone.getText().length() > 0) {
                            try {
                                int val = Integer.parseInt(this.vpostpone.getText());
                                urg.setUrgency(null, val);
                            } catch (Exception ex) {
                                this.vpostpone.setText("");
                            }
                        }
                        this.dependency = deps.addModifyingInterdependency(f, ChangePanel.this.feature, urg);
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    //
                }

                @Override
                public void contentPanelClosed(ContentPanel source) {
                    ProjectManager.getCurrentProject().getDependencies().removeInterdependency(this.dependency); // Commented out for testing
                    ChangePanel.this.closeChangePanel(ChangePanel.this.churg, source);
                }

                @Override
                public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
                }

                @Override
                public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
                }
            }
        }
    }
}
