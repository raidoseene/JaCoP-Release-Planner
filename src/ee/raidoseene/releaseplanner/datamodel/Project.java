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
public class Project extends NamedObject implements Serializable {

    private final Features features;
    private final Resources resources;
    private final Releases releases;
    //private Stakeholders stakeholders;

    public Project(String name) {
        super(name);
        
        this.features = new Features(this);
        this.resources = new Resources(this);
        this.releases = new Releases(this);
        //this.stakeholders = new Stakeholders();
    }

    /*public Project(String name, List<String> defaultResources, List<String> defaultReleases, List<String> defaultStakeholders) {
     this.name = name;
     resources = new Resources(defaultResources, releases);
     releases = new Releases(resources);
     stakeholders = new Stakeholders(defaultStakeholders);
     }*/
    
    public Resources getResources() {
        return this.resources;
    }

    public Releases getReleases() {
        return this.releases;
    }
    
    public Features getFeatures() {
        return this.features;
    }
    
    void featureRemoved(Feature f) {
        // TODO
    }
    
    void resourceRemoved(Resource r) {
        // TODO
        // Remove resource from releases
    }
    
    void releaseRemoved(Release r) {
        // TODO
    }

}
