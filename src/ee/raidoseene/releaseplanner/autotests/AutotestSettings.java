/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

/**
 *
 * @author Raido Seene
 */
public class AutotestSettings {

    private int projNo, featNo, relNo, resNo, stkNo, minCons, maxCons;
    private int featTo, relTo, resTo, stkTo;
    private float tightness, tightnessTo;
    private boolean projInterval, featInterval, relInterval, resInterval, stkInterval, tightnessInterval;
    private int[] totalResUsage;

    public AutotestSettings() {
        
        this.projNo = 0;
        this.projInterval = false;
        
        this.featNo = 0;
        this.featTo = 0;
        this.featInterval = false;
        
        this.relNo = 0;
        this.relTo = 0;
        this.relInterval = false;
        
        this.resNo = 0;
        this.resTo = 0;
        this.resInterval = false;
        
        this.stkNo = 0;
        this.stkTo = 0;
        this.stkInterval = false;
        
        this.tightness = 0.0f;
        this.tightnessTo = 0.0f;
        this.tightnessInterval = false;
        
        this.minCons = 0;
        this.maxCons = 0;
        
        this.totalResUsage = null;
    }
    
    public void setProjectNo(int projNo) {
        this.projNo = projNo;
    }
    
    public int getProjectNo() {
        return this.projNo;
    }
    
    public void setProjectInterval(boolean interval) {
        this.projInterval = interval;
    }
    
    public boolean getProjectInterval() {
        return this.projInterval;
    }

    public void setFeatureNo(int featNo) {
        this.featNo = featNo;
    }

    public int getFeatureNo() {
        return this.featNo;
    }
    
    public void setFeatureInterval(boolean featInterval) {
        this.featInterval = featInterval;
    }

    public boolean getFeatureInterval() {
        return this.featInterval;
    }
    
    public void setFeatureTo(int featTo) {
        this.featTo = featTo;
    }

    public int getFeatureTo() {
        return this.featTo;
    }
    
    public void setReleaseNo(int relNo) {
        this.relNo = relNo;
    }
    
    public int getReleaseNo() {
        return this.relNo;
    }
    
    public void setReleaseInterval(boolean relInterval) {
        this.relInterval = relInterval;
    }
    
    public boolean getReleaseInterval() {
        return this.relInterval;
    }
    
    public void setReleaseTo(int relTo) {
        this.relTo = relTo;
    }
    
    public int getReleaseTo() {
        return this.relTo;
    }
    
    public void setResourceNo(int resNo) {
        this.resNo = resNo;
    }
    
    public int getResourceNo() {
        return this.resNo;
    }
    
    public void setResourceInterval(boolean resInterval) {
        this.resInterval = resInterval;
    }
    
    public boolean getResourceInterval() {
        return this.resInterval;
    }
    
    public void setResourceTo(int resTo) {
        this.resNo = resTo;
    }
    
    public int getResourceTo() {
        return this.resTo;
    }
    
    public void setStakeholderNo(int stkNo) {
        this.stkNo = stkNo;
    }
    
    public int getStakeholderNo() {
        return this.stkNo;
    }
    
    public void setStakeholderInterval(boolean stkInterval) {
        this.stkInterval = stkInterval;
    }
    
    public boolean getStakeholderInterval() {
        return this.stkInterval;
    }
    
    public void setStakeholderTo(int stkTo) {
        this.stkTo = stkTo;
    }
    
    public int getStakeholderTo() {
        return this.stkTo;
    }
    
    public void setTightness(float tightness) {
        this.tightness = tightness;
    }
    
    public float getTightness() {
        return this.tightness;
    }
    
    public void setTightnessInterval(boolean tightnessInterval) {
        this.tightnessInterval = tightnessInterval;
    }
    
    public boolean getTightnessInterval() {
        return this.tightnessInterval;
    }
    
    public void setTightnessTo(float tightnessTo) {
        this.tightnessTo = tightnessTo;
    }
    
    public float getTightnessTo() {
        return this.tightnessTo;
    }
    
    public void setMinConsumption(int minCons) {
        this.minCons = minCons;
    }
    
    public int getMinConsumption() {
        return this.minCons;
    }
    
    public void setMaxConsumption(int maxCons) {
        this.maxCons = maxCons;
    }
    
    public int getMaxConsumption() {
        return this.maxCons;
    }
    
    public void addResConsumption(int id, int consumption) {
        if(this.totalResUsage == null) {
            this.totalResUsage = new int[this.resNo];
        }
        this.totalResUsage[id] += consumption;
    }
    
    public int getTotalResConsumption(int id) {
        return this.totalResUsage[id];
    }
}
