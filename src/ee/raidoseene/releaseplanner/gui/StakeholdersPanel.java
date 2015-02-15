/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author risto
 */
public final class StakeholdersPanel extends ScrollablePanel {
    
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
        StakeholdersPanel.SPContent content = new StakeholdersPanel.SPContent();
        ContentPanel panel = new ContentPanel(content, false);
        
        this.scrollable.add(panel, this.scrollable.getComponentCount() - 1);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();
        
        panel.addContentPanelListener(content);
    }

    private final class SPContent extends JPanel implements ContentPanelListener {
        
        private final JTextField name;
        private final JSpinner importance;
        
        public SPContent() {
            this.setBorder(new EmptyBorder(10, 10, 10, 80));
            this.setLayout(new BorderLayout(25, 25));
            
            this.name = new JTextField();
            this.add(BorderLayout.CENTER, this.name);
            
            Container c = new Container();
            c.setLayout(new BorderLayout(5, 5));
            this.add(BorderLayout.LINE_END, c);
            
            this.importance = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
            c.add(BorderLayout.CENTER, this.importance);
            c.add(BorderLayout.LINE_END, new JLabel("Importance"));
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            StakeholdersPanel.this.scrollable.remove(source);
            StakeholdersPanel.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelExpanded(ContentPanel source) {
        }

        @Override
        public void contentPanelCompressed(ContentPanel source) {
        }
    }
}
