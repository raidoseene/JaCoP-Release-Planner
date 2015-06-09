/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;

/**
 *
 * @author Raido Seene
 */
public class ReleaseDependency extends Dependency implements Serializable {

    private static final long serialVersionUID = 1;
    
    private Feature feature;
    private Release release;

    ReleaseDependency(Feature feature, Release release, int type) {
        super(type);
        this.feature = feature;
        this.release = release;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public Release getRelease() {
        return this.release;
    }

    public int getType() {
        return this.type;
    }
}