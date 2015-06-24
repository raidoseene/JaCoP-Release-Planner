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
    private int fixedNo, excludedNo, earlierNo, laterNo;
    private int softPrecedenceNo, hardPrecedenceNo, couplingNo, separationNo;
    private int andNo, xorNo, atLeastNo, atMostNo, exacltyNo;
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
        
        this.fixedNo = 0;
        this.excludedNo = 0;
        this.earlierNo = 0;
        this.laterNo = 0;
        
        this.softPrecedenceNo = 0;
        this.hardPrecedenceNo = 0;
        this.couplingNo = 0;
        this.separationNo = 0;
        
        this.andNo = 0;
        this.xorNo = 0;
        
        this.atLeastNo = 0;
        this.atMostNo = 0;
        this.exacltyNo = 0;
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
        if(this.featInterval) {
            setProjectNo(featNo, this.featTo);
        }
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
        setProjectNo(this.featNo, featTo);
    }

    public int getFeatureTo() {
        return this.featTo;
    }
    
    public void setReleaseNo(int relNo) {
        this.relNo = relNo;
        if(this.relInterval) {
            setProjectNo(relNo, this.relTo);
        }
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
        setProjectNo(this.relNo, relTo);
    }
    
    public int getReleaseTo() {
        return this.relTo;
    }
    
    public void setResourceNo(int resNo) {
        this.resNo = resNo;
        if(this.resInterval) {
            setProjectNo(resNo, this.resTo);
        }
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
        setProjectNo(this.resNo, resTo);
    }
    
    public int getResourceTo() {
        return this.resTo;
    }
    
    public void setStakeholderNo(int stkNo) {
        this.stkNo = stkNo;
        if(this.stkInterval) {
            setProjectNo(stkNo, this.stkTo);
        }
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
        setProjectNo(this.stkNo, stkTo);
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
    
    public void initializeResConsumption() {
        this.totalResUsage = new int[this.resNo];
    }
    
    public void addResConsumption(int id, int consumption) {
        this.totalResUsage[id] += consumption;
    }
    
    public int getTotalResConsumption(int id) {
        return this.totalResUsage[id];
    }
    
    public void setFixedNo(int fixedNo) {
        this.fixedNo = fixedNo;
    }
    
    public int getFixedNo() {
        return this.fixedNo;
    }
    
    public void setExcludedNo(int excludedNo) {
        this.excludedNo = excludedNo;
    }
    
    public int getExcludedNo() {
        return this.excludedNo;
    }
    
    public void setEarlierNo(int earlierNo) {
        this.earlierNo = earlierNo;
    }
    
    public int getEarlierNo() {
        return this.earlierNo;
    }
    
    public void setLaterNo(int laterNo) {
        this.laterNo = laterNo;
    }
    
    public int getLaterNo() {
        return this.laterNo;
    }
    
    public void setSoftPrecedenceNo(int softPrecedenceNo) {
        this.softPrecedenceNo = softPrecedenceNo;
    }
    
    public int getSoftPrecedenceNo() {
        return this.softPrecedenceNo;
    }
    
    public void setHardPrecedenceNo(int hardPrecedenceNo) {
        this.hardPrecedenceNo = hardPrecedenceNo;
    }
    
    public int getHardPrecedenceNo() {
        return this.hardPrecedenceNo;
    }
    
    public void setCouplingNo(int couplingNo) {
        this.couplingNo = couplingNo;
    }
    
    public int getCouplingNo() {
        return this.couplingNo;
    }
    
    public void setSeparationNo(int separationNo) {
        this.separationNo = separationNo;
    }
    
    public int getSeparationNo() {
        return this.separationNo;
    }
    
    public void setAndNo(int andNo) {
        this.andNo = andNo;
    }
    
    public int getAndNo() {
        return this.andNo;
    }

    public void setXorNo(int xorNo) {
        this.xorNo = xorNo;
    }
    
    public int getXorNo() {
        return this.xorNo;
    }

    public void setAtLeastNo(int atLeastNo) {
        this.atLeastNo = atLeastNo;
    }
    
    public int getAtLeastNo() {
        return this.atLeastNo;
    }

    public void setAtMostNo(int atMostNo) {
        this.atMostNo = atMostNo;
    }

    public int getAtMostNo() {
        return this.atMostNo;
    }

    public void setExacltyNo(int exacltyNo) {
        this.exacltyNo = exacltyNo;
    }

    public int getExacltyNo() {
        return this.exacltyNo;
    }
    
    private void setProjectNo(int min, int max) {
        this.projNo = max - min + 1;
    }
}
