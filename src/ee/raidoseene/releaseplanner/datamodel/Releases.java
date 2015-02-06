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
public class Releases {

    private final List<Release> releaseContainer;
    //private Resources resources;

    Releases() {
        this.releaseContainer = new ArrayList<>();
    }

    /*Releases(Resources resources) {
     releaseContainer = new ArrayList<>();
     this.resources = resources;
     }*/
    public Release addRelease() {
        Release r = new Release();
        this.releaseContainer.add(r);
        return r;
    }

    public void removeRelease(Release r) {
        // TODO: make sure it's not in use
        releaseContainer.remove(r);
    }

    /*public List<Release> getReleaseList() {
        return releaseContainer;
    }

    public void removeResource(Resource r) {
        for (Release rel : releaseContainer) {
            rel.removeResource(r);
        }
    }

    public void addResource(Resource r) {
        for (Release rel : releaseContainer) {
            rel.addResource(r);
        }
    }*/
    
    public Release getRelease(int index) {
        return this.releaseContainer.get(index);
    }
    
    public int getReleaseCount() {
        return this.releaseContainer.size();
    }
    
}
