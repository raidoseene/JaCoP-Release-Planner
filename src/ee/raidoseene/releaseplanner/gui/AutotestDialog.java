/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.autotests.AutotestSettings;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author risto
 */
public final class AutotestDialog extends JDialog {
    
    private final AutotestSettings settings;
    private final JTextField numTProjs, minRC, maxRC, fixNo, exNo, earNo, latNo, SPNo, HPNo, coupNo, sepNo, andNo, xorNo;
    private final JTextField[] numFs, numRss, numRls, numSs, resTness;
    private final JRadioButton numFInterv, numRsInterv, numRlInterv, numSInterv, resTInterv;
    private final JCheckBox overInterv;
    private boolean state = false;
    
    private AutotestDialog(AutotestSettings settings) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension(500, 600);

        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
        this.setSize(size);

        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setTitle("Test Project");
        this.setModal(true);

        JPanel cont = new JPanel(new BorderLayout(25, 25));
        cont.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(cont);
        this.settings = settings;
        
        
        Container c = new Container();
        c.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        c.add(new JLabel("Number of test projects"));
        cont.add(BorderLayout.PAGE_START, c);
        
        Container grid = new Container();
        grid.setLayout(new GridLayout(1, 2, 10, 10));
        c.add(grid);
        
        this.numTProjs = new JTextField();
        grid.add(this.numTProjs);
        
        this.overInterv = new JCheckBox("Over Interval", settings.getProjectInterval());
        this.overInterv.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    AutotestDialog.this.settings.setProjectInterval(AutotestDialog.this.overInterv.isSelected());
                    AutotestDialog.this.checkEnability();
                } catch (Exception ex) {
                    Messenger.showError(ex, null);
                }
            }
        });
        grid.add(this.overInterv);
        
        
        c = new Container();
        Container wrap = new Container();
        wrap.setLayout(new BorderLayout());
        c.setLayout(new BorderLayout(10, 10));
        wrap.add(BorderLayout.PAGE_START, c);
        cont.add(BorderLayout.CENTER, wrap);
        
        grid = new Container();
        grid.setLayout(new GridLayout(17, 1, 2, 2));
        grid.add(new JLabel("Number of features"));
        grid.add(new JLabel("  Minimum resource consumption"));
        grid.add(new JLabel("  Maximum resource consumption"));
        grid.add(new JLabel("Number of resources"));
        grid.add(new JLabel("Number of releases"));
        grid.add(new JLabel("Number of stakeholders"));
        grid.add(new JLabel("Resource tightness"));
        grid.add(new JLabel("FIXED dependency"));
        grid.add(new JLabel("EXCLUDED dependency"));
        grid.add(new JLabel("EARLIER dependency"));
        grid.add(new JLabel("LATER dependency"));
        grid.add(new JLabel("SOFT PRECEDENCE dependency"));
        grid.add(new JLabel("HARD PRECEDENCE dependency"));
        grid.add(new JLabel("COUPLING dependency"));
        grid.add(new JLabel("SEPARATION dependency"));
        grid.add(new JLabel("AND dependency"));
        grid.add(new JLabel("XOR dependency"));
        c.add(BorderLayout.LINE_START, grid);
        
        Container cols = new Container();
        cols.setLayout(new BorderLayout(10, 10));
        c.add(BorderLayout.CENTER, cols);
        
        grid = new Container();
        grid.setLayout(new GridLayout(17, 2, 5, 2));
        cols.add(BorderLayout.CENTER, grid);
        
        this.numFs = new JTextField[2];
        this.numFs[0] = new JTextField();
        this.numFs[1] = new JTextField();
        grid.add(this.numFs[0]);
        grid.add(this.numFs[1]);
        
        this.minRC = new JTextField();
        this.minRC.setText(Integer.toString(this.settings.getMinConsumption()));
        grid.add(this.minRC);
        grid.add(new JLabel());
        
        this.maxRC = new JTextField();
        grid.add(this.maxRC);
        grid.add(new JLabel());
        
        this.numRss = new JTextField[2];
        this.numRss[0] = new JTextField();
        this.numRss[1] = new JTextField();
        grid.add(this.numRss[0]);
        grid.add(this.numRss[1]);
        
        this.numRls = new JTextField[2];
        this.numRls[0] = new JTextField();
        this.numRls[1] = new JTextField();
        grid.add(this.numRls[0]);
        grid.add(this.numRls[1]);
        
        this.numSs = new JTextField[2];
        this.numSs[0] = new JTextField();
        this.numSs[1] = new JTextField();
        grid.add(this.numSs[0]);
        grid.add(this.numSs[1]);
        
        this.resTness = new JTextField[2];
        this.resTness[0] = new JTextField();
        this.resTness[1] = new JTextField();
        grid.add(this.resTness[0]);
        grid.add(this.resTness[1]);
        
        this.fixNo = new JTextField();
        grid.add(this.fixNo);
        grid.add(new JLabel());
        
        this.exNo = new JTextField();
        grid.add(this.exNo);
        grid.add(new JLabel());
        
        this.earNo = new JTextField();
        grid.add(this.earNo);
        grid.add(new JLabel());
        
        this.latNo = new JTextField();
        grid.add(this.latNo);
        grid.add(new JLabel());
        
        this.SPNo = new JTextField();
        grid.add(this.SPNo);
        grid.add(new JLabel());
        
        this.HPNo = new JTextField();
        grid.add(this.HPNo);
        grid.add(new JLabel());
        
        this.coupNo = new JTextField();
        grid.add(this.coupNo);
        grid.add(new JLabel());
        
        this.sepNo = new JTextField();
        grid.add(this.sepNo);
        grid.add(new JLabel());
        
        this.andNo = new JTextField();
        grid.add(this.andNo);
        grid.add(new JLabel());
        
        this.xorNo = new JTextField();
        grid.add(this.xorNo);
        grid.add(new JLabel());
        
        grid = new Container();
        grid.setLayout(new GridLayout(17, 1, 2, 2));
        cols.add(BorderLayout.LINE_END, grid);
        ButtonGroup bg = new ButtonGroup();
        
        ActionListener alistener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    AutotestDialog.this.updateIntervals();
                    AutotestDialog.this.checkEnability();
                } catch (Exception ex) {
                    Messenger.showError(ex, null);
                }
            }
        };
        
        this.numFInterv = new JRadioButton("Interval", settings.getFeatureInterval());
        this.numFInterv.addActionListener(alistener);
        grid.add(this.numFInterv);
        bg.add(this.numFInterv);
        
        grid.add(new JLabel());
        grid.add(new JLabel());
        
        this.numRsInterv = new JRadioButton("Interval", settings.getResourceInterval());
        this.numRsInterv.addActionListener(alistener);
        grid.add(this.numRsInterv);
        bg.add(this.numRsInterv);
        
        this.numRlInterv = new JRadioButton("Interval", settings.getReleaseInterval());
        this.numRlInterv.addActionListener(alistener);
        grid.add(this.numRlInterv);
        bg.add(this.numRlInterv);
        
        this.numSInterv = new JRadioButton("Interval", settings.getStakeholderInterval());
        this.numSInterv.addActionListener(alistener);
        grid.add(this.numSInterv);
        bg.add(this.numSInterv);
        
        this.resTInterv = new JRadioButton("Interval", settings.getTightnessInterval());
        this.resTInterv.addActionListener(alistener);
        grid.add(this.resTInterv);
        bg.add(this.resTInterv);
        
        
        c = new Container();
        c.setLayout(new FlowLayout(FlowLayout.CENTER));
        cont.add(BorderLayout.PAGE_END, c);
        
        JButton test = new JButton("Test");
        test.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    AutotestDialog.this.fillSettings();
                    AutotestDialog.this.state = true;
                    AutotestDialog.this.dispose();
                } catch (Throwable t) {
                    Messenger.showError(t, null);
                }
            }
        });
        c.add(test);
        
        this.checkEnability();
    }
    
    private void checkEnability() {
        boolean proji = this.settings.getProjectInterval();
        this.numTProjs.setEnabled(!proji);
        this.numFInterv.setEnabled(proji);
        this.numRsInterv.setEnabled(proji);
        this.numRlInterv.setEnabled(proji);
        this.numSInterv.setEnabled(proji);
        this.resTInterv.setEnabled(proji);
        
        this.numFs[1].setEnabled(proji && this.settings.getFeatureInterval());
        this.numRss[1].setEnabled(proji && this.settings.getResourceInterval());
        this.numRls[1].setEnabled(proji && this.settings.getReleaseInterval());
        this.numSs[1].setEnabled(proji && this.settings.getStakeholderInterval());
        this.resTness[1].setEnabled(proji && this.settings.getTightnessInterval());
    }
    
    private void updateIntervals() {
        this.settings.setFeatureInterval(this.numFInterv.isSelected());
        this.settings.setResourceInterval(this.numRsInterv.isSelected());
        this.settings.setReleaseInterval(this.numRlInterv.isSelected());
        this.settings.setStakeholderInterval(this.numSInterv.isSelected());
        this.settings.setTightnessInterval(this.resTInterv.isSelected());
    }
    
    private void fillSettings() {
        if (!this.settings.getProjectInterval()) {
            this.settings.setProjectNo(Integer.parseInt(this.numTProjs.getText()));
        }
        
        this.settings.setFeatureNo(Integer.parseInt(this.numFs[0].getText()));
        if (this.settings.getProjectInterval() && this.settings.getFeatureInterval()) {
            this.settings.setFeatureTo(Integer.parseInt(this.numFs[1].getText()));
        }
        
        this.settings.setMinConsumption(Integer.parseInt(this.minRC.getText()));
        this.settings.setMaxConsumption(Integer.parseInt(this.maxRC.getText()));
        
        this.settings.setResourceNo(Integer.parseInt(this.numRss[0].getText()));
        if (this.settings.getProjectInterval() && this.settings.getResourceInterval()) {
            this.settings.setResourceTo(Integer.parseInt(this.numRss[1].getText()));
        }
        
        this.settings.setReleaseNo(Integer.parseInt(this.numRls[0].getText()));
        if (this.settings.getProjectInterval() && this.settings.getReleaseInterval()) {
            this.settings.setReleaseTo(Integer.parseInt(this.numRls[1].getText()));
        }
        
        this.settings.setStakeholderNo(Integer.parseInt(this.numSs[0].getText()));
        if (this.settings.getProjectInterval() && this.settings.getStakeholderInterval()) {
            this.settings.setStakeholderTo(Integer.parseInt(this.numSs[1].getText()));
        }
        
        this.settings.setTightness(Float.parseFloat(this.resTness[0].getText()));
        if (this.settings.getProjectInterval() && this.settings.getTightnessInterval()) {
            this.settings.setTightnessTo(Float.parseFloat(this.resTness[1].getText()));
        }
        
        this.settings.setFixedNo(Integer.parseInt(this.fixNo.getText()));
        this.settings.setExcludedNo(Integer.parseInt(this.exNo.getText()));
        this.settings.setEarlierNo(Integer.parseInt(this.earNo.getText()));
        this.settings.setLaterNo(Integer.parseInt(this.latNo.getText()));
        this.settings.setSoftPrecedenceNo(Integer.parseInt(this.SPNo.getText()));
        this.settings.setHardPrecedenceNo(Integer.parseInt(this.HPNo.getText()));
        this.settings.setCouplingNo(Integer.parseInt(this.coupNo.getText()));
        this.settings.setSeparationNo(Integer.parseInt(this.sepNo.getText()));
        this.settings.setAndNo(Integer.parseInt(this.andNo.getText()));
        this.settings.setXorNo(Integer.parseInt(this.xorNo.getText()));
    }
    
    public static boolean showAutotestDialog(AutotestSettings settings) {
        AutotestDialog dialog = new AutotestDialog(settings);
        dialog.setVisible(true);
        return dialog.state;
    }
    
}
