/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.autotests.AutotestSettings;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 *
 * @author risto
 */
public final class AutotestDialog extends JDialog {

    private final AutotestSettings settings;
    private final Settings projectSettings;
    private final JTextField numTProjs;
    private final JTextField[] numFs, numRC, numRss, numRls, numSs, resTness, fixNo, exNo, earNo, latNo, SPNo, HPNo, coupNo, sepNo, andNo, xorNo;
    private final JButton test;
    private boolean state = false;

    private AutotestDialog(AutotestSettings settings) {
        projectSettings = SettingsManager.getCurrentSettings();
        
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


        CaretListener listener = new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                AutotestDialog.this.checkEnability();
            }
        };


        Container c = new Container();
        c.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        cont.add(BorderLayout.PAGE_START, c);

        Container grid = new Container();
        grid.setLayout(new GridLayout(4, 2, 10, 10));
        c.add(grid);

        final JTextField solverTimeLimit = new JTextField();
        solverTimeLimit.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                if(isInt(solverTimeLimit.getText())) {
                    projectSettings.setSolverTimeLimit(Integer.parseInt(solverTimeLimit.getText()));
                } else {
                    solverTimeLimit.setText("");
                    projectSettings.setSolverTimeLimit(null);
                }
            }
        });
        
        final JCheckBox resourceShifting = new JCheckBox("Carry over unused resources");
        final JCheckBox normalizedImportances = new JCheckBox("Use normalized importances");
        
        grid.add(solverTimeLimit);
        final JCheckBox limitSolverTime = new JCheckBox("Limit solver time (seconds)");
        if(!projectSettings.getLimitSolverTime()) {
            solverTimeLimit.setEditable(false);
        } else {
            if(projectSettings.getSolverTimeLimit() != null) {
                solverTimeLimit.setText(projectSettings.getSolverTimeLimit().toString());
            }
        }
        
        limitSolverTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (limitSolverTime.isSelected()) {
                        projectSettings.setLimitSolverTime(true);
                        solverTimeLimit.setEditable(true);
                        if(projectSettings.getSolverTimeLimit() != null) {
                            solverTimeLimit.setText(projectSettings.getSolverTimeLimit().toString());
                        }
                    } else {
                        projectSettings.setLimitSolverTime(false);
                        solverTimeLimit.setEditable(false);
                        solverTimeLimit.setText("");
                        projectSettings.setSolverTimeLimit(null);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        grid.add(limitSolverTime);
        grid.add(new JLabel(""));
        if (projectSettings.getResourceShifting()) {
            resourceShifting.setSelected(true);
        }
        if (!projectSettings.getCodeOutput()) {
            resourceShifting.setEnabled(false);
        }
        resourceShifting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (resourceShifting.isSelected()) {
                        projectSettings.setResourceShifting(true);
                    } else {
                        projectSettings.setResourceShifting(false);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        grid.add(resourceShifting);
        grid.add(new JLabel(""));
        if (projectSettings.getNormalizedImportances()) {
            normalizedImportances.setSelected(true);
        }
        
        normalizedImportances.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (normalizedImportances.isSelected()) {
                        projectSettings.setNormalizedImportances(true);
                    } else {
                        projectSettings.setNormalizedImportances(false);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        grid.add(normalizedImportances);
        
        grid.add(new JLabel("Number of test projects"));
        this.numTProjs = new JTextField();
        this.numTProjs.addCaretListener(listener);
        grid.add(this.numTProjs);

        c = new Container();
        Container wrap = new Container();
        wrap.setLayout(new BorderLayout());
        c.setLayout(new BorderLayout(15, 15));
        wrap.add(BorderLayout.PAGE_START, c);
        cont.add(BorderLayout.CENTER, wrap);

        grid = new Container();
        grid.setLayout(new GridLayout(16, 1, 10, 2));
        grid.add(new JLabel("Number of features"));
        grid.add(new JLabel("Resource consumption"));
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

        grid = new Container();
        grid.setLayout(new GridLayout(16, 2, 10, 2));
        c.add(BorderLayout.CENTER, grid);

        this.numFs = new JTextField[2];
        this.numFs[0] = new JTextField();
        this.numFs[0].addCaretListener(listener);
        this.numFs[1] = new JTextField();
        grid.add(this.numFs[0]);
        grid.add(this.numFs[1]);

        this.numRC = new JTextField[2];
        this.numRC[0] = new JTextField();
        this.numRC[0].addCaretListener(listener);
        this.numRC[1] = new JTextField();
        grid.add(this.numRC[0]);
        grid.add(this.numRC[1]);

        this.numRss = new JTextField[2];
        this.numRss[0] = new JTextField();
        this.numRss[0].addCaretListener(listener);
        this.numRss[1] = new JTextField();
        grid.add(this.numRss[0]);
        grid.add(this.numRss[1]);

        this.numRls = new JTextField[2];
        this.numRls[0] = new JTextField();
        this.numRls[0].addCaretListener(listener);
        this.numRls[1] = new JTextField();
        grid.add(this.numRls[0]);
        grid.add(this.numRls[1]);

        this.numSs = new JTextField[2];
        this.numSs[0] = new JTextField();
        this.numSs[0].addCaretListener(listener);
        this.numSs[1] = new JTextField();
        grid.add(this.numSs[0]);
        grid.add(this.numSs[1]);

        this.resTness = new JTextField[2];
        this.resTness[0] = new JTextField();
        this.resTness[0].addCaretListener(listener);
        this.resTness[1] = new JTextField();
        grid.add(this.resTness[0]);
        grid.add(this.resTness[1]);

        this.fixNo = new JTextField[2];
        this.fixNo[0] = new JTextField();
        this.fixNo[0].addCaretListener(listener);
        this.fixNo[1] = new JTextField();
        grid.add(this.fixNo[0]);
        grid.add(this.fixNo[1]);

        this.exNo = new JTextField[2];
        this.exNo[0] = new JTextField();
        this.exNo[0].addCaretListener(listener);
        this.exNo[1] = new JTextField();
        grid.add(this.exNo[0]);
        grid.add(this.exNo[1]);

        this.earNo = new JTextField[2];
        this.earNo[0] = new JTextField();
        this.earNo[0].addCaretListener(listener);
        this.earNo[1] = new JTextField();
        grid.add(this.earNo[0]);
        grid.add(this.earNo[1]);

        this.latNo = new JTextField[2];
        this.latNo[0] = new JTextField();
        this.latNo[0].addCaretListener(listener);
        this.latNo[1] = new JTextField();
        grid.add(this.latNo[0]);
        grid.add(this.latNo[1]);

        this.SPNo = new JTextField[2];
        this.SPNo[0] = new JTextField();
        this.SPNo[0].addCaretListener(listener);
        this.SPNo[1] = new JTextField();
        grid.add(this.SPNo[0]);
        grid.add(this.SPNo[1]);

        this.HPNo = new JTextField[2];
        this.HPNo[0] = new JTextField();
        this.HPNo[0].addCaretListener(listener);
        this.HPNo[1] = new JTextField();
        grid.add(this.HPNo[0]);
        grid.add(this.HPNo[1]);

        this.coupNo = new JTextField[2];
        this.coupNo[0] = new JTextField();
        this.coupNo[0].addCaretListener(listener);
        this.coupNo[1] = new JTextField();
        grid.add(this.coupNo[0]);
        grid.add(this.coupNo[1]);

        this.sepNo = new JTextField[2];
        this.sepNo[0] = new JTextField();
        this.sepNo[0].addCaretListener(listener);
        this.sepNo[1] = new JTextField();
        grid.add(this.sepNo[0]);
        grid.add(this.sepNo[1]);

        this.andNo = new JTextField[2];
        this.andNo[0] = new JTextField();
        this.andNo[0].addCaretListener(listener);
        this.andNo[1] = new JTextField();
        grid.add(this.andNo[0]);
        grid.add(this.andNo[1]);

        this.xorNo = new JTextField[2];
        this.xorNo[0] = new JTextField();
        this.xorNo[0].addCaretListener(listener);
        this.xorNo[1] = new JTextField();
        grid.add(this.xorNo[0]);
        grid.add(this.xorNo[1]);
        
        c = new Container();
        c.setLayout(new FlowLayout(FlowLayout.CENTER));
        cont.add(BorderLayout.PAGE_END, c);

        this.test = new JButton("Test");
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
        boolean more;
        try {
            int num = Integer.parseInt(this.numTProjs.getText());
            more = (num > 1);
        } catch (Exception ex) {
            more = false;
        }

        if (more) {
            this.numFs[1].setEnabled(isInt(this.numFs[0].getText()));
            this.numRss[1].setEnabled(isInt(this.numRss[0].getText()));
            this.numRls[1].setEnabled(isInt(this.numRls[0].getText()));
            this.numSs[1].setEnabled(isInt(this.numSs[0].getText()));
            this.resTness[1].setEnabled(isFloat(this.resTness[0].getText()));
            this.fixNo[1].setEnabled(isInt(this.fixNo[0].getText()));
            this.exNo[1].setEnabled(isInt(this.exNo[0].getText()));
            this.earNo[1].setEnabled(isInt(this.earNo[0].getText()));
            this.latNo[1].setEnabled(isInt(this.latNo[0].getText()));
            this.SPNo[1].setEnabled(isInt(this.SPNo[0].getText()));
            this.HPNo[1].setEnabled(isInt(this.HPNo[0].getText()));
            this.coupNo[1].setEnabled(isInt(this.coupNo[0].getText()));
            this.sepNo[1].setEnabled(isInt(this.sepNo[0].getText()));
            this.andNo[1].setEnabled(isInt(this.andNo[0].getText()));
            this.xorNo[1].setEnabled(isInt(this.xorNo[0].getText()));
        } else {
            this.numFs[1].setEnabled(false);
            this.numRss[1].setEnabled(false);
            this.numRls[1].setEnabled(false);
            this.numSs[1].setEnabled(false);
            this.resTness[1].setEnabled(false);
            this.fixNo[1].setEnabled(false);
            this.exNo[1].setEnabled(false);
            this.earNo[1].setEnabled(false);
            this.latNo[1].setEnabled(false);
            this.SPNo[1].setEnabled(false);
            this.HPNo[1].setEnabled(false);
            this.coupNo[1].setEnabled(false);
            this.sepNo[1].setEnabled(false);
            this.andNo[1].setEnabled(false);
            this.xorNo[1].setEnabled(false);
        }

        this.numRC[1].setEnabled(isInt(this.numRC[0].getText()));
        this.test.setEnabled(isInt(this.numFs[0].getText())
                && isInt(this.numRC[0].getText())
                && isInt(this.numRss[0].getText())
                && isInt(this.numRls[0].getText())
                && isInt(this.numSs[0].getText())
                && isFloat(this.resTness[0].getText()));
    }

    private void fillSettings() {
        if (isInt(this.numTProjs.getText())) {
            this.settings.setProjectNo(Integer.parseInt(this.numTProjs.getText()));
        }

        if (isInt(this.numFs[0].getText())) {
            Integer max = isInt(this.numFs[1].getText()) ? Integer.parseInt(this.numFs[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.FEATURES, Integer.parseInt(this.numFs[0].getText()), max);
        }

        if (isInt(this.numRC[0].getText())) {
            Integer max = isInt(this.numRC[1].getText()) ? Integer.parseInt(this.numRC[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.RESOURCE_CONS, Integer.parseInt(this.numRC[0].getText()), max);
        }

        if (isInt(this.numRss[0].getText())) {
            Integer max = isInt(this.numRss[1].getText()) ? Integer.parseInt(this.numRss[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.RESOURCES, Integer.parseInt(this.numRss[0].getText()), max);
        }
        if (isInt(this.numRls[0].getText())) {
            Integer max = isInt(this.numRls[1].getText()) ? Integer.parseInt(this.numRls[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.RELEASES, Integer.parseInt(this.numRls[0].getText()), max);
        }
        if (isInt(this.numSs[0].getText())) {
            Integer max = isInt(this.numSs[1].getText()) ? Integer.parseInt(this.numSs[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.STAKEHOLDERS, Integer.parseInt(this.numSs[0].getText()), max);
        }
        if (isFloat(this.resTness[0].getText())) {
            Float max = isFloat(this.resTness[1].getText()) ? Float.parseFloat(this.resTness[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.TIGHTNESS, Float.parseFloat(this.resTness[0].getText()), max);
        }
        if (isInt(this.fixNo[0].getText())) {
            Integer max = isInt(this.fixNo[1].getText()) ? Integer.parseInt(this.fixNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.FIXED_DEP, Integer.parseInt(this.fixNo[0].getText()), max);
        }
        if (isInt(this.exNo[0].getText())) {
            Integer max = isInt(this.exNo[1].getText()) ? Integer.parseInt(this.exNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.EXCLUDED_DEP, Integer.parseInt(this.exNo[0].getText()), max);
        }
        if (isInt(this.earNo[0].getText())) {
            Integer max = isInt(this.earNo[1].getText()) ? Integer.parseInt(this.earNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.EARLIER_DEP, Integer.parseInt(this.earNo[0].getText()), max);
        }
        if (isInt(this.latNo[0].getText())) {
            Integer max = isInt(this.latNo[1].getText()) ? Integer.parseInt(this.latNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.LATER_DEP, Integer.parseInt(this.latNo[0].getText()), max);
        }
        if (isInt(this.SPNo[0].getText())) {
            Integer max = isInt(this.SPNo[1].getText()) ? Integer.parseInt(this.SPNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.SOFT_PRECEDENCE_DEP, Integer.parseInt(this.SPNo[0].getText()), max);
        }
        if (isInt(this.HPNo[0].getText())) {
            Integer max = isInt(this.HPNo[1].getText()) ? Integer.parseInt(this.HPNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.HARD_PRECEDENCE_DEP, Integer.parseInt(this.HPNo[0].getText()), max);
        }
        if (isInt(this.coupNo[0].getText())) {
            Integer max = isInt(this.coupNo[1].getText()) ? Integer.parseInt(this.coupNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.COUPLING_DEP, Integer.parseInt(this.coupNo[0].getText()), max);
        }
        if (isInt(this.sepNo[0].getText())) {
            Integer max = isInt(this.sepNo[1].getText()) ? Integer.parseInt(this.sepNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.SEPARATION_DEP, Integer.parseInt(this.sepNo[0].getText()), max);
        }
        if (isInt(this.andNo[0].getText())) {
            Integer max = isInt(this.andNo[1].getText()) ? Integer.parseInt(this.andNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.AND_DEP, Integer.parseInt(this.andNo[0].getText()), max);
        }
        if (isInt(this.xorNo[0].getText())) {
            Integer max = isInt(this.xorNo[1].getText()) ? Integer.parseInt(this.xorNo[1].getText()) : null;
            this.settings.setParameter(AutotestSettings.Parameter.XOR_DEP, Integer.parseInt(this.xorNo[0].getText()), max);
        }
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public static boolean showAutotestDialog(AutotestSettings settings) {
        AutotestDialog dialog = new AutotestDialog(settings);
        dialog.setVisible(true);
        return dialog.state;
    }
}
