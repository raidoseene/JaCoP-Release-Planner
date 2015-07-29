/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raido Seene
 */
public class CandidatePlan extends ModifiableObject implements Serializable {

    private static final long serialVersionUID = 1;
    private final Map<Feature, Release> featureAllocations;
    private int planValue;
    private final Features features;
    private final Features independentFeatures;
    private final Releases releases;

    CandidatePlan(Features features, Releases releases, Features independendFeat) {
        this.features = features;
        this.independentFeatures = independendFeat;
        this.releases = releases;

        this.featureAllocations = new HashMap<>();
    }

    public void setPlanValue(int value) {
        this.planValue = value;
        this.modify();
    }

    public int getPlanValue() {
        return this.planValue;
    }

    public void setAllocation(Feature f, Release r) {
        this.featureAllocations.put(f, r);
        this.modify();
    }

    @Deprecated
    public Release getAllocation(Feature f) {
        return this.featureAllocations.get(f);
    }

    @Deprecated
    public Feature[] getAllocation(Release r) {
        ArrayList<Feature> featuresList = new ArrayList<>();

        int featCount = features.getFeatureCount();
        for (int i = 0; i < featCount; i++) {
            Release rel = featureAllocations.get(features.getFeature(i));
            if (r == rel) {
                featuresList.add(features.getFeature(i));
            }
        }

        return featuresList.toArray(new Feature[featuresList.size()]);
    }

    public String[][] getFeatureAllocationTable() {
        int count1 = features.getFeatureCount();
        int count2 = (independentFeatures != null ? independentFeatures.getFeatureCount() : 0);
        String[][] table = new String[count1 + count2][2];

        for (int i = 0; i < count1; i++) {
            Feature f = features.getFeature(i);
            Release r = this.featureAllocations.get(f);
            table[i][0] = f.getName();
            table[i][1] = r != null ? r.getName() : "Redundant";
        }
        for (int i = 0; i < count2; i++) {
            Feature f = independentFeatures.getFeature(i);
            Release r = this.featureAllocations.get(f);
            table[count1 + i][0] = f.getName();
            table[count1 + i][1] = r != null ? r.getName() : "Redundant";
        }

        return table;
    }

    public String[][] getReleaseContentTable() {
        int count1 = features.getFeatureCount();
        int count2 = (independentFeatures != null ? independentFeatures.getFeatureCount() : 0);
        int relCount = this.releases.getReleaseCount();
        String[][] table = new String[relCount][2];

        for (int rel = 0; rel < relCount; rel++) {
            Release r = releases.getRelease(rel);
            table[rel][0] = r.getName();
            for (int i = 0; i < count1; i++) {
                Feature f = features.getFeature(i);
                if (this.featureAllocations.get(f) == r) {
                    if (table[rel][1] != null) {
                        table[rel][1] = table[rel][1].concat(", " + f.getName());
                    } else {
                        table[rel][1] = f.getName();
                    }
                }
            }
            for (int i = 0; i < count2; i++) {
                Feature f = independentFeatures.getFeature(i);
                if (this.featureAllocations.get(f) == r) {
                    if (table[rel][1] != null) {
                        table[rel][1] = table[rel][1].concat(", " + f.getName());
                    } else {
                        table[rel][1] = f.getName();
                    }
                }
            }
        }
        return table;
    }
}
