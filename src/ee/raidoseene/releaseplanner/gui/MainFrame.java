package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectFileFilter;
import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author risto
 */
public final class MainFrame extends JFrame {

    private final JMenuItem save, saveas, close;

    private MainFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension((int) (screen.width * 0.75f), (int) (screen.height * 0.8f));
        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
        this.setMinimumSize(new Dimension(800, 600));
        this.setSize(size);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Release Planner");

        this.setContentPane(new JPanel());

        // MenuBar
        JMenuBar menubar = new JMenuBar();
        this.setJMenuBar(menubar);
        JMenuItem item;
        JMenu menu;

        // File menu
        menu = new JMenu("File");
        menubar.add(menu);

        item = new JMenuItem("New Empty Project");
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

        item = new JMenuItem("New Default Project");
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

        item = new JMenuItem("Open Project...");
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

        menu.addSeparator();

        this.save = new JMenuItem("Save Project");
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

        this.saveas = new JMenuItem("Save Project As...");
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

        menu.addSeparator();

        this.close = new JMenuItem("Close Project");
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
    }

    private void createNewProject(boolean def) {
        try {
            String title = def ? "New Default Project" : "New Empty Project";
            String name = JOptionPane.showInputDialog(this, "Project name:", title, JOptionPane.QUESTION_MESSAGE);
            
            if (name != null) {
                ProjectManager.createNewProject(name);
                this.setContentPane(new TabbedView());
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        
        this.updateEnablity();
    }

    private void openSavedProject() {
        try {
            FileDialog fd = new FileDialog(this, "Load Project", FileDialog.LOAD);
            fd.setFilenameFilter(new ProjectFileFilter());
            fd.setVisible(true);
            
            String dir = fd.getDirectory();
            String fil = fd.getFile();
            
            if (dir != null && fil != null) {
                File file = new File(dir, fil);
                ProjectManager.loadSavedProject(file);
                this.setContentPane(new TabbedView());
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        
        this.updateEnablity();
    }

    private void saveCurrentProject(boolean as) {
        try {
            if (!as) { // Overwrite
                try {
                    ProjectManager.saveCurrentProject(null);
                    return;
                } catch (NullPointerException nex) {
                }
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
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
    }

    private void closeCurrentProject() {
        try {
            ProjectManager.closeCurrentProject();
            this.setContentPane(new JPanel());
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        
        this.updateEnablity();
    }

    private void updateEnablity() {
        boolean has = (ProjectManager.getCurrentProject() != null);
        
        this.save.setEnabled(has);
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
