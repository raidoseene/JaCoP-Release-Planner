/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Raido Seene
 */
public class Criteria extends ProjectElement implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final List<Criterium> list;
    private final Map<Criteria.Key, Integer> relations;
    
    Criteria(Project project) {
        super(project);
        
        this.list = new ArrayList<>();
        this.relations = new HashMap<>();
        
        Criterium value = new Criterium(true);
        value.setName("Value");
        value.setWeight(5);
        this.list.add(value);
        
        Criterium urgency = new Criterium(true);
        urgency.setName("Urgency");
        urgency.setWeight(5);
        this.list.add(urgency);
    }
    
    public Criterium addCriterium() {
        Criterium c = new Criterium(false);
        this.list.add(c);
        this.modify();
        return c;
    }
    
    public void removeCriterium(Criterium c) {
        if (this.list.remove(c) && super.parent != null) {
            super.parent.criteriaRemoved(c);
            this.modify();
        }
    }
    
    public Criterium getCriterium(int Index) {
        return this.list.get(Index);
    }
    
    public Criterium getCriterium(String name) {
        for(int c = 0; c < this.list.size(); c++) {
            if(this.list.get(c).getName().toLowerCase().equals(name)) {
                return this.list.get(c);
            }
        }
        return null;
    }
    
    public int getCriteriumCount() {
        return this.list.size();
    }
    
    public void setCriteriumValue(Criterium c, Stakeholder stk, Feature f, int value) {
        Criteria.Key key = new Criteria.Key(c, stk, f);
        Integer val = this.relations.get(key);
        if (value > 0) {
            this.relations.put(key, value);
            this.modify();
        } else if (val != null) {
            this.relations.remove(key);
            this.modify();
        }
    }

    public int getCriteriumValue(Criterium c, Stakeholder stk, Feature f) {
        Criteria.Key key = new Criteria.Key(c, stk, f);
        Integer val = this.relations.get(key);
        return val != null ? val : 0;
    }
    
    private final class Key implements Serializable {

        private static final long serialVersionUID = 1;
        private final Stakeholder stakeholder;
        private final Criterium criteria;
        private final Feature feature;

        private Key(Criterium c, Stakeholder stk, Feature f) {
            this.stakeholder = stk;
            this.criteria = c;
            this.feature = f;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Criteria.Key) {
                Criteria.Key k = (Criteria.Key) o;
                return (this.stakeholder == k.stakeholder && this.criteria == k.criteria && this.feature == k.feature);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.stakeholder);
            hash = 17 * hash + Objects.hashCode(this.criteria);
            hash = 17 * hash + Objects.hashCode(this.feature);
            return hash;
        }
    }
    
    @Override
    public boolean isModified() {
        if(super.isModified()) {
            return true;
        } else {
            for(Criterium c: list) {
                if (c.isModified()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public void resetModification() {
        super.resetModification();
        for(Criterium c: list) {
            c.resetModification();
        }
    }
}
