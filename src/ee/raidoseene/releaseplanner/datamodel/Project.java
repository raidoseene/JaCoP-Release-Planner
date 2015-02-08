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
    private final Interdependencies interdependencies;
    private final Urgencies urgencies;
    private final Values values;
    private final Resources resources;
    private final Releases releases;
    private final Stakeholders stakeholders;

    public Project(String name) {
        super(name);

        this.features = new Features(this);
        this.interdependencies = new Interdependencies(this);
        this.urgencies = new Urgencies();
        this.values = new Values();
        this.resources = new Resources(this);
        this.releases = new Releases(this);
        this.stakeholders = new Stakeholders(this);
    }

    /*public Project(String name, List<String> defaultResources, List<String> defaultReleases, List<String> defaultStakeholders) {
     this.name = name;
     resources = new Resources(defaultResources, releases);
     releases = new Releases(resources);
     stakeholders = new Stakeholders(defaultStakeholders);
     }*/
    public Features getFeatures() {
        return this.features;
    }

    public Urgencies getUrgencies() {
        return this.urgencies;
    }

    public Values getValues() {
        return this.values;
    }

    public Resources getResources() {
        return this.resources;
    }

    public Releases getReleases() {
        return this.releases;
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
}
