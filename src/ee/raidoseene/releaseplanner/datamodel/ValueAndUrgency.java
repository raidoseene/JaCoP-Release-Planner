/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Raido Seene
 */
public class ValueAndUrgency extends ModifiableObject implements Serializable {

    private static final long serialVersionUID = 1;
    private final Map<ValueAndUrgency.Key, ValueAndUrgency.ValUrg> parameters;

    ValueAndUrgency() {
        this.parameters = new HashMap<>();
    }

    public void setValue(Stakeholder s, Feature f, int value) {
        ValueAndUrgency.Key key = new ValueAndUrgency.Key(s, f);
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(key);
        if (value > 0) {
            if (valUrg == null) {
                this.parameters.put(key, new ValueAndUrgency.ValUrg(value));
            } else {
                valUrg.value = value;
            }
            this.modify();
        } else if (valUrg != null) {
            this.parameters.remove(key);
            this.modify();
        }
    }

    public void setUrgency(Stakeholder s, Feature f, int urgency) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            valUrg.urgency.setUrgency(urgency);
            this.modify();
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }

    public void setRelease(Stakeholder s, Feature f, Release r) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            valUrg.urgency.setRelease(r);
            this.modify();
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }

    public void setDeadlineCurve(Stakeholder s, Feature f, int deadlineCurve) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            valUrg.urgency.setDeadlineCurve(deadlineCurve);
            this.modify();
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }

    public int getValue(Stakeholder s, Feature f) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            return valUrg.value;
        }
        return 0;
    }

    public int getUrgency(Stakeholder s, Feature f) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            return valUrg.urgency.getUrgency();
        }
        return 0;
    }

    public void setValUrgObject(Stakeholder s, Feature f, ValUrg valUrg) {
        ValueAndUrgency.Key key = new ValueAndUrgency.Key(s, f);
        this.parameters.put(key, valUrg);
        this.modify();
    }

    public Urgency getUrgencyObject(Stakeholder s, Feature f) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            return valUrg.urgency;
        }
        //System.out.println("Urgency is null!");
        return null;
    }

    public ValUrg getValUrgObject(Stakeholder s, Feature f) {
        return this.parameters.get(new ValueAndUrgency.Key(s, f));
    }

    public Release getUrgencyRelease(Stakeholder s, Feature f) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            return valUrg.urgency.getRelease();
        }
        return null;
    }

    public int getDeadlineCurve(Stakeholder s, Feature f) {
        ValueAndUrgency.ValUrg valUrg = this.parameters.get(new ValueAndUrgency.Key(s, f));
        if (valUrg != null) {
            return valUrg.urgency.getDeadlineCurve();
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

    public final class ValUrg implements Serializable {

        private static final long serialVersionUID = 1;
        private final Urgency urgency;
        private int value;

        private ValUrg(int v) {
            this.urgency = new Urgency();
            this.value = v;
        }
    }

    public static Urgency createStandaloneUrgency() {
        return new Urgency();
    }

    @Override
    public boolean isModified() {
        if (super.isModified()) {
            return true;
        } else {
            Collection<ValueAndUrgency.ValUrg> col = parameters.values();
            for (ValueAndUrgency.ValUrg v : col) {
                if (v.urgency != null && v.urgency.isModified()) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void resetModification() {
        super.resetModification();
        Collection<ValueAndUrgency.ValUrg> col = parameters.values();
        for (ValueAndUrgency.ValUrg v : col) {
            if (v.urgency != null) {
                v.urgency.resetModification();
            }
        }
    }
}
