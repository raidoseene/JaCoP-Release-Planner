/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import ee.raidoseene.releaseplanner.datamodel.Project;

/**
 *
 * @author risto
 */
public final class ProjectManager {

    private static Project currentProject = null;

    public static void createNewProject() {
        if (ProjectManager.currentProject != null) {
            // TODO: ...
        }

        ProjectManager.currentProject = new Project();
    }

    public static Project getCurrentProject() {
        return ProjectManager.currentProject;
    }

}
