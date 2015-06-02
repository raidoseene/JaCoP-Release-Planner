/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import ee.raidoseene.releaseplanner.datamodel.Resource;
import ee.raidoseene.releaseplanner.datamodel.Resources;
import ee.raidoseene.releaseplanner.datamodel.Stakeholder;
import ee.raidoseene.releaseplanner.datamodel.Stakeholders;
import java.util.Random;

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
        generateReleases(project, settings);
        generateStakeholders(project, settings);
        
        return project;
    }
    
    private static void generateResources(Resources resources, AutotestSettings settings) {
        int resNo = settings.getResourceNo();
        for(int r = 0; r < resNo; r++) {
            Resource resource = resources.addResource();
            resource.setName("Res" + numberGenerator(r, resNo));
        }
    }
    
    private static void generateFeatures(Project project, AutotestSettings settings) {
        int resNo = settings.getResourceNo();
        int featNo = settings.getFeatureNo();
        int minCons = settings.getMinConsumption();
        int maxCons = settings.getMaxConsumption();
        Features features = project.getFeatures();
        Resources resources = project.getResources();
        Random random = new Random();
        
        for(int f = 0; f < featNo; f++) {
            Feature feature = features.addFeature();
            feature.setName("F" + numberGenerator(f, featNo));
            
            for(int r = 0; r < resNo; r++) {
                if((Math.random() > 0.4)) {
                    int consumption = random.nextInt(maxCons - minCons + 1) + minCons;
                    feature.setConsumption(resources.getResource(r), consumption);
                    settings.addResConsumption(r, consumption);
                }
            }
            int randResId = random.nextInt(resNo);
            Resource randRes = resources.getResource(randResId);
            if(!feature.hasConsumption(randRes)) {
                int consumption = random.nextInt(maxCons - minCons) + minCons;
                feature.setConsumption(randRes, consumption);
                settings.addResConsumption(randResId, consumption);
            }
        }
    }
    
    private static void generateReleases(Project project, AutotestSettings settings) {
        int relNo = settings.getReleaseNo();
        int resNo = settings.getResourceNo();
        float tightness = settings.getTightness();
        Releases releases = project.getReleases();
        Resources resources = project.getResources();
        Random random = new Random();
        
        for(int r = 0; r < relNo; r++) {
            Release release = releases.addRelease();
            release.setName("Rel" + numberGenerator(r, relNo));
            release.setImportance(random.nextInt(9) + 1);
            
            for(int res = 0; res < resNo; res++) {
                int totalResConsumption = settings.getTotalResConsumption(res);
                int capacity = (int)((float)totalResConsumption / (tightness * (float)relNo));
                release.setCapacity(resources.getResource(res), capacity);
            }
        }
    }
    
    private static void generateStakeholders(Project project, AutotestSettings settings) {
        int stkNo = settings.getStakeholderNo();
        Stakeholders stakeholders = project.getStakeholders();
        Random random = new Random();
        
        for(int s = 0; s < stkNo; s++) {
            Stakeholder stakeholder = stakeholders.addStakeholder();
            stakeholder.setName("Stk" + numberGenerator(s, stkNo));
            stakeholder.setImportance(random.nextInt(9) + 1);
        }
    }
    
    private static String numberGenerator(int n, int amount) {
        String nString = Integer.toString(n + 1);
        int nLen = nString.length();
        int amountLen = Integer.toString(amount).length();
        int diff = amountLen - nLen;
        
        char[] zeros = new char[diff];
        for (int i = 0; i < diff; i++) {
            zeros[i] = '0';
        }
        
        return (new String(zeros) + nString);
    }
}
