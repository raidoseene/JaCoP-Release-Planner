/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raido Seene
 */
public class AutotestSettings {

    private int projNo;
    private int[] totalResUsage;
    private int repetitionNo;
    private int criteria;
    private final Map<Parameter, ParamValues> parameters;
    
    public enum Parameter {
        FEATURES,
        RESOURCE_CONS,
        RESOURCES,
        RELEASES,
        STAKEHOLDERS,
        TIGHTNESS,
        FIXED_DEP,
        EXCLUDED_DEP,
        EARLIER_DEP,
        LATER_DEP,
        SOFT_PRECEDENCE_DEP,
        HARD_PRECEDENCE_DEP,
        COUPLING_DEP,
        SEPARATION_DEP,
        AND_DEP,
        XOR_DEP
    }
    
    public class ParamValues {
        private Number min = null;
        private Number max = null;
        
        public ParamValues(Number min, Number max) {
            this.min = min;
            this.max = max;
        }
        
        public void setMin(Number min) {
            this.min = min;
        }
        
        public Number getMin() {
            return this.min;
        }
        
        public void setMax(Number max) {
            this.max = max;
        }
        
        public Number getMax() {
            return this.max;
        }
    }
    
    public void setParameter(Parameter param, Number min, Number max) {
        ParamValues values = getParameter(param);
        if(values == null) {
            if(min != null) {
                values = parameters.put(param, new ParamValues(min, max));
            }
        } else {
            if(min == null) {
                parameters.remove(param);
            } else {
                values.setMin(min);
                values.setMax(max);
            }
        }
    }
    
    public ParamValues getParameter(Parameter param) {
        return parameters.get(param);
    }

    public AutotestSettings() {
        this.parameters = new HashMap<>();
        this.criteria = 0;
    }
    
    public void setProjectNo(int projNo) {
        this.projNo = projNo;
    }
    
    public int getProjectNo() {
        return this.projNo;
    }
    
    public void setRepetitionNo(int repNo) {
        this.repetitionNo = repNo;
    }
    
    public int getRepetitionNo() {
        return this.repetitionNo;
    }
    
    public void setCriteriaNo(int criteriaNo) {
        this.criteria = criteriaNo;
    }
    
    public int getCriteriaNo() {
        return this.criteria;
    }
    
    public void initializeResConsumption(int resNo) {
        this.totalResUsage = new int[resNo];
    }
    
    public void addResConsumption(int id, int consumption) {
        this.totalResUsage[id] += consumption;
    }
    
    public int getTotalResConsumption(int id) {
        return this.totalResUsage[id];
    }
}
