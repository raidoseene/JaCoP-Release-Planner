package ee.raidoseene.releaseplanner.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 *
 * @author risto
 */
public final class MainFrame extends JFrame {
    
    private MainFrame() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = new Dimension((int) (screen.width * 0.75f), (int) (screen.height * 0.8f));
        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
        this.setMinimumSize(new Dimension(800, 600));
        this.setSize(size);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Release Planner");
        
        this.initContent();
    }
    
    private void initContent() {
        Container c = this.getContentPane();
        c.setLayout(new GridLayout(1, 1));
        
        JTabbedPane tb = new JTabbedPane();
        c.add(tb);
        
        FeaturesPanel featp = new FeaturesPanel();
        tb.add(FeaturesPanel.TITLE_STRING, featp);
        
        ResourcesPanel resp = new ResourcesPanel();
        tb.add(ResourcesPanel.TITLE_STRING, resp);
        
        ReleasesPanel relp = new ReleasesPanel();
        tb.add(ReleasesPanel.TITLE_STRING, relp);
        
        StakeholdersPanel stakp = new StakeholdersPanel();
        tb.add(StakeholdersPanel.TITLE_STRING, stakp);
        
        UrgValPanel urgvp = new UrgValPanel();
        tb.add(UrgValPanel.TITLE_STRING, urgvp);
        
        SettingsPanel setp = new SettingsPanel();
        tb.add(SettingsPanel.TITLE_STRING, setp);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Messenger.showWarning(ex, null);
        }
        
        try {
            MainFrame win = new MainFrame();
            win.setVisible(true);
        } catch (Exception ex) {
            Messenger.showError(ex, "Failed to lauch application!");
        }
    }
    
}
