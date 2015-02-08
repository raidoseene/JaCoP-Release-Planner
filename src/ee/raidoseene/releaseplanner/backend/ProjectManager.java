/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.gui.Messenger;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 * @author risto
 */
public final class ProjectManager {

    private static final long FILE_HEADER = 0x4a52505f70726f6aL; // JRP_proj
    private static Project currentProject = null;
    private static File currentLocation = null;

    public static void createNewProject(String name) throws Exception {
        if (ProjectManager.currentProject != null) {
            ProjectManager.closeCurrentProject();
        }

        ProjectManager.currentProject = new Project(name);
        ProjectManager.currentLocation = null;
    }
    
    public static void loadDefaultProject(String name) throws Exception {
        if (ProjectManager.currentProject != null) {
            ProjectManager.closeCurrentProject();
        }
        
        File file = ResourceManager.getResourceFile("default.proj");
        if (file.exists()) {
            ProjectManager.loadSavedProject(file);
            ProjectManager.currentLocation = null;
            if (ProjectManager.currentProject != null) {
                ProjectManager.currentProject.setName(name);
            }
        } else {
            Messenger.showWarning(null, "Default project not set!\nCreating empty project.");
            ProjectManager.createNewProject(name);
        }
    }

    public static void loadSavedProject(File file) throws Exception {
        if (ProjectManager.currentProject != null) {
            ProjectManager.closeCurrentProject();
        }
        
        try (InputStream in = new FileInputStream(file)) {
            for (int i = 56; i >= 0; i -= 8) {
                int value = in.read();
                if (value < 0) {
                    throw new EOFException("EOF reached prematurely!");
                } else if (value != ((int) (ProjectManager.FILE_HEADER >> i) & 0xff)) {
                    throw new Exception("Unrecognized file format!");
                }
            }
            
            try (ObjectInputStream oin = new ObjectInputStream(in)) {
                ProjectManager.currentProject = (Project) oin.readObject();
                ProjectManager.currentLocation = file;
            }
        }
    }

    public static void saveCurrentProject(File file) throws Exception {
        if (file == null) {
            if (ProjectManager.currentLocation == null) {
                throw new NullPointerException("No destination file provided!");
            }
            file = ProjectManager.currentLocation;
        }

        try (OutputStream out = new FileOutputStream(file)) {
            for (int i = 56; i >= 0; i -= 8) {
                out.write((int) (ProjectManager.FILE_HEADER >> i) & 0xff);
            }
            
            try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
                oout.writeObject(ProjectManager.currentProject);
                ProjectManager.currentLocation = file;
            }
        }
    }

    public static void closeCurrentProject() throws Exception {
        if (ProjectManager.currentProject != null) {
            // TODO: ...
        }

        ProjectManager.currentProject = null;
        ProjectManager.currentLocation = null;
    }

    public static Project getCurrentProject() {
        return ProjectManager.currentProject;
    }
    
    public static boolean hasStorage() {
        return (ProjectManager.currentLocation != null);
    }

}
