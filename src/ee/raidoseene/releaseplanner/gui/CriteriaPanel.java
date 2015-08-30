/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Criterium;
import ee.raidoseene.releaseplanner.datamodel.Criteria;
import ee.raidoseene.releaseplanner.datamodel.Project;
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
public class CriteriaPanel extends JPanel {

    public static final String TITLE_STRING = "Criteria";
    private final ScrollablePanel scrollable;
    private final JButton addButton;

    public CriteriaPanel() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new ScrollablePanel();
        this.add(new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

        Project project = ProjectManager.getCurrentProject();
        if (project != null) {
            Criteria criteria = project.getCriteria();
            int count = criteria.getCriteriumCount();

            for (int i = 0; i < count; i++) {
                Criterium c = criteria.getCriterium(i);
                CriteriaPanel.CPContent content = new CriteriaPanel.CPContent(c);
                ContentPanel panel = new ContentPanel(content, (!c.isPermanent() ? ContentPanel.TYPE_CLOSABLE : 0));
                panel.addContentPanelListener(content);
                this.scrollable.add(panel);
            }
        }

        this.addButton = new JButton("Add new criterium");
        this.addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    CriteriaPanel.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.scrollable.add(this.addButton);
    }

    private void processAddEvent() {
        Criterium c = ProjectManager.getCurrentProject().getCriteria().addCriterium();
        CriteriaPanel.CPContent content = new CriteriaPanel.CPContent(c);
        ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);

        this.scrollable.add(panel, this.scrollable.getComponentCount() - 1);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private final class CPContent extends JPanel implements ContentPanelListener {

        private final Criterium criteria;
        private final JTextField name;
        private final JSpinner importance;

        public CPContent(Criterium c) {
            this.setBorder(new EmptyBorder(10, 10, 10, 80));
            this.setLayout(new BorderLayout(25, 25));

            this.criteria = c;
            this.name = new JTextField(c.getName());
            if(c.isPermanent()) {
                this.name.setEditable(false);
            }
            this.name.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent fe) {
                }

                @Override
                public void focusLost(FocusEvent fe) {
                    try {
                        CriteriaPanel.CPContent.this.criteria.setName(CriteriaPanel.CPContent.this.name.getText());
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    CriteriaPanel.CPContent.this.name.setText(CriteriaPanel.CPContent.this.criteria.getName());
                }
            });
            this.add(BorderLayout.CENTER, this.name);

            Container con = new Container();
            con.setLayout(new BorderLayout(5, 5));
            this.add(BorderLayout.LINE_END, con);

            this.importance = new JSpinner(new SpinnerNumberModel(c.getWeight(), (!c.isPermanent() ? 0 : 1), 9, 1));
            this.importance.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent ce) {
                    try {
                        Object o = CriteriaPanel.CPContent.this.importance.getValue();
                        CriteriaPanel.CPContent.this.criteria.setWeight((Integer) o);
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    CriteriaPanel.CPContent.this.importance.setValue(CriteriaPanel.CPContent.this.criteria.getWeight());
                }
            });
            con.add(BorderLayout.CENTER, this.importance);
            con.add(BorderLayout.LINE_END, new JLabel("Importance"));
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            try {
                Criteria criterias = ProjectManager.getCurrentProject().getCriteria();
                criterias.removeCriterium(this.criteria);

                CriteriaPanel.this.scrollable.remove(source);
                CriteriaPanel.this.scrollable.contentUpdated();
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
