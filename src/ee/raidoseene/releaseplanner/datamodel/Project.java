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
public class Project {
    
    private String name;
    private Resources resources;
    private Releases releases;
    private Stakeholders stakeholders;
    
    Project(String name, List<String> defaultResources, List<String> defaultReleases, List<String> defaultStakeholders) {
        this.name = name;
        resources = new Resources(defaultResources, releases);
        releases = new Releases(resources);
        stakeholders = new Stakeholders(defaultStakeholders);
    }
}
