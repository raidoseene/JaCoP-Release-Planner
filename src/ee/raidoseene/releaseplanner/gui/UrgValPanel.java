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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
                UVSContent.this.scrollable.contentUpdated();
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
                    UVSContent.this.scrollable.contentUpdated();
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
                UVFContent.this.scrollable.contentUpdated();
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
                    UVFContent.this.scrollable.contentUpdated();
                }

                @Override
                public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
                }

            }

        }

        private final class UrgencyPanel extends JPanel {
            
            private final Feature feature;
            private final Stakeholder stakeholder;
            private final JTextField[] vreleases;
            private final JTextField vpostpone;

            private UrgencyPanel(Stakeholder s, Feature f) {
                this.setBorder(new EmptyBorder(10, 0, 0, 0));
                this.setLayout(new BorderLayout());
                this.stakeholder = s;
                this.feature = f;

                Container c = new Container();
                c.setLayout(new BorderLayout(10, 10));
                this.add(BorderLayout.CENTER, c);
                
                Container grid = new Container();
                grid.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
                c.add(BorderLayout.LINE_START, grid);
                
                Project project = ProjectManager.getCurrentProject();
                ValueAndUrgency vus = project.getValueAndUrgency();
                Releases releases = project.getReleases();
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
                };
                
                String str = "        ";
                this.vreleases = new JTextField[releases.getReleaseCount()];
                for (int i = 0; i < this.vreleases.length; i++) {
                    int urg = vus.getUrgency(s, f, releases.getRelease(i));
                    String val = (urg > 0) ? Integer.toString(urg) : "";
                    
                    Container ge = new Container();
                    ge.setLayout(new GridLayout(2, 1, 10, 10));
                    ge.add(this.vreleases[i] = new JTextField(val));
                    ge.add(new JLabel(str + Integer.toString(i + 1) + str, JLabel.CENTER));
                    this.vreleases[i].addFocusListener(listener);
                    grid.add(ge);
                }
                
                {
                    int urg = vus.getUrgency(s, f, null);
                    String val = (urg > 0) ? Integer.toString(urg) : "";
                    
                    Container ge = new Container();
                    ge.setLayout(new GridLayout(2, 1, 10, 10));
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
                }
                
                Container btns = new Container();
                btns.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
                c.add(BorderLayout.PAGE_END, btns);
            }
            
            private void clearUrgency() {
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
                            ChangePanel.this.OpenChangePanel(btn);
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
            }
            
            private void OpenChangePanel(JButton button) {
                button.setEnabled(false);
                
                if (button == this.chval) {
                    ChangePanel.CPVContent content = new ChangePanel.CPVContent();
                    ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);
                    this.chpanel.add(panel);
                } else if (button == this.churg) {
                    
                } else {
                    return;
                }
                
                if (this.chpanel.getComponentCount() == 1) {
                    this.add(BorderLayout.PAGE_END, this.chpanel);
                }
                
                Container c = this.getParent();
                if (c != null && c instanceof ScrollablePanel) {
                    ((ScrollablePanel) c).contentUpdated();
                }
            }
            
            private class CPVContent extends JPanel {
                
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
                    for (ModifyingInterdependency md: mds) {
                        // TODO
                    }
                    
                    if (this.dependency == null) {
                        Value val = new Value(ChangePanel.this.feature, ChangePanel.this.stakeholder);
                        this.dependency = deps.addModifyingInterdependency(val.getFeature(), features.getFeature(0), val); // TODO: check if is correct
                    }
                    
                    this.feature = new JComboBox();
                    c.add(BorderLayout.CENTER, this.feature);
                    int fcount = features.getFeatureCount();
                    for (int i = 0; i < fcount; i++) {
                        Feature f = features.getFeature(i);
                        this.feature.addItem(f.getName());
                        
                        if (f == this.dependency.getSecondary()) { // TODO: check if is correct
                            this.feature.setSelectedIndex(i);
                        }
                    }
                    
                    c = new Container();
                    c.setLayout(new BorderLayout(10, 10));
                    c.add(BorderLayout.CENTER, new JLabel("Value to stakeholder"));
                    this.add(c);
                    
                    this.value = new JSpinner();
                    c.add(BorderLayout.LINE_START, this.value);
                    
                }
                
            }
            
        }

    }

}
