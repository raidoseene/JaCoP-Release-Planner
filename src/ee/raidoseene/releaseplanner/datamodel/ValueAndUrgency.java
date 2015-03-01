/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raido Seene
 */
public class ValueAndUrgency implements Serializable {

    private final Map<Pair<Stakeholder, Feature>, Pair<Integer, Urgency>> parameters;

    ValueAndUrgency() {
        this.parameters = new HashMap<>();
    }

    public void setValue(Stakeholder s, Feature f, int value) {
        Pair<Stakeholder, Feature> stkFea = new Pair(s, f);
        if (value > 0) {
            Urgency u = new Urgency();
            Pair<Integer, Urgency> valUrg = new Pair(value, u);
            this.parameters.put(stkFea, valUrg);
        } else {
            this.parameters.remove(stkFea);
        }
    }

    public void setUrgency(Stakeholder s, Feature f, Release r, int urgency) {
        Pair<Stakeholder, Feature> stkFea = new Pair(s, f);
        Pair<Integer, Urgency> valUrg = this.parameters.get(stkFea);
        if (valUrg != null) {
            if (urgency > 0) {
                valUrg.getSecondary().setUrgency(r, urgency);
            } else {
                valUrg.getSecondary().removeUrgency(r);
            }
        } else {
            throw new RuntimeException("Urgency cannot be set without value!");
        }
    }

    public int getValue(Stakeholder s, Feature f) {
        Pair<Stakeholder, Feature> stkFea = new Pair(s, f);
        Pair<Integer, Urgency> valUrg = this.parameters.get(stkFea);
        if (valUrg != null) {
            return valUrg.getPrimary();
        }
        return 0;
    }

    public int getUrgency(Stakeholder s, Feature f, Release r) {
        Pair<Stakeholder, Feature> stkFea = new Pair(s, f);
        Pair<Integer, Urgency> valUrg = this.parameters.get(stkFea);
        if (valUrg != null) {
            return valUrg.getSecondary().getUrgency(r);
        }
        return 0;
    }
    
    public int getValueAndUrgencyCount() {
        return this.parameters.size();
    }

    private final class Pair<P, S> {

        private P primary;
        private S secondary;

        public Pair(P primary, S secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        public P getPrimary() {
            return primary;
        }

        public S getSecondary() {
            return secondary;
        }

        public void setPrimary(P primary) {
            this.primary = primary;
        }

        public void setSecondary(S secondary) {
            this.secondary = secondary;
        }

        @Override
        public int hashCode() {
            return primary.hashCode() ^ secondary.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof Pair)) {
                return false;
            }
            Pair pairo = (Pair) o;
            return this.primary.equals(pairo.getPrimary())
                    && this.secondary.equals(pairo.getSecondary());
        }
    }
}