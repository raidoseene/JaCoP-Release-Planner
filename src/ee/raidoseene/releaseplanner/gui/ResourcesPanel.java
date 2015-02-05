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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author risto
 */
public final class ResourcesPanel extends JPanel {
    
    public static final String TITLE_STRING = "Resources";
    private final ScrollablePanel scrollable;
    private final JButton addButton;
    
    public ResourcesPanel() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new ScrollablePanel();
        this.add(new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable.setLayout(new ContentListLayout());
        
        this.addButton = new JButton("Add new resource");
        this.addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    ResourcesPanel.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.scrollable.add(this.addButton);
    }
    
    private void processAddEvent() {
        ResourcesPanel.RPContent content = new ResourcesPanel.RPContent();
        ContentPanel panel = new ContentPanel(content, false);
        
        this.scrollable.add(panel, this.scrollable.getComponentCount() - 1);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();
        
        panel.addContentPanelListener(content);
    }

    private final class RPContent extends JPanel implements ContentPanelListener {
        
        private final JTextField name;
        
        private RPContent() {
            this.setBorder(new EmptyBorder(10, 10, 10, 80));
            this.setLayout(new GridLayout(1, 1));
            
            this.name = new JTextField();
            this.add(this.name);
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            ResourcesPanel.this.scrollable.remove(source);
            ResourcesPanel.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelExpanded(ContentPanel source) {
        }

        @Override
        public void contentPanelCompressed(ContentPanel source) {
        }
    }
}
