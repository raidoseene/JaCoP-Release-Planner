/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Project {
    
    private String name;
    private Resources resources;
    private Releases releases;
    
    Project(String name, List<String> defaultResources, List<String> defaultReleases) {
        this.name = name;
        resources = new Resources(defaultResources);
        releases = new Releases(defaultReleases, resources);
    }
}
