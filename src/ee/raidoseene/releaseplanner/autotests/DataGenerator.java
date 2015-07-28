/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import ee.raidoseene.releaseplanner.autotests.AutotestSettings.ParamValues;
import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
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

    private static final int[] urgCurve = new int[]{
        Urgency.EXACT | Urgency.HARD,
        Urgency.EXACT | Urgency.SOFT,
        Urgency.EARLIEST | Urgency.HARD,
        Urgency.EARLIEST | Urgency.SOFT,
        Urgency.LATEST | Urgency.HARD,
        Urgency.LATEST | Urgency.SOFT};
    private static final int urgCurveLen = urgCurve.length;

    public static Project generateProject(String name, AutotestSettings settings, int iterator) throws Exception {
        System.out.println("Project generation started");
        ProjectManager.createNewProject(name);
        Project project = ProjectManager.getCurrentProject();

        int resNo = generateResources(project.getResources(), settings, iterator);
        int featNo = generateFeatures(project, settings, iterator, resNo);
        int relNo = generateReleases(project, settings, iterator, resNo);
        int stkNo = generateStakeholders(project, settings, iterator);
        generateValueAndUrgency(project, stkNo, featNo, relNo);
        generateDependencies(project, settings, iterator, featNo, relNo);

        System.out.println("Project generation ended");
        return project;
    }

    private static int generateResources(Resources resources, AutotestSettings settings, int iterator) {
        ParamValues params = settings.getParameter(AutotestSettings.Parameter.RESOURCES);
        int projNo = settings.getProjectNo();
        Integer resMin = (Integer) params.getMin();
        Integer resMax = (Integer) params.getMax();
        int resNo;

        if (resMax != null && resMax > resMin) {
            float step = (float) (resMax - resMin) / (float) projNo;
            resNo = resMin + Math.round(step * (float) iterator);
        } else {
            resNo = resMin;
        }

        for (int r = 0; r < resNo; r++) {
            Resource resource = resources.addResource();
            resource.setName("Res" + numberGenerator(r, resNo));
        }

        return resNo;
    }

    private static int generateFeatures(Project project, AutotestSettings settings, int iterator, int resNo) {
        ParamValues featParams = settings.getParameter(AutotestSettings.Parameter.FEATURES);
        ParamValues consParams = settings.getParameter(AutotestSettings.Parameter.RESOURCE_CONS);
        int projNo = settings.getProjectNo();
        Integer featMin = (Integer) featParams.getMin();
        Integer featMax = (Integer) featParams.getMax();
        Integer consMin = (Integer) consParams.getMin();
        Integer consMax = (Integer) consParams.getMax();
        Integer consMid = null;
        if (consMax != null) {
            consMid = (int) ((consMax - consMin + 1) * 0.6);
        }
        int featNo;

        Resources resources = project.getResources();
        Features features = project.getFeatures();

        settings.initializeResConsumption(resNo);
        Random random = new Random();

        if (featMax != null && featMax > featMin) {
            float step = (float) (featMax - featMin) / (float) projNo;
            featNo = featMin + Math.round(step * (float) iterator);
        } else {
            featNo = featMin;
        }

        for (int f = 0; f < featNo; f++) {
            Feature feature = features.addFeature();
            feature.setName("F" + numberGenerator(f, featNo));

            for (int r = 0; r < resNo; r++) {
                if (consMax != null) {
                    if (Math.random() > 0.4) {
                        int consumption = generateRandCons(random, consMin, consMid, consMax);
                        feature.setConsumption(resources.getResource(r), consumption);
                        settings.addResConsumption(r, consumption);
                    }
                } else {
                    feature.setConsumption(resources.getResource(r), consMin);
                    settings.addResConsumption(r, consMin);
                }
            }
            int randResId = random.nextInt(resNo);
            Resource randRes = resources.getResource(randResId);
            if (!feature.hasConsumption(randRes)) {
                if (consMax != null) {
                    int consumption = generateRandCons(random, consMin, consMid, consMax);
                    feature.setConsumption(randRes, consumption);
                    settings.addResConsumption(randResId, consumption);
                } else {
                    feature.setConsumption(randRes, consMin);
                    settings.addResConsumption(randResId, consMin);
                }
            }
        }
        return featNo;
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

    private static int generateReleases(Project project, AutotestSettings settings, int iterator, int resNo) {
        ParamValues relParams = settings.getParameter(AutotestSettings.Parameter.RELEASES);
        ParamValues tightParams = settings.getParameter(AutotestSettings.Parameter.TIGHTNESS);
        int projNo = settings.getProjectNo();
        Integer relMin = (Integer) relParams.getMin();
        Integer relMax = (Integer) relParams.getMax();
        Float tightMin = (Float) tightParams.getMin();
        Float tightMax = (Float) tightParams.getMax();
        int relNo;
        Float tightNo;
        
        Releases releases = project.getReleases();
        Resources resources = project.getResources();
        Random random = new Random();

        if (relMax != null && relMax > relMin) {
            float step = (float) (relMax - relMin) / (float) projNo;
            relNo = relMin + Math.round(step * (float) iterator);
        } else {
            relNo = relMin;
        }
        
        if (tightMax != null && tightMax > tightMin) {
            float step = (tightMax - tightMin) / (float) projNo;
            tightNo = tightMin + (step * (float) iterator);
        } else {
            tightNo = tightMin;
        }

        for (int r = 0; r < relNo; r++) {
            Release release = releases.addRelease();
            release.setName("Rel" + numberGenerator(r, relNo));
            release.setImportance(random.nextInt(9) + 1);

            for (int res = 0; res < resNo; res++) {
                int totalResConsumption = settings.getTotalResConsumption(res);
                int capacity = (int) ((float) totalResConsumption / (tightNo * (float) relNo));
                release.setCapacity(resources.getResource(res), capacity);
            }
        }
        return relNo;
    }

    private static int generateStakeholders(Project project, AutotestSettings settings, int iterator) {
        ParamValues params = settings.getParameter(AutotestSettings.Parameter.STAKEHOLDERS);
        int projNo = settings.getProjectNo();
        Integer stkMin = (Integer) params.getMin();
        Integer stkMax = (Integer) params.getMax();
        int stkNo;
        
        Stakeholders stakeholders = project.getStakeholders();
        Random random = new Random();

        if (stkMax != null && stkMax > stkMin) {
            float step = (float) (stkMax - stkMin) / (float) projNo;
            stkNo = stkMin + Math.round(step * (float) iterator);
        } else {
            stkNo = stkMin;
        }

        for (int s = 0; s < stkNo; s++) {
            Stakeholder stakeholder = stakeholders.addStakeholder();
            stakeholder.setName("Stk" + numberGenerator(s, stkNo));
            stakeholder.setImportance(random.nextInt(9) + 1);
        }
        return stkNo;
    }

    private static void generateValueAndUrgency(Project project, int stkNo, int featNo, int relNo) {
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

    private static void generateDependencies(Project project, AutotestSettings settings, int iterator, int featNo, int relNo) {
        System.out.println("Dependency generations started");
        
        ParamValues fixParams = settings.getParameter(AutotestSettings.Parameter.FIXED_DEP);
        ParamValues exParams = settings.getParameter(AutotestSettings.Parameter.EXCLUDED_DEP);
        ParamValues earParams = settings.getParameter(AutotestSettings.Parameter.EARLIER_DEP);
        ParamValues latParams = settings.getParameter(AutotestSettings.Parameter.LATER_DEP);
        ParamValues softParams = settings.getParameter(AutotestSettings.Parameter.SOFT_PRECEDENCE_DEP);
        ParamValues hardParams = settings.getParameter(AutotestSettings.Parameter.HARD_PRECEDENCE_DEP);
        ParamValues coupParams = settings.getParameter(AutotestSettings.Parameter.COUPLING_DEP);
        ParamValues sepParams = settings.getParameter(AutotestSettings.Parameter.SEPARATION_DEP);
        ParamValues andParams = settings.getParameter(AutotestSettings.Parameter.AND_DEP);
        ParamValues xorParams = settings.getParameter(AutotestSettings.Parameter.XOR_DEP);

        Random random = new Random();

        //System.out.println("Fixed dependency check");
        if (fixParams != null) {
            generateReleaseDep(project, settings, random, fixParams, Dependency.FIXED, iterator, featNo, relNo);
        }
        //System.out.println("Excluded dependency check");
        if (exParams != null) {
            generateReleaseDep(project, settings, random, exParams, Dependency.EXCLUDED, iterator, featNo, relNo);
        }
        //System.out.println("Earlier dependency check");
        if (earParams != null) {
            generateReleaseDep(project, settings, random, earParams, Dependency.EARLIER, iterator, featNo, relNo);
        }
        //System.out.println("Later dependency check");
        if (latParams != null) {
            generateReleaseDep(project, settings, random, latParams, Dependency.LATER, iterator, featNo, relNo);
        }

        //System.out.println("Soft precedence dependency check");
        if (softParams != null) {
            generateOrderDep(project, settings, random, softParams, Dependency.SOFTPRECEDENCE, iterator, featNo);
        }
        //System.out.println("Hard precedence dependency check");
        if (hardParams != null) {
            generateOrderDep(project, settings, random, hardParams, Dependency.HARDPRECEDENCE, iterator, featNo);
        }
        //System.out.println("Coupling dependency check");
        if (coupParams != null) {
            generateOrderDep(project, settings, random, coupParams, Dependency.COUPLING, iterator, featNo);
        }
        //System.out.println("Separation dependency check");
        if (sepParams != null) {
            generateOrderDep(project, settings, random, sepParams, Dependency.SEPARATION, iterator, featNo);
        }

        //System.out.println("And dependency check");
        if (andParams != null) {
            generateExistanceDep(project, settings, random, andParams, Dependency.AND, iterator, featNo);
        }
        //System.out.println("Xor dependency check");
        if (xorParams != null) {
            generateExistanceDep(project, settings, random, xorParams, Dependency.XOR, iterator, featNo);
        }
        //System.out.println("Dependency generations ended");
    }

    private static void generateReleaseDep(Project project, AutotestSettings settings, Random random, ParamValues params, int type, int iterator, int featNo, int relNo) {
        //System.out.println("Release dependency generations started");
        
        int projNo = settings.getProjectNo();
        Integer depMin = (Integer)params.getMin();
        Integer depMax = (Integer)params.getMax();
        int depNo;
        
        Feature randFeat;
        Release randRel;
        int randFeatNo = 0;

        int usedFeat[] = new int[featNo];
        
        Dependencies dependencies = project.getDependencies();
        Features features = project.getFeatures();
        Releases releases = project.getReleases();
        
        if (depMax != null && depMax > depMin) {
            float step = (float) (depMax - depMin) / (float) projNo;
            depNo = depMin + Math.round(step * (float) iterator);
        } else {
            depNo = depMin;
        }

        for (int d = 0; d < depNo; d++) {
            boolean uniqueFeat = false;
            while (!uniqueFeat) {
                randFeatNo = random.nextInt(featNo);
                uniqueFeat = true;

                for (int i = 0; i < d; i++) {
                    if (usedFeat[i] == randFeatNo) {
                        uniqueFeat = uniqueFeat & false;
                    } else {
                        uniqueFeat = uniqueFeat & true;
                    }
                }
            }
            usedFeat[d] = randFeatNo;

            randFeat = features.getFeature(randFeatNo);
            randRel = releases.getRelease(random.nextInt(relNo));

            if(dependencies.addReleaseDependency(randFeat, randRel, type, true, false) == null) {
                d--;
            }
        }
        //System.out.println("Release dependency generations ended");
    }

    private static void generateOrderDep(Project project, AutotestSettings settings, Random random, ParamValues params, int type, int iterator, int featNo) {
        //System.out.println("Order dependency generations started");
        
        int projNo = settings.getProjectNo();
        Integer depMin = (Integer)params.getMin();
        Integer depMax = (Integer)params.getMax();
        int depNo;
        
        int randFeat1;
        int randFeat2 = 0;
        
        Dependencies dependencies = project.getDependencies();
        Features features = project.getFeatures();
        
        if (depMax != null && depMax > depMin) {
            float step = (float) (depMax - depMin) / (float) projNo;
            depNo = depMin + Math.round(step * (float) iterator);
        } else {
            depNo = depMin;
        }
        
        for (int d = 0; d < depNo; d++) {
            randFeat1 = random.nextInt(featNo);

            boolean uniqueFeat = false;
            while (!uniqueFeat) {
                randFeat2 = random.nextInt(featNo);
                uniqueFeat = true;

                if (randFeat1 == randFeat2) {
                    uniqueFeat = uniqueFeat & false;
                } else {
                    uniqueFeat = uniqueFeat & true;
                }
            }

            dependencies.addOrderDependency(features.getFeature(randFeat1), features.getFeature(randFeat2), type);
        }
        //System.out.println("Order dependency generations ended");
    }

    private static void generateExistanceDep(Project project, AutotestSettings settings, Random random, ParamValues params, int type, int iterator, int featNo) {
        //System.out.println("Existance dependency generations started");
        int projNo = settings.getProjectNo();
        Integer depMin = (Integer)params.getMin();
        Integer depMax = (Integer)params.getMax();
        int depNo;
        
        int randFeat1;
        int randFeat2 = 0;
        int randFeatNo = 0;
        
        Dependencies dependencies = project.getDependencies();
        Features features = project.getFeatures();
        
        if (depMax != null && depMax > depMin) {
            float step = (float) (depMax - depMin) / (float) projNo;
            depNo = depMin + Math.round(step * (float) iterator);
        } else {
            depNo = depMin;
        }

        for (int d = 0; d < depNo; d++) {
            randFeat1 = random.nextInt(featNo);

            boolean uniqueFeat = false;
            while (!uniqueFeat) {
                randFeat2 = random.nextInt(featNo);
                uniqueFeat = true;

                if (randFeat1 == randFeatNo) {
                    uniqueFeat = uniqueFeat & false;
                } else {
                    uniqueFeat = uniqueFeat & true;
                }
            }

            dependencies.addExistanceDependency(features.getFeature(randFeat1), features.getFeature(randFeat2), type);
        }
        //System.out.println("Existance dependency generations ended");
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
