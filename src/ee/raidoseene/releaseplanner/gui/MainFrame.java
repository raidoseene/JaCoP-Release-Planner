package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.autotests.AutotestManager;
import ee.raidoseene.releaseplanner.autotests.AutotestSettings;
import ee.raidoseene.releaseplanner.backend.ProjectFileFilter;
import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.backend.UnsavedException;
import ee.raidoseene.releaseplanner.dataimport.ExportManager;
import ee.raidoseene.releaseplanner.dataimport.ImportManager;
import ee.raidoseene.releaseplanner.dataoutput.DataManager;
import ee.raidoseene.releaseplanner.solverutils.Solver;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 *
 * @author Raido Seene
 */
public final class MainFrame extends JFrame {

    private final JMenuItem save, saveas, close;

    private MainFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension((int) (screen.width * 0.75f), (int) (screen.height * 0.8f));
        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
        this.setMinimumSize(new Dimension(800, 600));
        this.setSize(size);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                if(MainFrame.this.closeCurrentProject()) {
                    MainFrame.this.dispose();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
            
        });
        this.setTitle("Release Planner");

        this.setContentPane(new JPanel());

        // MenuBar
        JMenuBar menubar = new JMenuBar();
        this.setJMenuBar(menubar);
        JMenuItem item;
        JMenu menu;

        // File menu
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                try {
                    MainFrame.this.updateEnablity();
                } catch (Exception ex) {
                    Messenger.showWarning(ex, "Oh snap!");
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        menubar.add(menu);

        item = new JMenuItem("New Empty Project", KeyEvent.VK_E);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        item.setDisplayedMnemonicIndex(item.getText().indexOf('E'));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.createNewProject(false);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(item);

        item = new JMenuItem("New Default Project", KeyEvent.VK_D);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        item.setDisplayedMnemonicIndex(item.getText().indexOf('D'));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.createNewProject(true);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(item);

        item = new JMenuItem("Open Project...", KeyEvent.VK_O);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.openSavedProject();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(item);
        
        //Import menu
        JMenu menu2 = new JMenu("Import");
        menu2.setMnemonic(KeyEvent.VK_I);
        menu.add(menu2);

        item = new JMenuItem("Import Project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.importProject();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu2.add(item);

        menu.addSeparator();

        this.save = new JMenuItem("Save Project", KeyEvent.VK_S);
        this.save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        this.save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.saveCurrentProject(false);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(this.save);

        this.saveas = new JMenuItem("Save Project As...", KeyEvent.VK_A);
        this.saveas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        this.saveas.setDisplayedMnemonicIndex(this.saveas.getText().indexOf('A'));
        this.saveas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.saveCurrentProject(true);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(this.saveas);
        
        //Export menu
        JMenu menu3 = new JMenu("Export");
        menu3.setMnemonic(KeyEvent.VK_X);
        menu.add(menu3);

        item = new JMenuItem("Export Project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.exportProject();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu3.add(item);

        menu.addSeparator();

        this.close = new JMenuItem("Close Project", KeyEvent.VK_C);
        this.close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        this.close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.closeCurrentProject();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(this.close);

        //Autotests menu
        menu = new JMenu("Autotests");
        menubar.add(menu);

        item = new JMenuItem("Generate Projects");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.generateProjects();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(item);
        
        item = new JMenuItem("Generate & Simulate Projects");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    MainFrame.this.generateAndSimulateProjects();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        menu.add(item);
    }

    private void createNewProject(boolean def) {
        if(!this.closeCurrentProject()) {
            return;
        }
        try {
            String title = def ? "New Default Project" : "New Empty Project";
            String name = JOptionPane.showInputDialog(this, "Project name:", title, JOptionPane.QUESTION_MESSAGE);

            if (name != null) {
                if (def) {
                    String fname = "default" + ProjectFileFilter.FILE_EXTENSION;
                    File file = new File(ResourceManager.getDirectory(), fname);
                    if (file.exists() && file.isFile()) {
                        ProjectManager.loadSavedProject(file);
                        ProjectManager.getCurrentProject().setStorage(null);
                        ProjectManager.getCurrentProject().setName(name);
                    } else {
                        Messenger.showWarning(null, "Default project not set!\nCreating empty project.");
                        ProjectManager.createNewProject(name);
                    }
                } else {
                    ProjectManager.createNewProject(name);
                }
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        } finally {
            if (ProjectManager.getCurrentProject() != null) {
                this.setContentPane(new TabbedView());
            } else {
                this.setContentPane(new JPanel());
            }
        }

        this.updateEnablity();
    }

    private void openSavedProject() {
        if(!this.closeCurrentProject()) {
            return;
        }
        try {
            FileDialog fd = new FileDialog(this, "Load Project", FileDialog.LOAD);
            fd.setFilenameFilter(new ProjectFileFilter());
            fd.setVisible(true);

            String dir = fd.getDirectory();
            String fil = fd.getFile();

            if (dir != null && fil != null) {
                File file = new File(dir, fil);
                ProjectManager.loadSavedProject(file);
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        } finally {
            if (ProjectManager.getCurrentProject() != null) {
                this.setContentPane(new TabbedView());
            } else {
                this.setContentPane(new JPanel());
            }
        }

        this.updateEnablity();
    }

    private boolean saveCurrentProject(boolean as) {
        try {
            if (!as && ProjectManager.getCurrentProject().getStorage() != null) { // Overwrite
                ProjectManager.saveCurrentProject(null);
                return true;
            }

            String name = ProjectManager.getCurrentProject().getName();
            FileDialog fd = new FileDialog(this, "Save Project", FileDialog.SAVE);
            fd.setFilenameFilter(new ProjectFileFilter());
            fd.setFile(name + ".proj");
            fd.setVisible(true);

            String dir = fd.getDirectory();
            String fil = fd.getFile();

            if (dir != null && fil != null) {
                File file = new File(dir, fil);
                ProjectManager.saveCurrentProject(file);
                return true;
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        return false;
    }

    private boolean closeCurrentProject() {
        try {
            ProjectManager.closeCurrentProject(false);
        } catch (UnsavedException uex) {
            String message = "There are unsaved changes in current project. Would you like to save the project?";
            int result = JOptionPane.showConfirmDialog(this, message, "Unsaved project", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                if(!this.saveCurrentProject(false)) {
                    return false;
                }
            } else if (result == JOptionPane.NO_OPTION) {
                try {
                    ProjectManager.closeCurrentProject(true);
                } catch (Exception ex) {
                }
            } else if (result == JOptionPane.CANCEL_OPTION) {
                this.updateEnablity();
                return false;
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        if (ProjectManager.getCurrentProject() != null) {
            this.setContentPane(new TabbedView());
        } else {
            this.setContentPane(new JPanel());
        }

        this.updateEnablity();
        return true;
    }

    private void importProject() {
        try {
            FileDialog fd = new FileDialog(this, "Import Project", FileDialog.LOAD);
            fd.setVisible(true);

            String dir = fd.getDirectory();
            String fil = fd.getFile();

            if (dir != null && fil != null) {
                File file = new File(dir, fil);
                ProjectManager.createNewProject("");
                ImportManager.importProject(ProjectManager.getCurrentProject(), file);
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }

        if (ProjectManager.getCurrentProject() != null) {
            this.setContentPane(new TabbedView());
        } else {
            this.setContentPane(new JPanel());
        }
        this.updateEnablity();
    }
    
    private void exportProject() {
        /*
        try {
            FileDialog fd = new FileDialog(this, "Import Project", FileDialog.LOAD);
            fd.setVisible(true);

            String dir = fd.getDirectory();
            String fil = fd.getFile();

            if (dir != null && fil != null) {
                File file = new File(dir, fil);
                ProjectManager.createNewProject("");
                ImportManager.importProject(ProjectManager.getCurrentProject(), file);
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        */

        try {
            ExportManager.dependencies(ProjectManager.getCurrentProject(), null);
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        
        this.updateEnablity();
    }
    
    private void generateProjects() {
        try {
            AutotestManager am = new AutotestManager();
            AutotestSettings settings = am.getSettings();
            if (AutotestDialog.showAutotestDialog(settings)) {
                //am.startTesting();
                am.generateProjects(false);
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }

        if (ProjectManager.getCurrentProject() != null) {
            this.setContentPane(new TabbedView());
        } else {
            this.setContentPane(new JPanel());
        }
        this.updateEnablity();
    }

    private void generateAndSimulateProjects() {
        try {
            AutotestManager am = new AutotestManager();
            AutotestSettings settings = am.getSettings();
            if (AutotestDialog.showAutotestDialog(settings)) {
                //am.startTesting();
                am.generateProjects(true);
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }

        if (ProjectManager.getCurrentProject() != null) {
            this.setContentPane(new TabbedView());
        } else {
            this.setContentPane(new JPanel());
        }
        this.updateEnablity();
    }

    private void updateEnablity() {
        boolean has = (ProjectManager.getCurrentProject() != null);
        this.save.setEnabled(has && ProjectManager.getCurrentProject().isModified());
        this.saveas.setEnabled(has);
        this.close.setEnabled(has);

        this.revalidate();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    Messenger.showWarning(ex, null);
                }

                try {
                    MainFrame win = new MainFrame();
                    win.setVisible(true);
                    win.updateEnablity();
                } catch (Exception ex) {
                    Messenger.showError(ex, "Failed to lauch application!");
                }
            }
        });
    }
}
