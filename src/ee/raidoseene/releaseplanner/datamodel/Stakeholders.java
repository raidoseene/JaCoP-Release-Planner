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

    private List<Stakeholder> stakeholderContainer;
    private int importance;

    Stakeholders(Project project) {
        super(project);

        stakeholderContainer = new ArrayList<>();
    }

    public Stakeholder addStakeholder() {
        Stakeholder stakeholder = new Stakeholder();
        stakeholderContainer.add(stakeholder);
        return stakeholder;
    }

    public void removeStakeholder(Stakeholder stakeholder) {
        if (this.stakeholderContainer.remove(stakeholder) && super.parent != null) {
            super.parent.stakeholderRemoved(stakeholder);
        }
    }

    public Stakeholder getStakeholder(int index) {
        return this.stakeholderContainer.get(index);
    }

    public int getStakeholderCount() {
        return this.stakeholderContainer.size();
    }
}
