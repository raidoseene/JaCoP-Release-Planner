/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Resources {

    private final List<Resource> resourceContainer;
    //private Releases releases;

    Resources() {
        this.resourceContainer = new ArrayList<>();
    }

    /*Resources(List<String> defaultResources, Releases releases) {
     resourceContainer = new ArrayList<>();
     this.releases = releases;
     for (String r : defaultResources) {
     Resource res = new Resource();
     resourceContainer.add(res);
     res.setName(r);
     }
     }*/
    public Resource addResource() {
        Resource r = new Resource();
        this.resourceContainer.add(r);
        //releases.addResource(r);
        return r;
    }

    public void removeResource(Resource r) {
        // TODO: make sure it's not in use
        resourceContainer.remove(r);
        //releases.removeResource(r);
    }

    /*public List<Resource> getResourceList() {
     return resourceContainer;
     }*/
    public Resource getResource(int index) {
        return this.resourceContainer.get(index);
    }

    public int getResourceCount() {
        return this.resourceContainer.size();
    }

}
