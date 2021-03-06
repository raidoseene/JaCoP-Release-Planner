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
import ee.raidoseene.releaseplanner.datamodel.ModifyingParameterDependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Resource;
import ee.raidoseene.releaseplanner.datamodel.Resources;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.DependencyGraph;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
        this.add(new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        this.add(this.handler = new DepHandler());
        this.setLayout(this.handler);

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
        this.add(btn);

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
        this.add(btn);

        Project project = ProjectManager.getCurrentProject();
        if (project != null) {
            Features features = project.getFeatures();
            int count = features.getFeatureCount();

            for (int i = 0; i < count; i++) {
                Feature f = features.getFeature(i);
                FeaturesPanel.FPContent content = new FeaturesPanel.FPContent(f);
                ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE | ContentPanel.TYPE_EXPANDABLE | ContentPanel.TYPE_TOGGLEABLE);
                panel.addContentPanelListener(content);
                this.scrollable.add(panel);
            }
        }
    }

    private void processAddEvent() {
        Feature f = ProjectManager.getCurrentProject().getFeatures().addFeature();
        FeaturesPanel.FPContent content = new FeaturesPanel.FPContent(f);
        ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE | ContentPanel.TYPE_EXPANDABLE | ContentPanel.TYPE_TOGGLEABLE);

        this.scrollable.add(panel);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        this.scrollable.updateGraph();
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
        
        private DependencyGraph graphData;
        private boolean visibility;

        private FPScrollable() {
            this.setBorder(new EmptyBorder(10, 310, 10, 10));
            this.setLayout(new ContentListLayout(ContentPanel.class));
            this.visibility = true;
            
            try {
                this.graphData = new DependencyGraph(ProjectManager.getCurrentProject());
            } catch (Exception ex) {
                this.graphData = null;
                ex.printStackTrace();
            }
        }
        
        private void setGraphicsVisible(boolean visible) {
            if (this.visibility != visible) {
                this.visibility = visible;
                
                if (this.visibility) {
                    this.setBorder(new EmptyBorder(10, 310, 10, 10));
                    this.updateGraph();
                } else {
                    this.setBorder(new EmptyBorder(10, 10, 10, 10));
                }
            }
        }
        
        private void updateGraph() {
            try {
                this.graphData = new DependencyGraph(ProjectManager.getCurrentProject());
            } catch (Exception ex) {
                this.graphData = null;
                ex.printStackTrace();
            }
            
            Insets is = this.getInsets();
            this.repaint(0, 0, is.left, this.getHeight());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            
            DependencyGraph gdata = this.graphData;
            if (this.visibility && gdata != null) {
                Graphics2D g = (Graphics2D) graphics;
                FontMetrics fmetrics = g.getFontMetrics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.max(fmetrics.getMaxAdvance(), fmetrics.getHeight()) + 10;
                int arc = size >> 2;
                int oval = arc << 1;
                
                Component[] comps = this.getComponents();
                Rectangle rect = new Rectangle();
                
                int dcount = gdata.getDependencyCount();
                for (int di = 0; di < dcount; di++) {
                    DependencyGraph.Dependency dep = gdata.getDependency(di);
                    if (dep.node1 < comps.length && dep.node2 < comps.length) {
                        comps[dep.node1].getBounds(rect);
                        int dheight = dep.getHeight() * size;
                        int y = rect.y + (int) (rect.height * dep.getAnchor1());
                        int x = rect.x;
                        
                        if (dep.distance > 0) {
                            comps[dep.node2].getBounds(rect);
                            int y2 = rect.y + (int) (rect.height * dep.getAnchor2());
                            int x2 = rect.x;
                            
                            g.setColor(this.getForeground());
                            g.drawLine(x, y, x - dheight + arc, y);
                            g.drawLine(x2, y2, x2 - dheight + arc, y2);
                            g.drawLine(x - dheight, y + arc, x2 - dheight, y2 - arc);
                            g.drawArc(x - dheight, y2 - oval, oval, oval, 180, 90);
                            g.drawArc(x - dheight, y, oval, oval, 90, 90);
                        } else {
                            g.drawLine(x, y, x - dheight, y);
                        }
                        
                    }
                }
                
                // Draw labels
                for (int di = 0; di < dcount; di++) {
                    DependencyGraph.Dependency dep = gdata.getDependency(di);
                    int sh = fmetrics.getMaxAscent() + fmetrics.getMaxDescent();
                    int fh = fmetrics.getHeight();
                    
                    if (dep.node1 < comps.length && dep.node2 < comps.length) {
                        comps[dep.node1].getBounds(rect);
                        int dheight = dep.getHeight() * size;
                        int y = rect.y + (int) (rect.height * dep.getAnchor1());
                        int x = rect.x;
                        
                        if (dep.distance > 0) {
                            comps[dep.node2].getBounds(rect);
                            int y2 = rect.y + (int) (rect.height * dep.getAnchor2());
                            int sw = fmetrics.stringWidth(dep.text);
                            
                            g.setColor(Color.WHITE);
                            g.fillRoundRect(x - dheight - (sw >> 1) - 2, (y + y2 - fh) >> 1, sw + 4, fh, arc, arc);
                            
                            g.setColor(this.getForeground());
                            g.drawRoundRect(x - dheight - (sw >> 1) - 2, (y + y2 - fh) >> 1, sw + 4, fh, arc, arc);
                            g.drawString(dep.text, x - dheight - (sw >> 1), (y + y2 + fmetrics.getAscent()) >> 1);
                        } else {
                            int sw = fmetrics.stringWidth(dep.text);
                            
                            g.setColor(Color.WHITE);
                            g.fillRoundRect(x - dheight - sw - 2, y - (fh >> 1), sw + 4, fh, arc, arc);
                            
                            g.setColor(this.getForeground());
                            g.drawRoundRect(x - dheight - sw - 2, y - (fh >> 1), sw + 4, fh, arc, arc);
                            g.drawString(dep.text, x - dheight - sw, y + (fmetrics.getAscent() >> 1));
                        }
                        
                    }
                }
                
            }
        }

    }

    private final class DepHandler extends JPanel implements LayoutManager {

        private final JToggleButton fixed, excluded, earlier, later, softPrecedence, hardPrecedence, coupling, separation, and, xor;
        private final ArrayList<FPContent> selections;
        private final ActionListener listener;
        private final JPanel buttons;

        private DepHandler() {
            this.setLayout(new BorderLayout());
            this.selections = new ArrayList<>(2);
            this.listener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        Object source = ae.getSource();
                        if (source == DepHandler.this.fixed) {
                            boolean state = DepHandler.this.fixed.isSelected();
                            
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.excluded) {
                            boolean state = DepHandler.this.excluded.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.earlier) {
                            boolean state = DepHandler.this.earlier.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.later) {
                            boolean state = DepHandler.this.later.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.softPrecedence) {
                            boolean state = DepHandler.this.softPrecedence.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.hardPrecedence) {
                            boolean state = DepHandler.this.hardPrecedence.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.coupling) {
                            boolean state = DepHandler.this.coupling.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.separation) {
                            boolean state = DepHandler.this.separation.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.and.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.and) {
                            boolean state = DepHandler.this.and.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
                            DepHandler.this.xor.setEnabled(!state);
                        } else if (source == DepHandler.this.xor) {
                            boolean state = DepHandler.this.xor.isSelected();
                            
                            DepHandler.this.fixed.setEnabled(!state);
                            DepHandler.this.excluded.setEnabled(!state);
                            DepHandler.this.earlier.setEnabled(!state);
                            DepHandler.this.later.setEnabled(!state);
                            DepHandler.this.softPrecedence.setEnabled(!state);
                            DepHandler.this.hardPrecedence.setEnabled(!state);
                            DepHandler.this.coupling.setEnabled(!state);
                            DepHandler.this.separation.setEnabled(!state);
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
            
            this.buttons = new JPanel(new GridLayout(12, 1, 2, 2));
            this.buttons.setBorder(new EmptyBorder(5, 5, 5, 5));
            
            this.fixed = new JToggleButton("FIXED");
            this.fixed.addActionListener(this.listener);
            this.buttons.add(this.fixed);
            
            this.excluded = new JToggleButton("EXCLUDED");
            this.excluded.addActionListener(this.listener);
            this.buttons.add(this.excluded);
            
            this.earlier = new JToggleButton("EARLIER");
            this.earlier.addActionListener(this.listener);
            this.buttons.add(this.earlier);
            
            this.later = new JToggleButton("LATER");
            this.later.addActionListener(this.listener);
            this.buttons.add(this.later);

            this.buttons.add(new JSeparator(JSeparator.HORIZONTAL));

            this.softPrecedence = new JToggleButton("SOFTPRECEDENCE");
            this.softPrecedence.addActionListener(this.listener);
            this.buttons.add(this.softPrecedence);

            this.hardPrecedence = new JToggleButton("HARDPRECEDENCE");
            this.hardPrecedence.addActionListener(this.listener);
            this.buttons.add(this.hardPrecedence);
            
            this.coupling = new JToggleButton("COUPLING");
            this.coupling.addActionListener(this.listener);
            this.buttons.add(this.coupling);

            this.separation = new JToggleButton("SEPARATION");
            this.separation.addActionListener(this.listener);
            this.buttons.add(this.separation);

            this.buttons.add(new JSeparator(JSeparator.HORIZONTAL));

            this.and = new JToggleButton("AND");
            this.and.addActionListener(this.listener);
            this.buttons.add(this.and);

            this.xor = new JToggleButton("XOR");
            this.xor.addActionListener(this.listener);
            this.buttons.add(this.xor);
            
            JToggleButton toggler = new JToggleButton("Dependencies");
            toggler.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (FeaturesPanel.this.scrollable.visibility) {
                            FeaturesPanel.this.add(FeaturesPanel.DepHandler.this.buttons);
                            FeaturesPanel.this.scrollable.setGraphicsVisible(false);
                        } else {
                            FeaturesPanel.this.remove(FeaturesPanel.DepHandler.this.buttons);
                            FeaturesPanel.this.scrollable.setGraphicsVisible(true);
                        }
                        FeaturesPanel.this.validate();
                        FeaturesPanel.this.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            this.add(toggler);
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
                    FixedDependencyDialog.showFixedDependencyDialog(f, Dependency.FIXED);

                    this.fixed.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.fixed, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.excluded.isSelected()) {
                if (this.selections.size() == 1) {
                    Feature f = this.selections.get(0).feature;
                    FixedDependencyDialog.showFixedDependencyDialog(f, Dependency.EXCLUDED);

                    this.excluded.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.excluded, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;   
            } else if (this.earlier.isSelected()) {
                if (this.selections.size() == 1) {
                    Feature f = this.selections.get(0).feature;
                    FixedDependencyDialog.showFixedDependencyDialog(f, Dependency.EARLIER);

                    this.earlier.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.earlier, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.later.isSelected()) {
                if (this.selections.size() == 1) {
                    Feature f = this.selections.get(0).feature;
                    FixedDependencyDialog.showFixedDependencyDialog(f, Dependency.LATER);

                    this.later.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.later, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.softPrecedence.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addOrderDependency(f1, f2, Dependency.SOFTPRECEDENCE);

                    this.softPrecedence.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.softPrecedence, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.hardPrecedence.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addOrderDependency(f1, f2, Dependency.HARDPRECEDENCE);

                    this.hardPrecedence.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.hardPrecedence, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.coupling.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addOrderDependency(f1, f2, Dependency.COUPLING);

                    this.coupling.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.coupling, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.separation.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addOrderDependency(f1, f2, Dependency.SEPARATION);

                    this.separation.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.separation, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            } else if (this.and.isSelected()) {
                if (this.selections.size() == 2) {
                    Dependencies ids = ProjectManager.getCurrentProject().getDependencies();
                    Feature f1 = this.selections.get(0).feature;
                    Feature f2 = this.selections.get(1).feature;
                    ids.addExistanceDependency(f1, f2, Dependency.AND);

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
                    ids.addExistanceDependency(f1, f2, Dependency.XOR);

                    this.xor.setSelected(false);
                    ActionEvent ae = new ActionEvent(this.xor, ActionEvent.ACTION_PERFORMED, null);
                    this.listener.actionPerformed(ae);
                }
                return true;
            }

            this.selections.clear();
            return false;
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension();
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension();
        }

        @Override
        public void layoutContainer(Container parent) {
            Insets is = parent.getInsets();
            int width = parent.getWidth();
            int height = parent.getHeight();
            
            int deph = this.getPreferredSize().height;
            int h = height - is.top - is.bottom - deph - 10;
            this.setBounds(is.left, height - is.bottom - deph, 300, deph);
            
            int x = is.left + 300 + 10;
            int count = parent.getComponentCount();
            for (int i = 2; i < count; i++) {
                Component c = parent.getComponent(i);
                Dimension pref = c.getPreferredSize();
                c.setBounds(x, height - is.bottom - deph, pref.width, pref.height);
                x += (pref.width + 10);
            }
            
            if (!FeaturesPanel.this.scrollable.visibility) {
                int w = width - is.left - is.right - 300;
                parent.getComponent(0).setBounds(is.left + 300, is.top, w, h);
                
                Dimension pref = this.buttons.getPreferredSize();
                this.buttons.setBounds(is.left, height - is.bottom - deph - pref.height - 10, 300, pref.height);
            } else {
                int w = width - is.left - is.right;
                parent.getComponent(0).setBounds(is.left, is.top, w, h);
            }
        }

    }

    private final class FPContent extends JPanel implements ContentPanelListener {

        private final Feature feature;
        private final JPanel cont1;
        private final JTextField name;
        private final JPanel cont2;
        private final JButton chcost;
        private final FPContent.FPCPanel panel;
        private final JComboBox group;
        private final ActionListener listener;
        private final JRadioButton hours, days;

        private FPContent(Feature f) {
            this.setLayout(new BorderLayout(10, 10));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));

            this.feature = f;
            this.cont1 = new JPanel();
            this.cont1.setBorder(new EmptyBorder(0, 0, 0, 110));
            this.cont1.setLayout(new GridLayout(1, 1));
            this.add(BorderLayout.CENTER, this.cont1);

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
                        FeaturesPanel.this.scrollable.contentUpdated();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
            p2.add(BorderLayout.LINE_START, btn);

            this.chcost = new JButton("Change in cost");
            this.chcost.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        JButton btn = (JButton) ae.getSource();
                        FPContent.this.openChangePanel(btn);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
            p2.add(BorderLayout.LINE_END, this.chcost);

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
                            Group g = groups.getGroup(index - 1);
                            groups.addFeature(g, FPContent.this.feature);
                        } else {
                            Group g = groups.getGroupByFeature(FPContent.this.feature);
                            if (g != null) {
                                g.removeFeature(FPContent.this.feature);
                            }
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

            Project project = ProjectManager.getCurrentProject();
            ModifyingParameterDependency[] deps = project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CC);
            for (ModifyingParameterDependency dep : deps) {
                if (dep.getSecondary() == this.feature) {
                    this.openChangePanel(this.chcost);
                    break;
                }
            }
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

        private void openChangePanel(JButton button) {
            button.setEnabled(false);

            FPContent.FPCCContent content = new FPContent.FPCCContent();
            ContentPanel p = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);
            p.addContentPanelListener(content);
            p.addHierarchyListener(content);

            this.cont2.add(BorderLayout.PAGE_END, p);
            FeaturesPanel.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            try {
                Features features = ProjectManager.getCurrentProject().getFeatures();
                features.removeFeature(this.feature);

                FeaturesPanel.this.scrollable.remove(source);
                FeaturesPanel.this.scrollable.contentUpdated();
                FeaturesPanel.this.scrollable.updateGraph();
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }

        @Override
        public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
            if (expanded && this.getComponentCount() == 1) {
                this.add(BorderLayout.PAGE_END, this.cont2);
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

        private final class FPCCContent extends JPanel implements ContentPanelListener, HierarchyListener {

            private ModifyingParameterDependency dependency;
            private final JComboBox feature;
            private final ArrayList<JTextField> texts;
            private final ArrayList<JComboBox> combos;
            private final FocusListener flistener;
            private final ItemListener ilistener;
            private final Container cpanel;
            private String[] selection;

            private FPCCContent() {
                this.setBorder(new EmptyBorder(10, 10, 10, 80));
                this.setLayout(new BorderLayout(10, 10));
                this.reinitializeDependency();
                Container c, c2;

                c = new Container();
                c.setLayout(new GridLayout(2, 1, 10, 10));
                c.add(new JLabel("Change in cost"));
                this.add(BorderLayout.PAGE_START, c);

                c2 = new Container();
                c2.setLayout(new BorderLayout(10, 10));
                c.add(c2);

                this.feature = new JComboBox();
                c2.add(BorderLayout.CENTER, this.feature);
                c2.add(BorderLayout.LINE_END, new JLabel("Preceding feature"));

                Features feats = ProjectManager.getCurrentProject().getFeatures();
                int fcount = feats.getFeatureCount();
                for (int i = 0; i < fcount; i++) {
                    Feature f = feats.getFeature(i);
                    this.feature.addItem(f.getName());
                    if (f == this.dependency.getPrimary()) {
                        this.feature.setSelectedIndex(i);
                    }
                }
                this.feature.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        FPCCContent.this.changeFeature();
                    }
                });

                this.cpanel = new Container();
                this.texts = new ArrayList<>();
                this.combos = new ArrayList<>();
                this.add(BorderLayout.LINE_START, this.cpanel);

                this.flistener = new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent fe) {
                    }

                    @Override
                    public void focusLost(FocusEvent fe) {
                        try {
                            Object source = fe.getSource();
                            int index = FPCCContent.this.texts.indexOf(source);
                            int select = FPCCContent.this.combos.get(index).getSelectedIndex();
                            if (select < 1) {
                                FPCCContent.this.texts.get(index).setText("0");
                                return;
                            }

                            Resource r = ProjectManager.getCurrentProject().getResources().getResource(select - 1);
                            Feature f = FPCCContent.this.dependency.getChange(Feature.class);
                            JTextField tf = FPCCContent.this.texts.get(index);
                            try {
                                f.setConsumption(r, Integer.parseInt(tf.getText()));
                            } catch (Exception e2) {
                                Messenger.showError(e2, null);
                            }
                            tf.setText(Integer.toString(f.getConsumption(r)));
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
                                int sindex = FPCCContent.this.getSelectionIndex(item);
                                if (sindex > 0) {
                                    Resource r = ProjectManager.getCurrentProject().getResources().getResource(sindex - 1);
                                    FPCCContent.this.dependency.getChange(Feature.class).setConsumption(r, 0);
                                }
                            } else if (ie.getStateChange() == ItemEvent.SELECTED) {
                                int sindex = FPCCContent.this.getSelectionIndex(item);
                                int index = FPCCContent.this.combos.indexOf(ie.getSource());
                                if (sindex > 0) {
                                    JComboBox cb = FPCCContent.this.combos.get(index);
                                    for (JComboBox c : FPCCContent.this.combos) {
                                        if (c != cb && c.getSelectedIndex() == sindex) {
                                            cb.setSelectedIndex(0);

                                            throw new Exception("Duplicate resource!");
                                        }
                                    }
                                }

                                JTextField tf = FPCCContent.this.texts.get(index);
                                FocusEvent fe = new FocusEvent(tf, FocusEvent.FOCUS_LOST);
                                FPCCContent.this.flistener.focusLost(fe);
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

                c = new Container();
                c.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                this.add(BorderLayout.LINE_END, c);

                JButton btn = new JButton("Add resource");
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        try {
                            FPCCContent.this.addEmptyElement();
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                });
                c.add(btn);
            }

            private void reinitializeDependency() {
                Project project = ProjectManager.getCurrentProject();
                ModifyingParameterDependency[] deps = project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CC);
                Feature feat = FPContent.this.feature;

                for (ModifyingParameterDependency dep : deps) {
                    if (dep.getSecondary() == feat) {
                        this.dependency = dep;
                        return;
                    }
                }

                Feature f = Features.createStandaloneFeature();
                Feature f1 = project.getFeatures().getFeature(0);
                this.dependency = project.getDependencies().addModifyingParameterDependency(f1, feat, f);
            }

            private void reinitializeElements() {
                Resources rs = ProjectManager.getCurrentProject().getResources();
                Feature f = FPCCContent.this.dependency.getChange(Feature.class);
                int rcount = rs.getResourceCount();
                int count = 0;

                this.texts.clear();
                this.combos.clear();
                this.cpanel.removeAll();

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
                        this.cpanel.add(tf);

                        JComboBox cb = new JComboBox(this.selection);
                        cb.setSelectedIndex(i + 1); // Items are shifted, because the first on is empty
                        cb.addItemListener(this.ilistener);
                        this.combos.add(cb);
                        this.cpanel.add(cb);

                        count++;
                    }
                }

                this.cpanel.setLayout(new GridLayout(count, 2, 10, 2));
            }

            private void addEmptyElement() {
                JTextField tf = new JTextField("0");
                tf.addFocusListener(this.flistener);
                this.texts.add(tf);
                this.cpanel.add(tf);

                JComboBox cb = new JComboBox(this.selection);
                cb.addItemListener(this.ilistener);
                this.combos.add(cb);
                this.cpanel.add(cb);

                int count = this.texts.size();
                this.cpanel.setLayout(new GridLayout(count, 2, 10, 2));
            }

            private void changeFeature() {
                try {
                    Project project = ProjectManager.getCurrentProject();
                    Dependencies deps = project.getDependencies();
                    Resources rsrcs = project.getResources();
                    Features feats = project.getFeatures();
                    
                    Feature old = this.dependency.getChange(Feature.class);
                    deps.removeInterdependency(this.dependency);
                    
                    int index = this.feature.getSelectedIndex();
                    Feature f1 = feats.getFeature(index);
                    
                    Feature f = Features.createStandaloneFeature();
                    int rcount = rsrcs.getResourceCount();
                    for (int i = 0; i < rcount; i++) {
                        Resource r = rsrcs.getResource(i);
                        f.setConsumption(r, old.getConsumption(r));
                    }
                    this.dependency = deps.addModifyingParameterDependency(f1, FPContent.this.feature, f);
                } catch (Exception ex) {
                    Messenger.showError(ex, null);
                }
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
            public void contentPanelClosed(ContentPanel source) {
                Project project = ProjectManager.getCurrentProject();
                project.getDependencies().removeInterdependency(this.dependency);

                FPContent.this.cont2.remove(source);
                FPContent.this.chcost.setEnabled(true);
                FeaturesPanel.this.scrollable.contentUpdated();
            }

            @Override
            public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
            }

            @Override
            public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
            }

            @Override
            public void hierarchyChanged(HierarchyEvent he) {
                int mask = HierarchyEvent.SHOWING_CHANGED;
                if ((he.getChangeFlags() & mask) == mask && he.getChanged().isShowing()) {
                    this.reinitializeDependency();
                    this.reinitializeElements();
                    if (this.texts.size() < 1) {
                        this.addEmptyElement();
                    }
                }
            }

        }

    }
}
