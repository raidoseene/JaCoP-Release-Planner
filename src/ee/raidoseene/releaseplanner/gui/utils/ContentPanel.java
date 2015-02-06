/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 *
 * @author risto
 */
public final class ContentPanel extends JPanel {

    private static final String CLOSE_BUTTON = "×";
    private static final String[] EXPAND_BUTTON = new String[]{"▼", "▲"};
    private final boolean expandable;
    private final JButton close, expand;
    private final Component content;
    private boolean expanded;

    public ContentPanel(Component content, boolean expandable) {
        this.expandable = expandable;
        this.content = content;
        this.expanded = false;

        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setLayout(new ContentPanel.CPLayout());

        this.close = new JButton(CLOSE_BUTTON);
        this.close.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    ContentPanel.this.processCloseEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.add(this.close);

        if (this.expandable) {
            this.expand = new JButton(EXPAND_BUTTON[0]);
            this.expand.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        ContentPanel.this.processExpandEvent();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
            this.add(this.expand);
        } else {
            this.expand = null;
        }

        this.add(this.content);
    }
    
    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        this.content.setEnabled(enable);
        this.close.setEnabled(enable);
        if (this.expand != null) {
            this.expand.setEnabled(enable);
        }
    }
    
    public Component getContent() {
        return this.content;
    }

    public void addContentPanelListener(ContentPanelListener l) {
        this.listenerList.add(ContentPanelListener.class, l);
    }

    public void removeContentPanelListener(ContentPanelListener l) {
        this.listenerList.remove(ContentPanelListener.class, l);
    }

    private void processCloseEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ContentPanelListener.class) {
                ((ContentPanelListener) listeners[i + 1]).contentPanelClosed(this);
            }
        }
    }

    private void processExpandEvent() {
        this.expanded = !this.expanded;
        if (this.expanded) {
            this.expand.setText(EXPAND_BUTTON[1]);
        } else {
            this.expand.setText(EXPAND_BUTTON[0]);
        }

        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ContentPanelListener.class) {
                if (this.expanded) {
                    ((ContentPanelListener) listeners[i + 1]).contentPanelExpanded(this);
                } else {
                    ((ContentPanelListener) listeners[i + 1]).contentPanelCompressed(this);
                }
            }
        }
    }

    private final class CPLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String string, Component cmpnt) {
        }

        @Override
        public void removeLayoutComponent(Component cmpnt) {
        }

        @Override
        public Dimension preferredLayoutSize(Container cntnr) {
            Dimension pref = ContentPanel.this.close.getPreferredSize();
            Insets is = ContentPanel.this.getInsets();
            int height = pref.height + 12;
            int width = pref.width + 12;

            if (ContentPanel.this.expand != null) {
                pref = ContentPanel.this.expand.getPreferredSize();
                width += (pref.width + 4);
            }

            pref = ContentPanel.this.content.getPreferredSize();
            height = Math.max(height, pref.height) + is.top + is.bottom;
            width = Math.max(width, pref.width) + is.left + is.right;

            return new Dimension(width, height);
        }

        @Override
        public Dimension minimumLayoutSize(Container cntnr) {
            Dimension pref = ContentPanel.this.close.getMinimumSize();
            Insets is = ContentPanel.this.getInsets();
            int height = pref.height + 12;
            int width = pref.width + 12;

            if (ContentPanel.this.expand != null) {
                pref = ContentPanel.this.expand.getMinimumSize();
                width += (pref.width + 4);
            }

            pref = ContentPanel.this.content.getMinimumSize();
            height = Math.max(height, pref.height) + is.top + is.bottom;
            width = Math.max(width, pref.width) + is.left + is.right;

            return new Dimension(width, height);
        }

        @Override
        public void layoutContainer(Container cntnr) {
            Insets is = ContentPanel.this.getInsets();
            int width = ContentPanel.this.getWidth();
            int height = ContentPanel.this.getHeight();

            Dimension pref = ContentPanel.this.close.getPreferredSize();
            ContentPanel.this.close.setBounds(width - is.right - 6 - pref.width, is.top + 6, pref.width, pref.height);
            int cw = pref.width;

            if (ContentPanel.this.expand != null) {
                pref = ContentPanel.this.expand.getPreferredSize();
                ContentPanel.this.expand.setBounds(width - is.right - 10 - cw - pref.width, is.top + 6, pref.width, pref.height);
            }

            ContentPanel.this.content.setBounds(is.left, is.top, width - is.right - is.left, height - is.bottom - is.top);
        }

    }
    
}
