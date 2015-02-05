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
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author risto
 */
public final class ReleasesPanel extends ScrollablePanel {

    public static final String TITLE_STRING = "Releases";
    private final ScrollablePanel scrollable;
    private final JButton addButton;

    public ReleasesPanel() {
        this.setLayout(new GridLayout(1, 1));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable = new ScrollablePanel();
        this.add(new JScrollPane(this.scrollable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        this.scrollable.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.scrollable.setLayout(new ContentListLayout());

        this.addButton = new JButton("Add new release");
        this.addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    ReleasesPanel.this.processAddEvent();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.scrollable.add(this.addButton);
    }

    private void processAddEvent() {
        ReleasesPanel.RPContent content = new ReleasesPanel.RPContent();
        ContentPanel panel = new ContentPanel(content, true);

        this.scrollable.add(panel, this.scrollable.getComponentCount() - 1);
        this.scrollable.contentUpdated();
        this.scrollable.scrollDown();

        panel.addContentPanelListener(content);
    }

    private final class RPContent extends JPanel implements ContentPanelListener {

        private final JPanel cont1;
        private final JTextField name;
        private final JSpinner importance;
        private final JPanel cont2;
        private final JTextField architecture, analysis, development, testing;
        private final JRadioButton hours, days;

        public RPContent() {
            this.setLayout(new ExtendableLayout(ExtendableLayout.VERTICAL, 10));
            this.setBorder(new EmptyBorder(10, 10, 10, 10));

            this.cont1 = new JPanel();
            this.cont1.setBorder(new EmptyBorder(0, 0, 0, 110));
            this.cont1.setLayout(new BorderLayout(25, 25));
            this.add(this.cont1);

            this.name = new JTextField();
            this.cont1.add(BorderLayout.CENTER, this.name);

            Container c = new Container();
            c.setLayout(new BorderLayout(5, 5));
            this.cont1.add(BorderLayout.LINE_END, c);

            this.importance = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
            c.add(BorderLayout.CENTER, this.importance);
            c.add(BorderLayout.LINE_END, new JLabel("Importance"));

            this.cont2 = new JPanel();
            this.cont2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Resource capacity"));
            this.cont2.setLayout(new BorderLayout());

            JPanel p = new JPanel(new GridLayout(4, 2, 10, 2));
            p.setBorder(new EmptyBorder(5, 5, 5, 5));
            this.cont2.add(BorderLayout.LINE_START, p);

            this.architecture = new JTextField();
            p.add(this.architecture);
            p.add(new JLabel("Architecture (h)"));

            this.analysis = new JTextField();
            p.add(this.analysis);
            p.add(new JLabel("Analysis (h)"));

            this.development = new JTextField();
            p.add(this.development);
            p.add(new JLabel("Development (h)"));

            this.testing = new JTextField();
            p.add(this.testing);
            p.add(new JLabel("Testing (h)"));

            p = new JPanel(new BorderLayout());
            p.setBorder(new EmptyBorder(5, 5, 5, 5));
            this.cont2.add(BorderLayout.LINE_END, p);

            c = new Container();
            c.setLayout(new GridLayout(1, 2, 5, 5));
            p.add(BorderLayout.PAGE_START, c);

            Container c2 = new Container();
            c2.setLayout(new GridLayout(2, 1, 5, 5));
            c.add(c2);

            ButtonGroup bg = new ButtonGroup();
            this.hours = new JRadioButton("hours");
            this.days = new JRadioButton("days");
            c2.add(this.hours);
            bg.add(this.hours);
            c2.add(this.days);
            bg.add(this.days);

            c.add(new JLabel("Time format"));
        }

        @Override
        public void contentPanelClosed(ContentPanel source) {
            ReleasesPanel.this.scrollable.remove(source);
            ReleasesPanel.this.scrollable.contentUpdated();
        }

        @Override
        public void contentPanelExpanded(ContentPanel source) {
            if (this.getComponentCount() == 1) {
                this.add(this.cont2);

                ReleasesPanel.this.scrollable.contentUpdated();
            }
        }

        @Override
        public void contentPanelCompressed(ContentPanel source) {
            if (this.getComponentCount() > 1) {
                this.remove(this.cont2);

                ReleasesPanel.this.scrollable.contentUpdated();
            }
        }

    }
}
