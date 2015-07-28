/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Releases extends ProjectElement implements Serializable {

    private static final long serialVersionUID = 1;
    private final List<Release> releaseContainer;

    Releases(Project project) {
        super(project);

        this.releaseContainer = new ArrayList<>();
        
        Release postponed = new Release(Release.Type.POSTPONED, 0);
        postponed.setName("Postponed");
        this.releaseContainer.add(postponed);
    }

    public Release addRelease() {
        Release r = new Release();
        int count = getReleaseCount();
        this.releaseContainer.add(count - 1, r);
        this.modify();
        return r;
    }

    public void removeRelease(Release r) {
        if (this.releaseContainer.remove(r) && super.parent != null) {
            super.parent.releaseRemoved(r);
            this.modify();
        }
    }

    public Release getRelease(int index) {
        return this.releaseContainer.get(index);
    }

    public int getReleaseCount() {
        return this.releaseContainer.size();
    }
    
    public int getReleaseIndex(Release release) {
        return this.releaseContainer.indexOf(release);
    }
    
    @Override
    public boolean isModified() {
        if(super.isModified()) {
            return true;
        } else {
            for(Release r: releaseContainer) {
                if (r.isModified()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public void resetModification() {
        super.resetModification();
        for(Release r: releaseContainer) {
            r.resetModification();
        }
    }
}
