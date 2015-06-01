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

    private int featNo, relNo, resNo, stkNo;

    AutotestSettings() {
        this.featNo = 0;
        this.relNo = 0;
        this.resNo = 0;
        this.stkNo = 0;
    }

    public void setFeatureNo(int featNo) {
        this.featNo = featNo;
    }

    public int getFeatureNo() {
        return this.featNo;
    }
    
    public void setReleaseN0(int relNo) {
        this.relNo = relNo;
    }
    
    public int getReleaseNo() {
        return this.relNo;
    }
    
    public void setResourceNo(int resNo) {
        this.resNo = resNo;
    }
    
    public int getResourceNo() {
        return this.resNo;
    }
    
    public void setStakeholderNo(int stkNo) {
        this.stkNo = stkNo;
    }
    
    public int getStakeholderNo() {
        return this.stkNo;
    }
}
