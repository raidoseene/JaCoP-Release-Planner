/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanel;
import ee.raidoseene.releaseplanner.gui.utils.ContentPanelListener;
import ee.raidoseene.releaseplanner.gui.utils.ExtendableLayout;
import ee.raidoseene.releaseplanner.gui.utils.ScrollablePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author risto
 */
public final class FeaturesPanel extends JPanel {

    public static final String TITLE_STRING = "Features";
    private final FeaturesPanel.FPScrollable scrollable;

    public FeaturesPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new FeaturesPanel.FPScrollable();
        this.add(BorderLayout.CENTER, new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        Container c = new Container();
        this.add(BorderLayout.PAGE_END, c);
        c.setLayout(new LayoutManager() {

            @Override
            public void addLayoutComponent(String string, Component cmpnt) {
            }

            @Override
            public void removeLayoutComponent(Component cmpnt) {
            }

            @Override
            public Dimension preferredLayoutSize(Container cntnr) {
                int width = 250;
                int height = 1;

                for (Component c : cntnr.getComponents()) {
                    Dimension d = c.getPreferredSize();
                    width += (10 + d.width);

                    if (d.height > height) {
                        height = d.height;
                    }
                }

                return new Dimension(width, height);
            }

            @Override
            public Dimension minimumLayoutSize(Container cntnr) {
                int width = 250;
                int height = 1;

                for (Component c : cntnr.getComponents()) {
                    Dimension d = c.getMinimumSize();
                    width += (10 + d.width);

                    if (d.height > height) {
                        height = d.height;
                    }
                }

                return new Dimension(width, height);
            }

            @Override
            public void layoutContainer(Container cntnr) {
                int height = cntnr.getHeight();
                int x = 250;

                for (Component c : cntnr.getComponents()) {
                    int w = c.getPreferredSize().width;
                    c.setBounds(x + 10, 0, w, height);
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
        FeaturesPanel.FPContent content = new FeaturesPanel.FPContent();
        ContentPanel panel = new ContentPanel(content, true);

        this.scrollable.add(panel);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private void processManageEvent() {
        GroupManagerDialog.showGroupManagerDialog();
    }

    private final class FPScrollable extends ScrollablePanel {

        private FPScrollable() {
            this.setBorder(new EmptyBorder(10, 260, 10, 10));
            this.setLayout(new ContentListLayout());
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
        }

    }

    private final class FPContent extends JPanel implements ContentPanelListener {

        private final JPanel cont1;
        private final JTextField name;
        private final JPanel cont2;

        private FPContent() {
            this.setLayout(new ExtendableLayout(ExtendableLayout.VERTICAL, 10));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));

            this.cont1 = new JPanel();
            this.cont1.setBorder(new EmptyBorder(0, 0, 0, 110));
            this.cont1.setLayout(new GridLayout(1, 1));
            this.add(this.cont1);

            this.name = new JTextField();
            this.cont1.add(this.name);

            this.cont2 = new JPanel();
            this.cont2.setLayout(new BorderLayout(10, 10));

            JPanel p = new JPanel();
            p.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Resource consumption"));
            p.setLayout(new BorderLayout(10, 10));
            this.cont2.add(BorderLayout.LINE_START, p);

            JPanel p1 = new JPanel();
            p1.setBorder(new EmptyBorder(5, 5, 0, 5));
            p1.setLayout(new GridLayout(3, 2, 10, 2));
            p.add(BorderLayout.CENTER, p1);
            for (int i = 0; i < 3; i++) {
                p1.add(new JTextField());
                p1.add(new JComboBox());
            }
            
            JPanel p2 = new JPanel();
            p2.setBorder(new EmptyBorder(0, 5, 5, 5));
            p2.setLayout(new BorderLayout(10, 10));
            p.add(BorderLayout.PAGE_END, p2);
            
            p2.add(BorderLayout.LINE_START, new JButton("Add resource"));
            p2.add(BorderLayout.LINE_END, new JButton("Change in cost"));
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            FeaturesPanel.this.scrollable.remove(source);
            FeaturesPanel.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelExpanded(ContentPanel source) {
            if (this.getComponentCount() == 1) {
                this.add(this.cont2);

                FeaturesPanel.this.scrollable.contentUpdated();
            }
        }

        @Override
        public void contentPanelCompressed(ContentPanel source) {
            if (this.getComponentCount() > 1) {
                this.remove(this.cont2);

                FeaturesPanel.this.scrollable.contentUpdated();
            }
        }

    }
}
