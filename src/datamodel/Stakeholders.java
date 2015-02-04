/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Stakeholders {

    private List<Stakeholder> stakeholderContainer;
    
    Stakeholders(List<String> defaultStakeholders) {
        stakeholderContainer = new ArrayList<Stakeholder>();
        for (String s : defaultStakeholders) {
            addStakeholder(s);
        }
    }
    
    public void addStakeholder() {
        stakeholderContainer.add(new Stakeholder());
    }
    
    public void addStakeholder(String name) {
        stakeholderContainer.add(new Stakeholder(name));
    }
    
    public void removeStakeholder(int id) {
        stakeholderContainer.remove(this.getStakeholder(id));
    }
    
    public void changeStakeholderName(int id, String name) {
        this.getStakeholder(id).setName(name);
    }
    
    public List<Stakeholder> getStakeholderList() {
        List<Stakeholder> list = new ArrayList<Stakeholder>();
        for (Stakeholder s : stakeholderContainer) {
            list.add(s);
        }
        return list;
    }
    
    private Stakeholder getStakeholder(int id) {
        return stakeholderContainer.get(id);
}
