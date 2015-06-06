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
import ee.raidoseene.releaseplanner.datamodel.Urgency;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;
import java.util.Random;

/**
 *
 * @author Raido Seene
 */
public class DataGenerator {

    private static int[] urgCurve = new int[]{0x10, 0x21, 0x22, 0x31, 0x32};
    private static int urgCurveLen = urgCurve.length;

    public static Project generateProject(String name, AutotestSettings settings, int iterator) throws Exception {
        ProjectManager.createNewProject(name);
        Project project = ProjectManager.getCurrentProject();

        generateResources(project.getResources(), settings, iterator);
        generateFeatures(project, settings, iterator);
        generateReleases(project, settings, iterator);
        generateStakeholders(project, settings, iterator);
        generateValueAndUrgency(project, settings);
        generateDependencies(project, settings, iterator);

        return project;
    }

    private static void generateResources(Resources resources, AutotestSettings settings, int iterator) {
        int resNo = settings.getResourceNo();

        if (settings.getResourceInterval()) {
            resNo += iterator;
        }

        for (int r = 0; r < resNo; r++) {
            Resource resource = resources.addResource();
            resource.setName("Res" + numberGenerator(r, resNo));
        }
    }

    private static void generateFeatures(Project project, AutotestSettings settings, int iterator) {
        int resNo = settings.getResourceNo();
        int featNo = settings.getFeatureNo();
        int minCons = settings.getMinConsumption();
        int maxCons = settings.getMaxConsumption();
        int midCons = (int) ((maxCons - minCons + 1) * 0.6);

        Resources resources = project.getResources();
        Features features = project.getFeatures();
        settings.initializeResConsumption();
        Random random = new Random();

        if (settings.getFeatureInterval()) {
            featNo += iterator;
        }

        for (int f = 0; f < featNo; f++) {
            Feature feature = features.addFeature();
            feature.setName("F" + numberGenerator(f, featNo));

            for (int r = 0; r < resNo; r++) {
                if (Math.random() > 0.4) {
                    int consumption = generateRandCons(random, minCons, midCons, maxCons);
                    /*
                    if (Math.random() > 0.3) {
                        consumption = random.nextInt(midCons - minCons + 1) + minCons;
                    } else {
                        consumption = random.nextInt(maxCons - midCons + 1) + minCons;
                    }
                    */
                    feature.setConsumption(resources.getResource(r), consumption);
                    settings.addResConsumption(r, consumption);
                }
            }
            int randResId = random.nextInt(resNo);
            Resource randRes = resources.getResource(randResId);
            if (!feature.hasConsumption(randRes)) {
                int consumption = generateRandCons(random, minCons, midCons, maxCons);
                //int consumption = random.nextInt(maxCons - minCons) + minCons;
                feature.setConsumption(randRes, consumption);
                settings.addResConsumption(randResId, consumption);
            }
        }
    }

    private static int generateRandCons(Random random, int minCons, int midCons, int maxCons) {
        int consumption;
        
        if (Math.random() > 0.4) {
            consumption = random.nextInt(midCons - minCons + 1) + minCons;
        } else {
            consumption = random.nextInt(maxCons - minCons + 1) + minCons;
        }

        return consumption;
    }

    private static void generateReleases(Project project, AutotestSettings settings, int iterator) {
        int relNo = settings.getReleaseNo();
        int resNo = settings.getResourceNo();
        float tightness = settings.getTightness();
        Releases releases = project.getReleases();
        Resources resources = project.getResources();
        Random random = new Random();

        if (settings.getReleaseInterval()) {
            relNo += iterator;
        }

        for (int r = 0; r < relNo; r++) {
            Release release = releases.addRelease();
            release.setName("Rel" + numberGenerator(r, relNo));
            release.setImportance(random.nextInt(9) + 1);

            for (int res = 0; res < resNo; res++) {
                int totalResConsumption = settings.getTotalResConsumption(res);
                int capacity = (int) ((float) totalResConsumption / (tightness * (float) relNo));
                release.setCapacity(resources.getResource(res), capacity);
            }
        }
    }

    private static void generateStakeholders(Project project, AutotestSettings settings, int iterator) {
        int stkNo = settings.getStakeholderNo();
        Stakeholders stakeholders = project.getStakeholders();
        Random random = new Random();

        if (settings.getStakeholderInterval()) {
            stkNo += iterator;
        }

        for (int s = 0; s < stkNo; s++) {
            Stakeholder stakeholder = stakeholders.addStakeholder();
            stakeholder.setName("Stk" + numberGenerator(s, stkNo));
            stakeholder.setImportance(random.nextInt(9) + 1);
        }
    }

    private static void generateValueAndUrgency(Project project, AutotestSettings settings) {
        int stkNo = settings.getStakeholderNo();
        int featNo = settings.getFeatureNo();
        int relNo = settings.getReleaseNo();
        Random random = new Random();

        for (int f = 0; f < featNo; f++) {
            int valCounter = 0;
            for (int s = 0; s < stkNo; s++) {
                if (Math.random() > 0.5) {
                    valCounter++;

                    randValAndUrg(project, relNo, f, s);
                }
            }
            if (valCounter == 0) {
                int s = random.nextInt(stkNo);
                randValAndUrg(project, relNo, f, s);
            }
        }
    }

    private static void randValAndUrg(Project project, int relNo, int f, int s) {
        ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
        Stakeholders stakeholders = project.getStakeholders();
        Features features = project.getFeatures();
        Releases releases = project.getReleases();

        Random random = new Random();
        Stakeholder stk;
        Feature feat;
        Urgency urg;

        feat = features.getFeature(f);
        stk = stakeholders.getStakeholder(s);
        int randVal = random.nextInt(9) + 1;
        int randUrg = random.nextInt(9) + 1;
        int randRel = random.nextInt(relNo);
        int randUrgCurve = random.nextInt(urgCurveLen);

        valueAndUrgency.setValue(stk, feat, randVal);
        urg = valueAndUrgency.getUrgencyObject(stk, feat);
        urg.setUrgency(randUrg);
        urg.setRelease(releases.getRelease(randRel));
        urg.setDeadlineCurve(urgCurve[randUrgCurve]);
    }

    private static void generateDependencies(Project project, AutotestSettings settings, int iterator) {
    }

    public static String numberGenerator(int n, int amount) {
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
