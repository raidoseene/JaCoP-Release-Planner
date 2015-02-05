/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import ee.raidoseene.releaseplanner.gui.Messenger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Releases {

    private List<Release> releaseContainer;
    private Resources resources;

    Releases(Resources resources) {
        releaseContainer = new ArrayList<>();
        this.resources = resources;
    }

    public Release addRelease() {
        Release r = new Release(resources);
        releaseContainer.add(r);
        return r;
    }

    public void removeRelease(Release r) {
            releaseContainer.remove(r);
    }

    public List<Release> getReleaseList() {
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
    }
}
