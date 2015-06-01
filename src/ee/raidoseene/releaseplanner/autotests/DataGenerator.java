/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Resource;
import ee.raidoseene.releaseplanner.datamodel.Resources;

/**
 *
 * @author Raido Seene
 */
public class DataGenerator {
    
    public static Project generateProject(String name, AutotestSettings settings) throws Exception {
        ProjectManager.createNewProject(name);
        Project project = ProjectManager.getCurrentProject();
        
        generateResources(project.getResources(), settings);
        generateFeatures(project, settings);
        
        return project;
    }
    
    private static void generateResources(Resources resources, AutotestSettings settings) {
        int resNo = settings.getResourceNo();
        for(int i = 0; i < resNo; i++) {
            Resource r = resources.addResource();
            r.setName("Res" + numberGenerator(i, resNo));
        }
    }
    
    private static void generateFeatures(Project project, AutotestSettings settings) {
        
    }
    
    private static String numberGenerator(int i, int amount) {
        String output = "";
        String iString = Integer.toString(i);
        int iLen = iString.length();
        int amountLen = Integer.toString(amount).length();
        int diff = amountLen - iLen;
        
        for(int j = 0; j < diff; j++) {
            output.concat("0");
        }
        output.concat(iString);
        
        return output;
    }
}
