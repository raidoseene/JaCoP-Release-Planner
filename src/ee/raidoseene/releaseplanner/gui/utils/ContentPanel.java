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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Raido Seene
 */
public final class ContentPanel extends JPanel {

    public static final int TYPE_CLOSABLE = 1;
    public static final int TYPE_EXPANDABLE = 2;
    public static final int TYPE_CLICKABLE = 4;
    public static final int TYPE_TOGGLEABLE = 12;
    private static final String[] EXPAND_BUTTON = new String[]{"▼", "▲"};
    private static final String CLOSE_BUTTON = "×";
    private final BevelBorder[] borders;
    private final JButton close, expand;
    private final Component content;
    private boolean expanded;
    private boolean selected;

    public ContentPanel(Component content, int type) {
        this.content = content;
        this.expanded = false;
        this.selected = false;

        if ((type & TYPE_CLICKABLE) != 0) {
            this.borders = new BevelBorder[2];
            this.borders[1] = new BevelBorder(BevelBorder.LOWERED);
            this.addMouseListener(new CPMouseListener((type & TYPE_TOGGLEABLE) == TYPE_TOGGLEABLE));
        } else {
            this.borders = new BevelBorder[1];
        }
        this.setBorder(this.borders[0] = new BevelBorder(BevelBorder.RAISED));
        this.setLayout(new ContentPanel.CPLayout());

        if ((type & TYPE_CLOSABLE) != 0) {
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
        } else {
            this.close = null;
        }

        if ((type & TYPE_EXPANDABLE) != 0) {
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
        if (this.close != null) {
            this.close.setEnabled(enable);
        }
        if (this.expand != null) {
            this.expand.setEnabled(enable);
        }
    }
    
    public void setSelected(boolean select) {
        if (select && this.borders.length > 0) {
            this.setBorder(this.borders[1]);
            this.selected = true;
        } else {
            this.setBorder(this.borders[0]);
            this.selected = false;
        }
    }
    
    public boolean isSelected() {
        return this.selected;
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
                ((ContentPanelListener) listeners[i + 1]).contentPanelExpansionChanged(this, this.expanded);
            }
        }
    }
    
    private void notifySelectionEvent() {
        Object[] listeners = listenerList.getListenerList();
        boolean selected = (this.getBorder() != this.borders[0]);
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ContentPanelListener.class) {
                ((ContentPanelListener) listeners[i + 1]).contentPanelSelectionChanged(this, selected);
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
            Insets is = ContentPanel.this.getInsets();
            int height = 0;
            int width = 0;
            
            if (ContentPanel.this.close != null) {
                Dimension pref = ContentPanel.this.close.getPreferredSize();
                height = pref.height + 12;
                width = pref.width + 12;
            }
            
            if (ContentPanel.this.expand != null) {
                Dimension pref = ContentPanel.this.expand.getPreferredSize();
                width = (width > 0) ? (pref.width + 4) : (pref.width + 12);
                height = Math.max(height, pref.height + 12);
            }

            Dimension pref = ContentPanel.this.content.getPreferredSize();
            height = Math.max(height, pref.height) + is.top + is.bottom;
            width = Math.max(width, pref.width) + is.left + is.right;

            return new Dimension(width, height);
        }

        @Override
        public Dimension minimumLayoutSize(Container cntnr) {
            Insets is = ContentPanel.this.getInsets();
            int height = 0;
            int width = 0;
            
            if (ContentPanel.this.close != null) {
                Dimension pref = ContentPanel.this.close.getMinimumSize();
                height = pref.height + 12;
                width = pref.width + 12;
            }
            
            if (ContentPanel.this.expand != null) {
                Dimension pref = ContentPanel.this.expand.getMinimumSize();
                width = (width > 0) ? (pref.width + 4) : (pref.width + 12);
                height = Math.max(height, pref.height + 12);
            }

            Dimension pref = ContentPanel.this.content.getMinimumSize();
            height = Math.max(height, pref.height) + is.top + is.bottom;
            width = Math.max(width, pref.width) + is.left + is.right;

            return new Dimension(width, height);
        }

        @Override
        public void layoutContainer(Container cntnr) {
            Insets is = ContentPanel.this.getInsets();
            int width = ContentPanel.this.getWidth();
            int height = ContentPanel.this.getHeight();
            int cw = 6;

            if (ContentPanel.this.close != null) {
                Dimension pref = ContentPanel.this.close.getPreferredSize();
                ContentPanel.this.close.setBounds(width - is.right - cw - pref.width, is.top + 6, pref.width, pref.height);
                cw = pref.width + 10;
            }

            if (ContentPanel.this.expand != null) {
                Dimension pref = ContentPanel.this.expand.getPreferredSize();
                ContentPanel.this.expand.setBounds(width - is.right - cw - pref.width, is.top + 6, pref.width, pref.height);
            }

            ContentPanel.this.content.setBounds(is.left, is.top, width - is.right - is.left, height - is.bottom - is.top);
        }

    }
    
    private final class CPMouseListener implements MouseListener {
        
        private final boolean toggleable;
        private boolean tmp;
        
        private CPMouseListener(boolean toggleable) {
            this.toggleable = toggleable;
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {
            this.tmp = ContentPanel.this.selected;
            
            if (!this.tmp) {
                ContentPanel.this.setBorder(ContentPanel.this.borders[1]);
                ContentPanel.this.selected = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            boolean next = this.toggleable ? !this.tmp : false;
            
            if (ContentPanel.this.selected  != next) {
                ContentPanel.this.setBorder(ContentPanel.this.borders[0]);
                ContentPanel.this.selected = next;
            }
            
            ContentPanel.this.notifySelectionEvent();
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }
        
    }

}
