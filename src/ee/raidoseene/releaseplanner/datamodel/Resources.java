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
public class Resources extends ProjectElement {

    private final List<Resource> resourceContainer;
    //private Releases releases;

    Resources(Project project) {
        super(project);

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
        if (this.resourceContainer.remove(r) && super.parent != null) {
            super.parent.resourceRemoved(r);
        }
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
