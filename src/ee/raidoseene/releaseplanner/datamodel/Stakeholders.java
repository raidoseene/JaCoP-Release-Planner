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
public final class Stakeholders {

    private List<Stakeholder> stakeholderContainer;
    private int importance;

    Stakeholders(List<String> defaultStakeholders) {
        stakeholderContainer = new ArrayList<>();
        for (String s : defaultStakeholders) {
            Stakeholder stk = new Stakeholder();
            stakeholderContainer.add(stk);
            stk.setName(s);
        }
    }

    public Stakeholder addStakeholder() {
        Stakeholder s = new Stakeholder();
        stakeholderContainer.add(s);
        return s;
    }

    public void removeStakeholder(Stakeholder s) {
        stakeholderContainer.remove(s);
    }

    public List<Stakeholder> getStakeholderList() {
        return stakeholderContainer;
    }
}
