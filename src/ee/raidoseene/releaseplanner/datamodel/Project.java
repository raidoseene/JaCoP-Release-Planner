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

    private static final long serialVersionUID = 1;
    private transient String storage;
    private final Features features;
    private final Groups groups;
    private final Dependencies dependencies;
    private final ValueAndUrgency valueAndUrgency;
    private final Resources resources;
    private final Releases releases;
    private final Stakeholders stakeholders;

    public Project(String name) {
        super(name);

        this.storage = null;
        this.features = new Features(this);
        this.groups = new Groups(this);
        this.dependencies = new Dependencies(this);
        this.valueAndUrgency = new ValueAndUrgency();
        this.resources = new Resources(this);
        this.releases = new Releases(this);
        this.stakeholders = new Stakeholders(this);
       
    }
    
    public void setStorage(String storage) {
        this.storage = storage;
    }
    
    public String getStorage() {
        return this.storage;
    }

    public Features getFeatures() {
        return this.features;
    }
    
    public Groups getGroups() {
        return this.groups;
    }
    
    public ValueAndUrgency getValueAndUrgency() {
        return this.valueAndUrgency;
    }

    public Resources getResources() {
        return this.resources;
    }

    public Releases getReleases() {
        return this.releases;
    }
    
    public Stakeholders getStakeholders() {
        return this.stakeholders;
    }
    
    public Dependencies getDependencies() {
        return this.dependencies;
    }

    void featureRemoved(Feature feature) {
        // TODO
    }

    void dependencyRemoved(Dependency dependency) {
        // TODO
    }

    void resourceRemoved(Resource resource) {
        // TODO
        // Remove resource from releases
    }

    void releaseRemoved(Release release) {
        // TODO
    }

    void stakeholderRemoved(Stakeholder stakeohlder) {
        // TODO
    }

    void groupRemoved(Group g) {
        // TODO
    }
}
