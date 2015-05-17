/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectFileFilter;
import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.gui.utils.ContentListLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Raido Seene
 */
public final class SettingsPanel extends JPanel {

    public static final String TITLE_STRING = "Settings";
    private final JLabel[] paths;

    public SettingsPanel() {
        this.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.setLayout(new ContentListLayout(Component.class));
        Container c, e;
        final Settings settings = SettingsManager.getCurrentSettings();

        Container proj = new Container();
        proj.setLayout(new BorderLayout(10, 10));
        proj.add(BorderLayout.PAGE_START, new JLabel("<html><b><u>Project Settings</u></b></html>"));
        this.add(proj);

        c = new Container();
        c.setLayout(new GridLayout(2, 2, 10, 10));
        proj.add(BorderLayout.LINE_START, c);

        c.add(new JTextField());
        c.add(new JLabel("Resource buffer"));

        c.add(new JComboBox());
        c.add(new JLabel("Solving criteria"));

        this.add(new JSeparator());

        Container def = new Container();
        def.setLayout(new BorderLayout(10, 10));
        def.add(BorderLayout.PAGE_START, new JLabel("<html><b><u>Default Settings</u></b></html>"));
        this.add(def);

        JButton btn = new JButton("Set current project as default");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    SettingsPanel.this.processDefaultProjectSet();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        def.add(BorderLayout.LINE_START, btn);

        this.add(new JSeparator());

        Container solv = new Container();
        solv.setLayout(new BorderLayout(10, 10));
        solv.add(BorderLayout.PAGE_START, new JLabel("<html><b><u>Solver Settings</u></b></html>"));
        this.add(solv);

        c = new Container();
        c.setLayout(new BorderLayout(10, 10));
        solv.add(BorderLayout.LINE_START, c);

        e = new Container();
        e.setLayout(new GridLayout(4, 1, 10, 10));
        c.add(BorderLayout.CENTER, e);

        this.paths = new JLabel[2];
        e.add(this.paths[0] = new JLabel());
        e.add(this.paths[1] = new JLabel());
        this.setPath(SettingsManager.getCurrentSettings().getMiniZincPath(), 0);
        this.setPath(SettingsManager.getCurrentSettings().getJaCoPPath(), 1);
        this.paths[0].setToolTipText("Path to MiniZinc");
        this.paths[1].setToolTipText("Path to JaCoP");

        e = new Container();
        e.setLayout(new GridLayout(4, 1, 10, 10));
        c.add(BorderLayout.LINE_END, e);

        btn = new JButton("Modify");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    SettingsPanel.this.processPathModification(0);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        e.add(btn);

        btn = new JButton("Modify");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    SettingsPanel.this.processPathModification(1);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        e.add(btn);

        final JCheckBox codeOutput = new JCheckBox("Create and use project specific solver code");
        final JCheckBox resourceShifting = new JCheckBox("Allow using unused resources in next releases");
        if (settings.getCodeOutput()) {
            codeOutput.setSelected(true);
        }
        codeOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (codeOutput.isSelected()) {
                        settings.setCodeOutput(true);
                        resourceShifting.setEnabled(true);
                    } else {
                        settings.setCodeOutput(false);
                        resourceShifting.setEnabled(false);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        e.add(codeOutput);

        if (settings.getResourceShifting()) {
            resourceShifting.setSelected(true);
        }
        if (!settings.getCodeOutput()) {
            resourceShifting.setEnabled(false);
        }
        resourceShifting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (resourceShifting.isSelected()) {
                        settings.setResourceShifting(true);
                    } else {
                        settings.setResourceShifting(false);
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        e.add(resourceShifting);
    }

    private void setPath(String path, int target) {
        if (path != null) {
            this.paths[target].setText(path);
            this.paths[target].setEnabled(true);
        } else {
            this.paths[target].setText("<not set>");
            this.paths[target].setEnabled(false);
        }
    }

    private void processPathModification(int target) {
        JFileChooser fc;

        if (this.paths[target].isEnabled()) {
            fc = new JFileChooser(this.paths[target].getText());
        } else {
            fc = new JFileChooser();
        }

        fc.setDialogTitle(this.paths[target].getToolTipText());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);

        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Directories only";
            }
        });

        if (fc.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
            try {
                Settings settings = SettingsManager.getCurrentSettings();
                String path = fc.getSelectedFile().getPath();

                if (target == 0) {
                    settings.setMiniZincPath(path);
                    path = settings.getMiniZincPath();
                } else if (target == 1) {
                    settings.setJaCoPPath(path);
                    path = settings.getJaCoPPath();
                }

                this.setPath(path, target);
                SettingsManager.saveCurrentSettings();
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }
    }

    private void processDefaultProjectSet() {
        Project project = ProjectManager.getCurrentProject();
        String storage = project.getStorage();

        try {
            String fname = "default" + ProjectFileFilter.FILE_EXTENSION;
            File file = new File(ResourceManager.getDirectory(), fname);
            ProjectManager.saveCurrentProject(file);
        } catch (Exception ex) {
            String msg = "Failed to set the current project as default!";
            Messenger.showError(ex, msg);
        }

        project.setStorage(storage);
    }
}
