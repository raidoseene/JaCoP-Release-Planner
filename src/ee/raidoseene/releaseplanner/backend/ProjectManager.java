/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import ee.raidoseene.releaseplanner.datamodel.Project;
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
 * @author Raido Seene
 */
public final class ProjectManager {

    private static final long FILE_HEADER = 0x4a52505f70726f6aL; // JRP_proj
    private static Project currentProject = null;

    public static void createNewProject(String name) throws Exception {
        if (ProjectManager.currentProject != null) {
            ProjectManager.closeCurrentProject(false);
        }

        ProjectManager.currentProject = new Project(name);
        ProjectManager.currentProject.setStorage(null);
        ProjectManager.currentProject.modify();
    }

    public static void loadSavedProject(File file) throws Exception {
        if (ProjectManager.currentProject != null) {
            ProjectManager.closeCurrentProject(false);
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
                ProjectManager.currentProject.setStorage(file.getPath());
            }
        }
    }

    public static void saveCurrentProject(File file) throws Exception {        
        if (file == null) {
            Project proj = ProjectManager.currentProject;
            if (proj == null || proj.getStorage() == null) {
                throw new NullPointerException("No destination file provided!");
            }
            file = new File(proj.getStorage());
        }

        try (OutputStream out = new FileOutputStream(file)) {
            for (int i = 56; i >= 0; i -= 8) {
                out.write((int) (ProjectManager.FILE_HEADER >> i) & 0xff);
            }
            
            try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
                ProjectManager.currentProject.setStorage(file.getPath());
                oout.writeObject(ProjectManager.currentProject);
                ProjectManager.currentProject.resetModification();
            }
        }
    }

    public static void closeCurrentProject(boolean force) throws Exception {
        if (!force && ProjectManager.currentProject != null) {
            if(ProjectManager.currentProject.isModified()) {
                throw new UnsavedException("Current project is not saved!");
            }
        }

        ProjectManager.currentProject = null;
    }

    public static Project getCurrentProject() {
        return ProjectManager.currentProject;
    }

}
