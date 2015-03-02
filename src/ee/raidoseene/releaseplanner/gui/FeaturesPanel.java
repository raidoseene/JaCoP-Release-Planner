/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.Groups;
import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Resource;
import ee.raidoseene.releaseplanner.datamodel.Resources;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ExtendableLayout;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Raido Seene
 */
public final class FeaturesPanel extends JPanel {

    public static final String TITLE_STRING = "Features";
    private final FeaturesPanel.FPScrollable scrollable;
    private final FeaturesPanel.DepHandler handler;

    public FeaturesPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new FeaturesPanel.FPScrollable();
        this.add(BorderLayout.CENTER, new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        Container c = new Container();
        this.add(BorderLayout.PAGE_END, c);
        c.add(this.handler = new DepHandler());
        c.setLayout(new LayoutManager() {

            @Override
            public void addLayoutComponent(String string, Component cmpnt) {
            }

            @Override
            public void removeLayoutComponent(Component cmpnt) {
            }

            @Override
            public Dimension preferredLayoutSize(Container cntnr) {
                Component[] comps = cntnr.getComponents();
                int height = comps[0].getPreferredSize().height;
                int width = 250;

                for (int i = 1; i < comps.length; i++) {
                    Dimension d = comps[i].getPreferredSize();
                    width += (10 + d.width);

                    if (d.height > height) {
                        height = d.height;
                    }
                }

                return new Dimension(width, height);
            }

            @Override
            public Dimension minimumLayoutSize(Container cntnr) {
                Component[] comps = cntnr.getComponents();
                int height = comps[0].getMinimumSize().height;
                int width = 250;

                for (int i = 1; i < comps.length; i++) {
                    Dimension d = comps[i].getMinimumSize();
                    width += (10 + d.width);

                    if (d.height > height) {
                        height = d.height;
                    }
                }

                return new Dimension(width, height);
            }

            @Override
            public void layoutContainer(Container cntnr) {
                Component[] comps = cntnr.getComponents();
                int height = cntnr.getHeight();
                int x = 250;

                comps[0].setBounds(0, 0, 250, height);
                for (int i = 1; i < comps.length; i++) {
                    int w = comps[i].getPreferredSize().width;
                    comps[i].setBounds(x + 10, 0, w, height);
                    x += (10 + w);
                }
            }
        });

        JButton btn = new JButton("Add new feature");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    FeaturesPanel.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        c.add(btn);

        btn = new JButton("Manage groups");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    FeaturesPanel.this.processManageEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        c.add(btn);
    }

    private void processAddEvent() {
        Feature f = ProjectManager.getCurrentProject().getFeatures().addFeature();
        FeaturesPanel.FPContent content = new FeaturesPanel.FPContent(f);
        ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE | ContentPanel.TYPE_EXPANDABLE | ContentPanel.TYPE_TOGGLEABLE);

        this.scrollable.add(panel);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private void processManageEvent() {
        GroupManagerDialog.showGroupManagerDialog();
        // Update group selections
        Component[] comps = this.scrollable.getComponents();
        for (Component comp : comps) {
            if (comp instanceof ContentPanel) {
                Component cont = ((ContentPanel) comp).getContent();
                if (cont != null && cont instanceof FeaturesPanel.FPContent) {
                    ((FeaturesPanel.FPContent) cont).updateGroupSelection();
                }
            }
        }
    }

    private final class FPScrollable extends ScrollablePanel {

        private FPScrollable() {
            this.setBorder(new EmptyBorder(10, 260, 10, 10));
            this.setLayout(new ContentListLayout(ContentPanel.class));

            Project project = ProjectManager.getCurrentProject();
            if (project != null) {
                Features features = project.getFeatures();
                int count = features.getFeatureCount();

                for (int i = 0; i < count; i++) {
                    Feature f = features.getFeature(i);
                    FeaturesPanel.FPContent content = new FeaturesPanel.FPContent(f);
                    ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE | ContentPanel.TYPE_EXPANDABLE | ContentPanel.TYPE_TOGGLEABLE);
                    panel.addContentPanelListener(content);
                    this.add(panel);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
        }

    }
    
    private final class DepHandler extends JPanel {
        
        private final JToggleButton fixed, req, and, xor;
        private final ArrayList<FPContent> selections;
        private final ActionListener listener;
        
        private DepHandler() {
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
            this.selections = new ArrayList<>(2);
            this.listener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        Object source = ae.getSource();
                        if (source == DepHandler.this.fixed) {
                            boolean state = DepHandler.this.fixed.isSelected();
                            DepHandler.this.req.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.req) {
                            boolean state = DepHandler.this.req.isSelected();
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.and) {
                            boolean state = DepHandler.this.and.isSelected();
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.req.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.xor) {
                            boolean state = DepHandler.this.xor.isSelected();
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.req.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                        }
                        
                        for (FPContent sel : DepHandler.this.selections) {
                            ((ContentPanel) sel.getParent()).setSelected(false);
                        }
                        DepHandler.this.selections.clear();
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                }
            };
            
            this.fixed = new JToggleButton("FIXED");
            this.fixed.addActionListener(this.listener);
            this.add(this.fixed);
            
            this.add(new JSeparator(JSeparator.VERTICAL));
            
            this.req = new JToggleButton("REQ");
            this.req.addActionListener(this.listener);
            this.add(this.req);
            
            this.add(new JSeparator(JSeparator.VERTICAL));
            
            this.and = new JToggleButton("AND");
            this.and.addActionListener(this.listener);
            this.add(this.and);
            
            this.xor = new JToggleButton("XOR");
            this.xor.addActionListener(this.listener);
            this.add(this.xor);
        }
        
        public boolean processFeatureEvent(FPContent source, boolean selected) {
            if (selected) {
                this.selections.add(source);
            } else {
                this.selections.remove(source);
            }
            
            if (this.fixed.isSelected()) {
                if (this.selections.size() == 1) {
                    Feature f = this.selections.get(0).feature;
                    FixedDependencyDialog.showFixedDependencyDialog(f);
                    
                    this.fixed.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.fixed, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.req.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addInterdependency(f1, f2, Dependency.REQ);
                    
                    this.req.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.req, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.and.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addInterdependency(f1, f2, Dependency.AND);
                    
                    this.and.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.and, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.xor.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addInterdependency(f1, f2, Dependency.XOR);
                    
                    this.xor.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.xor, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            }
            
            this.selections.clear();
            return false;
        }
        
    }

    private final class FPContent extends JPanel implements ContentPanelListener {

        private final Feature feature;
        private final JPanel cont1;
        private final JTextField name;
        private final JPanel cont2;
        private final FPContent.FPCPanel panel;
        private final JComboBox group;
        private final ActionListener listener;
        private final JRadioButton hours, days;

        private FPContent(Feature f) {
            this.setLayout(new ExtendableLayout(ExtendableLayout.VERTICAL, 10));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));

            this.feature = f;
            this.cont1 = new JPanel();
            this.cont1.setBorder(new EmptyBorder(0, 0, 0, 110));
            this.cont1.setLayout(new GridLayout(1, 1));
            this.add(this.cont1);

            this.name = new JTextField(feature.getName());
            this.name.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent fe) {
                }

                @Override
                public void focusLost(FocusEvent fe) {
                    try {
                        FPContent.this.feature.setName(FPContent.this.name.getText());
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    FPContent.this.name.setText(FPContent.this.feature.getName());
                }
            });
            this.cont1.add(this.name);

            this.cont2 = new JPanel();
            this.cont2.setLayout(new BorderLayout(10, 10));

            JPanel p = new JPanel();
            p.setBorder(new TitledBorder("Resource consumption"));
            p.setLayout(new BorderLayout(10, 10));
            this.cont2.add(BorderLayout.LINE_START, p);

            this.panel = new FPContent.FPCPanel();
            p.add(BorderLayout.CENTER, this.panel);
            this.addHierarchyListener(this.panel);

            JPanel p2 = new JPanel();
            p2.setBorder(new EmptyBorder(0, 5, 5, 5));
            p2.setLayout(new BorderLayout(10, 10));
            p.add(BorderLayout.PAGE_END, p2);

            JButton btn = new JButton("Add resource");
            btn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        FPContent.this.panel.addEmptyElement();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
            p2.add(BorderLayout.LINE_START, btn);
            p2.add(BorderLayout.LINE_END, new JButton("Change in cost"));
            
            Container c = new Container();
            c.setLayout(new BorderLayout(10, 10));
            this.cont2.add(BorderLayout.LINE_END, c);
            
            Container c1 = new Container();
            c1.setLayout(new BorderLayout(10, 10));
            c.add(BorderLayout.PAGE_START, c1);
            
            Container c2 = new Container();
            c2.setLayout(new GridLayout(1, 2, 5, 5));
            this.listener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        Groups groups = ProjectManager.getCurrentProject().getGroups();
                        int index = FPContent.this.group.getSelectedIndex();
                        if (index > 0) {
                            
                        } else {
                            
                        }
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                }
            };
            this.group = new JComboBox();
            this.updateGroupSelection();
            c2.add(this.group);
            c2.add(new JLabel("Belongs to"));
            c1.add(BorderLayout.PAGE_START, c2);
            
            c2 = new Container();
            c2.setLayout(new GridLayout(1, 2, 5, 5));
            Container c3 = new Container();
            c3.setLayout(new GridLayout(2, 1, 5, 5));
            c2.add(c3);
            c2.add(new JLabel("Time format"));
            c1.add(BorderLayout.CENTER, c2);
            
            ButtonGroup bg = new ButtonGroup();
            this.hours = new JRadioButton("hours");
            this.days = new JRadioButton("days");
            c3.add(this.hours);
            bg.add(this.hours);
            c3.add(this.days);
            bg.add(this.days);
        }
        
        private void updateGroupSelection() {
            try {
                Groups groups = ProjectManager.getCurrentProject().getGroups();
                Group ogroup = groups.getGroupByFeature(this.feature);
                int count = groups.getGroupCount();
                
                this.group.removeActionListener(this.listener);
                this.group.removeAllItems();
                this.group.addItem("None");
                this.group.setSelectedIndex(0);
                
                for (int i = 0; i < count; i++) {
                    Group g = groups.getGroup(i);
                    this.group.addItem(g.getName());
                    if (ogroup == g) {
                        this.group.setSelectedIndex(i + 1);
                    }
                }
                
                this.group.addActionListener(this.listener);
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            try {
                Features features = ProjectManager.getCurrentProject().getFeatures();
                features.removeFeature(this.feature);

                FeaturesPanel.this.scrollable.remove(source);
                FeaturesPanel.this.scrollable.contentUpdated();
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }

        @Override
        public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
            if (expanded && this.getComponentCount() == 1) {
                this.add(this.cont2);
            } else if (!expanded && this.getComponentCount() > 1) {
                this.remove(this.cont2);
            }
            FeaturesPanel.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
            if (!FeaturesPanel.this.handler.processFeatureEvent(this, selected) && selected) {
                ((ContentPanel) this.getParent()).setSelected(false);
            }
        }

        private final class FPCPanel extends JPanel implements HierarchyListener {

            private final ArrayList<JTextField> texts;
            private final ArrayList<JComboBox> combos;
            private final FocusListener flistener;
            private final ItemListener ilistener;
            private String[] selection;

            private FPCPanel() {
                this.setBorder(new EmptyBorder(5, 5, 0, 5));

                this.texts = new ArrayList<>();
                this.combos = new ArrayList<>();
                this.flistener = new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent fe) {
                    }

                    @Override
                    public void focusLost(FocusEvent fe) {
                        try {
                            Object source = fe.getSource();
                            int index = FPCPanel.this.texts.indexOf(source);
                            int select = FPCPanel.this.combos.get(index).getSelectedIndex();
                            if (select < 1) {
                                FPCPanel.this.texts.get(index).setText("0");
                                return;
                            }

                            Resource r = ProjectManager.getCurrentProject().getResources().getResource(select - 1);
                            JTextField tf = FPCPanel.this.texts.get(index);
                            try {
                                FPContent.this.feature.setConsumption(r, Integer.parseInt(tf.getText()));
                            } catch (Exception e2) {
                                Messenger.showError(e2, null);
                            }
                            tf.setText(Integer.toString(FPContent.this.feature.getConsumption(r)));
                        } catch (Exception ex) {
                            Messenger.showError(ex, null);
                        }
                    }
                };
                this.ilistener = new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent ie) {
                        try {
                            Object item = ie.getItem();
                            if (ie.getStateChange() == ItemEvent.DESELECTED) {
                                int sindex = FPCPanel.this.getSelectionIndex(item);
                                if (sindex > 0) {
                                    Resource r = ProjectManager.getCurrentProject().getResources().getResource(sindex - 1);
                                    FPContent.this.feature.setConsumption(r, 0);
                                }
                            } else if (ie.getStateChange() == ItemEvent.SELECTED) {
                                int sindex = FPCPanel.this.getSelectionIndex(item);
                                int index = FPCPanel.this.combos.indexOf(ie.getSource());
                                if (sindex > 0) {
                                    JComboBox cb = FPCPanel.this.combos.get(index);
                                    for (JComboBox c : FPCPanel.this.combos) {
                                        if (c != cb && c.getSelectedIndex() == sindex) {
                                            cb.setSelectedIndex(0);

                                            throw new Exception("Duplicate resource!");
                                        }
                                    }
                                }

                                JTextField tf = FPCPanel.this.texts.get(index);
                                FocusEvent fe = new FocusEvent(tf, FocusEvent.FOCUS_LOST);
                                FPCPanel.this.flistener.focusLost(fe);
                            }
                        } catch (Exception ex) {
                            Messenger.showError(ex, null);
                        }
                    }
                };

                this.reinitializeElements();
                if (this.texts.size() < 1) {
                    this.addEmptyElement();
                }
            }

            private void reinitializeElements() {
                Resources rs = ProjectManager.getCurrentProject().getResources();
                Feature f = FPContent.this.feature;
                int rcount = rs.getResourceCount();
                int count = 0;

                this.texts.clear();
                this.combos.clear();
                this.removeAll();

                this.selection = new String[rcount + 1];
                this.selection[0] = new String();
                for (int i = 0; i < rcount; i++) {
                    this.selection[i + 1] = rs.getResource(i).getName();
                }

                for (int i = 0; i < rcount; i++) {
                    Resource r = rs.getResource(i);
                    if (f.getConsumption(r) > 0) {
                        JTextField tf = new JTextField(Integer.toString(f.getConsumption(r)));
                        tf.addFocusListener(this.flistener);
                        this.texts.add(tf);
                        this.add(tf);

                        JComboBox cb = new JComboBox(this.selection);
                        cb.setSelectedIndex(i + 1); // Items are shifted, because the first on is empty
                        cb.addItemListener(this.ilistener);
                        this.combos.add(cb);
                        this.add(cb);

                        count++;
                    }
                }

                this.setLayout(new GridLayout(count, 2, 10, 2));
            }

            private void addEmptyElement() {
                JTextField tf = new JTextField("0");
                tf.addFocusListener(this.flistener);
                this.texts.add(tf);
                this.add(tf);

                JComboBox cb = new JComboBox(this.selection);
                cb.addItemListener(this.ilistener);
                this.combos.add(cb);
                this.add(cb);

                int count = this.texts.size();
                this.setLayout(new GridLayout(count, 2, 10, 2));
            }

            private int getSelectionIndex(Object object) {
                for (int i = 0; i < this.selection.length; i++) {
                    if (this.selection[i] == object) {
                        return i;
                    }
                }

                return -1;
            }

            @Override
            public void hierarchyChanged(HierarchyEvent he) {
                int mask = HierarchyEvent.SHOWING_CHANGED;
                if ((he.getChangeFlags() & mask) == mask && he.getChanged().isShowing()) {
                    this.reinitializeElements();
                    if (this.texts.size() < 1) {
                        this.addEmptyElement();
                    }
                }
            }

        }

    }
}
