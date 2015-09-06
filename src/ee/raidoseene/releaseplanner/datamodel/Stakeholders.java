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
public class Stakeholders extends ProjectElement implements Serializable {

    private static final long serialVersionUID = 1;
    private List<Stakeholder> stakeholderContainer;

    Stakeholders(Project project) {
        super(project);

        stakeholderContainer = new ArrayList<>();
    }

    public Stakeholder addStakeholder() {
        Stakeholder stakeholder = new Stakeholder();
        stakeholderContainer.add(stakeholder);
        this.modify();
        return stakeholder;
    }

    public void removeStakeholder(Stakeholder stakeholder) {
        if (this.stakeholderContainer.remove(stakeholder) && super.parent != null) {
            super.parent.stakeholderRemoved(stakeholder);
            this.modify();
        }
    }

    public Stakeholder getStakeholder(int index) {
        return this.stakeholderContainer.get(index);
    }

    public int getStakeholderCount() {
        return this.stakeholderContainer.size();
    }
    
    public int getStakeholderIndex(Stakeholder stakeholder) {
        return this.stakeholderContainer.indexOf(stakeholder);
    }
    
    @Override
    public boolean isModified() {
        if(super.isModified()) {
            return true;
        } else {
            for(Stakeholder s: stakeholderContainer) {
                if (s.isModified()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public void resetModification() {
        super.resetModification();
        for(Stakeholder s: stakeholderContainer) {
            s.resetModification();
        }
    }
}
