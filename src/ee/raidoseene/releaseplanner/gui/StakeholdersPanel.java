/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Raido Seene
 */
public final class StakeholdersPanel extends JPanel {

    public static final String TITLE_STRING = "Stakeholders";
    private final ScrollablePanel scrollable;
    private final JButton addButton;

    public StakeholdersPanel() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new ScrollablePanel();
        this.add(new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

        Project project = ProjectManager.getCurrentProject();
        if (project != null) {
            Stakeholders stakeholders = project.getStakeholders();
            int count = stakeholders.getStakeholderCount();

            for (int i = 0; i < count; i++) {
                Stakeholder s = stakeholders.getStakeholder(i);
                StakeholdersPanel.SPContent content = new StakeholdersPanel.SPContent(s);
                ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);
                panel.addContentPanelListener(content);
                this.scrollable.add(panel);
            }
        }

        this.addButton = new JButton("Add new stakeholder");
        this.addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    StakeholdersPanel.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.scrollable.add(this.addButton);
    }

    private void processAddEvent() {
        Stakeholder s = ProjectManager.getCurrentProject().getStakeholders().addStakeholder();
        StakeholdersPanel.SPContent content = new StakeholdersPanel.SPContent(s);
        ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);

        this.scrollable.add(panel, this.scrollable.getComponentCount() - 1);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private final class SPContent extends JPanel implements ContentPanelListener {

        private final Stakeholder stakeholder;
        private final JTextField name;
        private final JSpinner importance;

        public SPContent(Stakeholder s) {
            this.setBorder(new EmptyBorder(10, 10, 10, 80));
            this.setLayout(new BorderLayout(25, 25));

            this.stakeholder = s;
            this.name = new JTextField(s.getName());
            this.name.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent fe) {
                }

                @Override
                public void focusLost(FocusEvent fe) {
                    try {
                        SPContent.this.stakeholder.setName(SPContent.this.name.getText());
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    SPContent.this.name.setText(SPContent.this.stakeholder.getName());
                }
            });
            this.add(BorderLayout.CENTER, this.name);

            Container c = new Container();
            c.setLayout(new BorderLayout(5, 5));
            this.add(BorderLayout.LINE_END, c);

            this.importance = new JSpinner(new SpinnerNumberModel(s.getImportance(), 1, 9, 1));
            this.importance.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    try {
                        Object o = SPContent.this.importance.getValue();
                        SPContent.this.stakeholder.setImportance((Integer) o);
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    SPContent.this.importance.setValue(SPContent.this.stakeholder.getImportance());
                }
            });
            c.add(BorderLayout.CENTER, this.importance);
            c.add(BorderLayout.LINE_END, new JLabel("Importance"));
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            try {
                Stakeholders stakeholders = ProjectManager.getCurrentProject().getStakeholders();
                stakeholders.removeStakeholder(this.stakeholder);

                StakeholdersPanel.this.scrollable.remove(source);
                StakeholdersPanel.this.scrollable.contentUpdated();
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }

        @Override
        public void contentPanelExpansionChanged(ContentPanel source, boolean expanded) {
        }

        @Override
        public void contentPanelSelectionChanged(ContentPanel source, boolean selected) {
        }
        
    }
}
