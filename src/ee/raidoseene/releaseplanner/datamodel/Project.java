/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Project extends NamedObject {

    private final Resources resources;
    private final Releases releases;
    //private Stakeholders stakeholders;

    public Project() {
        this.resources = new Resources();
        this.releases = new Releases();
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

}
