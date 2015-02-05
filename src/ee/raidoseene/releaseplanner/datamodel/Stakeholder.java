/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

/**
 * 
 * @author Raido Seene
 */
public class Stakeholder {
    
    private String name;
    private int importance;
    
    Stakeholder() {};

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setImportance(int importance) {
        this.importance = importance;
    }
    
    public int getImportance() {
        return importance;
    }
}
