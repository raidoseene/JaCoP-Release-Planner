/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Features extends ProjectElement implements Serializable {

    private final List<Feature> featureContainer;
    //private Releases releases;

    Features(Project project) {
        super(project);

        this.featureContainer = new ArrayList<>();
    }

    public Feature addFeature() {
        Feature f = new Feature();
        this.featureContainer.add(f);
        return f;
    }

    public void removeFeature(Feature f) {
        if (this.featureContainer.remove(f) && super.parent != null) {
            super.parent.featureRemoved(f);
        }
    }

    public Feature getFeature(int index) {
        return this.featureContainer.get(index);
    }

    public int getFeatureCount() {
        return this.featureContainer.size();
    }
    
}