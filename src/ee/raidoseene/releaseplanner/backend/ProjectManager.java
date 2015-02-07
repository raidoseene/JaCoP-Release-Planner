/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import ee.raidoseene.releaseplanner.datamodel.Project;
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

    private static final long HEADER = 0;
    private static Project currentProject = null;
    private static File currentLocation = null;

    public static void createNewProject(String name) throws Exception {
        if (ProjectManager.currentProject != null) {
            // TODO: ...
        }

        ProjectManager.currentProject = new Project(name);
        ProjectManager.currentLocation = null;
    }

    public static void loadSavedProject(File file) throws Exception {
        try (InputStream in = new FileInputStream(file)) {
            try (ObjectInputStream oin = new ObjectInputStream(in)) {
                if (oin.readLong() != ProjectManager.HEADER) {
                    // TODO: error
                }
                
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
            try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
                oout.writeLong(ProjectManager.HEADER);
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

}
