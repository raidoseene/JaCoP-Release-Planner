/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Resource;
import ee.raidoseene.releaseplanner.datamodel.Resources;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Raido Seene
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
        this.scrollable.setLayout(new ContentListLayout(ContentPanel.class));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));

        Project project = ProjectManager.getCurrentProject();
        if (project != null) {
            Resources resources = project.getResources();
            int count = resources.getResourceCount();

            for (int i = 0; i < count; i++) {
                Resource r = resources.getResource(i);
                ResourcesPanel.RPContent content = new ResourcesPanel.RPContent(r);
                ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);
                panel.addContentPanelListener(content);
                this.scrollable.add(panel);
            }
        }

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
        Resource r = ProjectManager.getCurrentProject().getResources().addResource();
        ResourcesPanel.RPContent content = new ResourcesPanel.RPContent(r);
        ContentPanel panel = new ContentPanel(content, ContentPanel.TYPE_CLOSABLE);

        this.scrollable.add(panel, this.scrollable.getComponentCount() - 1);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private final class RPContent extends JPanel implements ContentPanelListener {

        private final Resource resource;
        private final JTextField name;

        private RPContent(Resource r) {
            this.setBorder(new EmptyBorder(10, 10, 10, 80));
            this.setLayout(new GridLayout(1, 1));

            this.resource = r;
            this.name = new JTextField(r.getName());
            this.name.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent fe) {
                }

                @Override
                public void focusLost(FocusEvent fe) {
                    try {
                        RPContent.this.resource.setName(RPContent.this.name.getText());
                    } catch (Exception ex) {
                        Messenger.showError(ex, null);
                    }
                    RPContent.this.name.setText(RPContent.this.resource.getName());
                }
            });
            this.add(this.name);
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            try {
                Resources resources = ProjectManager.getCurrentProject().getResources();
                resources.removeResource(this.resource);

                ResourcesPanel.this.scrollable.remove(source);
                ResourcesPanel.this.scrollable.contentUpdated();
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }

        @Override
        public void contentPanelExpanded(ContentPanel source) {
        }

        @Override
        public void contentPanelCompressed(ContentPanel source) {
        }

    }
}
