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

    private static final long serialVersionUID = 1;
    private final List<Feature> featureContainer;

    Features(Project project) {
        super(project);

        this.featureContainer = new ArrayList<>();
    }

    public Feature addFeature() {
        Feature f = new Feature();
        this.featureContainer.add(f);
        this.modify();
        return f;
    }

    public void removeFeature(Feature f) {
        if (this.featureContainer.remove(f) && super.parent != null) {
            super.parent.featureRemoved(f);
            this.modify();
        }
    }

    public Feature getFeature(int index) {
        return this.featureContainer.get(index);
    }

    public int getFeatureCount() {
        return this.featureContainer.size();
    }
    
    public int getFeatureIndex(Feature feature) {
        return this.featureContainer.indexOf(feature);
    }
    
    public static Feature createStandaloneFeature() {
        return new Feature();
    }
    
    @Override
    public boolean isModified() {
        if(super.isModified()) {
            return true;
        } else {
            for(Feature f: featureContainer) {
                if (f.isModified()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public void resetModification() {
        super.resetModification();
        for(Feature f: featureContainer) {
            f.resetModification();
        }
    }
}
