/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Raido Seene
 */
public class ValueAndUrgency implements Serializable {

    private static final long serialVersionUID = 1;
    private final Map<ValueAndUrgency.Key, ValueAndUrgency.Value> parameters;

    ValueAndUrgency() {
        this.parameters = new HashMap<>();
    }

    public void setValue(Stakeholder s, Feature f, int value) {
        ValueAndUrgency.Key key = new ValueAndUrgency.Key(s, f);
        ValueAndUrgency.Value val = this.parameters.get(key);
        if (value > 0) {
            if (val == null) {
                this.parameters.put(key, new ValueAndUrgency.Value(value));
            } else {
                val.value = value;
            }
        } else if (val != null) {
            this.parameters.remove(key);
        }
    }
    /*
    public void setUrgency(Stakeholder s, Feature f, Release r, int urgency) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            val.urgency.setUrgency(r, urgency);
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }
    */
    
    public void setUrgency(Stakeholder s, Feature f, int urgency) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            val.urgency.setUrgency(urgency);
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }
    
    public void setRelease(Stakeholder s, Feature f, Release r) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            val.urgency.setRelease(r);
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }
    
    public void setDeadlineCurve(Stakeholder s, Feature f, int deadlineCurve) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            val.urgency.setDeadlineCurve(deadlineCurve);
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }
    

    public int getValue(Stakeholder s, Feature f) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            return val.value;
        }
        return 0;
    }
    /*
    public int getUrgency(Stakeholder s, Feature f, Release r) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            return val.urgency.getUrgency(r);
        }
        return 0;
    }
    */
    
    public int getUrgency(Stakeholder s, Feature f) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            return val.urgency.getUrgency();
        }
        return 0;
    }
    
    public Urgency getUrgencyObject(Stakeholder s, Feature f) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            return val.urgency;
        }
        return null;
    }
    
    public Release getUrgencyRelease(Stakeholder s, Feature f) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            return val.urgency.getRelease();
        }
        return null;
    }
    
    public int getDeadlineCurve(Stakeholder s, Feature f) {
        ValueAndUrgency.Value val = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (val != null) {
            return val.urgency.getDeadlineCurve();
        }
        return 0x00;
    }
    

    public int getValueAndUrgencyCount() {
        return this.parameters.size();
    }

    private final class Key implements Serializable {

        private static final long serialVersionUID = 1;
        private final Stakeholder stakeholder;
        private final Feature feature;

        private Key(Stakeholder s, Feature f) {
            this.stakeholder = s;
            this.feature = f;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ValueAndUrgency.Key) {
                ValueAndUrgency.Key k = (ValueAndUrgency.Key) o;
                return (this.stakeholder == k.stakeholder && this.feature == k.feature);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.stakeholder);
            hash = 17 * hash + Objects.hashCode(this.feature);
            return hash;
        }

    }

    private final class Value implements Serializable {

        private static final long serialVersionUID = 1;
        private final Urgency urgency;
        private int value;

        private Value(int v) {
            this.urgency = new Urgency();
            this.value = v;
        }

    }
    
    public static Urgency createStandaloneUrgency() {
        return new Urgency();
    }

}
