/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import ee.raidoseene.releaseplanner.datamodel.CandidatePlan;
import ee.raidoseene.releaseplanner.datamodel.Simulation;
import ee.raidoseene.releaseplanner.datamodel.SimulationArchive;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Raido Seene
 */
public final class SimulationPanel extends ScrollablePanel {

    public static final String TITLE_STRING = "Simulations";
    private final ScrollablePanel scrollable;
    private final JButton simulationButton;

    public SimulationPanel() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new ScrollablePanel();
        this.add(new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

        Project project = ProjectManager.getCurrentProject();
        if (project != null) {
            SimulationArchive archive = project.getSimulationArchive();
            int count = archive.getSimulationCount();

            for (int i = 0; i < count; i++) {
                Simulation s = archive.getSimulation(i);
                SimulationPanel.SPContent content = new SimulationPanel.SPContent(s);
                int type = ContentPanel.TYPE_CLOSABLE | (s.isConsistan() ? ContentPanel.TYPE_EXPANDABLE : 0);
                ContentPanel panel = new ContentPanel(content, type);
                panel.addContentPanelListener(content);
                this.scrollable.add(panel);
            }
        }

        this.simulationButton = new JButton("Simulate plan");
        this.simulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    SimulationPanel.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.scrollable.add(this.simulationButton);
    }

    private void processAddEvent() {
        if (SimulationDialog.showSimulationDialog()) {
            Project project = ProjectManager.getCurrentProject();
            this.scrollable.removeAll();

            if (project != null) {
                SimulationArchive archive = project.getSimulationArchive();
                int count = archive.getSimulationCount();

                for (int i = 0; i < count; i++) {
                    Simulation s = archive.getSimulation(i);
                    SimulationPanel.SPContent content = new SimulationPanel.SPContent(s);
                    int type = ContentPanel.TYPE_CLOSABLE | (s.isConsistan() ? ContentPanel.TYPE_EXPANDABLE : 0);
                    ContentPanel panel = new ContentPanel(content, type);
                    panel.addContentPanelListener(content);
                    this.scrollable.add(panel);
                }
            }

            this.scrollable.add(this.simulationButton);
            this.scrollable.contentUpdated();
            this.scrollable.scrollDown();
        }
    }

    private final class SPContent extends JPanel implements ContentPanelListener, HierarchyListener {

        private final Simulation simulation;
        private final JPanel cont1;
        private final JTextField comment;
        private final JScrollPane scroller;
        private final ScrollablePanel scrollable;

        private SPContent(Simulation s) {
            this.setLayout(new BorderLayout(10, 10));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));

            this.simulation = s;
            this.cont1 = new JPanel();
            this.cont1.setBorder(new EmptyBorder(0, 0, 0, 110));
            this.cont1.setLayout(new BorderLayout(25, 25));
            this.add(BorderLayout.CENTER, this.cont1);

            SimulationArchive archive = ProjectManager.getCurrentProject().getSimulationArchive();
            int simNo = archive.getSimulationIndex(s) + 1;
            this.comment = new JTextField();
            if (s.isConsistan()) {
                cont1.add(BorderLayout.LINE_START, new JLabel("Simulation no " + simNo + ": " + s.getSimulationDate() + ", calculation duration: " + s.getSimulationDuration() + "ms"));

                this.comment.setText(s.getComment());
                this.comment.addFocusListener(new FocusListener() {
                    @Override
                    public void focusGained(FocusEvent fe) {
                    }

                    @Override
                    public void focusLost(FocusEvent fe) {
                        try {
                            SimulationPanel.SPContent.this.simulation.setComment(SimulationPanel.SPContent.this.comment.getText());
                        } catch (Exception ex) {
                            Messenger.showError(ex, null);
                        }
                        SimulationPanel.SPContent.this.comment.setText(SimulationPanel.SPContent.this.simulation.getComment());
                    }
                });
                cont1.add(BorderLayout.CENTER, this.comment);
            } else {
                cont1.add(BorderLayout.LINE_START, new JLabel("Simulation no " + simNo + ": " + s.getSimulationDate() + ". Inconsistent simulation. Project contains unsatisfiable dependencies!"));
            }
            
            this.scrollable = new ScrollablePanel();
            this.scroller = new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
            this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

            this.scrollable.addHierarchyListener(this);
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            try {
                SimulationArchive archive = ProjectManager.getCurrentProject().getSimulationArchive();
                archive.removeSimulation(this.simulation);

                SimulationPanel.this.scrollable.remove(source);
                SimulationPanel.this.scrollable.contentUpdated();
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }

        @Override
        public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
            if (expanded && this.getComponentCount() == 1) {
                this.add(BorderLayout.PAGE_END, this.scroller);
            } else if (!expanded && this.getComponentCount() > 1) {
                this.remove(this.scroller);
            }
            SimulationPanel.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
        }

        @Override
        public void hierarchyChanged(HierarchyEvent he) {
            int mask = HierarchyEvent.SHOWING_CHANGED;
            if ((he.getChangeFlags() & mask) == mask && he.getChanged().isShowing()) {
                int count = this.simulation.getCandidatePlanCount();
                this.scrollable.removeAll();

                for (int i = count - 1; i >= 0; i--) {
                    CandidatePlan cp = this.simulation.getCandidatePlan(i);
                    SimulationPanel.SPContent.SPCPanel content = new SimulationPanel.SPContent.SPCPanel(cp);
                    ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE | ContentPanel.TYPE_EXPANDABLE);
                    panel.addContentPanelListener(content);
                    this.scrollable.add(panel);
                }
            }
        }

        private final class SPCPanel extends JPanel implements ContentPanelListener {

            private final CandidatePlan plan;
            private final JPanel cont1, cont2;

            private SPCPanel(CandidatePlan cp) {
                this.setLayout(new BorderLayout(10, 10));
                this.setBorder(new EmptyBorder(10, 10, 10, 10));

                this.plan = cp;
                this.cont1 = new JPanel();
                this.cont1.setBorder(new EmptyBorder(0, 0, 0, 110));
                this.cont1.setLayout(new BorderLayout(25, 25));
                this.add(BorderLayout.CENTER, this.cont1);

                cont1.add(BorderLayout.LINE_START, new JLabel("Candidate plan value: " + cp.getPlanValue()));

                this.cont2 = new JPanel(new GridLayout(1, 2, 10, 10));


                DefaultTableModel dm = new DefaultTableModel();
                dm.addColumn("Feature");
                dm.addColumn("Release");
                String[][] table = cp.getFeatureAllocationTable();
                for (String[] row : table) {
                    dm.addRow(row);
                }

                JTable table1 = new JTable(dm);
                cont2.add(table1);

                dm = new DefaultTableModel();
                dm.addColumn("Release");
                dm.addColumn("Features");
                table = cp.getReleaseContentTable();
                for (String[] row : table) {
                    dm.addRow(row);
                }

                JTable table2 = new JTable(dm);
                cont2.add(table2);
            }

            @Override
            public void contentPanelClosed(ContentPanel source) {
                try {
                    SPContent.this.simulation.removeCandidatePlan(this.plan);

                    SPContent.this.scrollable.remove(source);
                    SPContent.this.scrollable.contentUpdated();
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
                SimulationPanel.this.scrollable.contentUpdated();
            }

            @Override
            public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
            }
        }
    }
}
