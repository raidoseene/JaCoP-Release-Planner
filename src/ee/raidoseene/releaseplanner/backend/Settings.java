/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import java.io.Serializable;

/**
 *
 * @author Raido Seene
 */
public final class Settings implements Serializable {
    
    private static final long serialVersionUID = 1;
    private String pathMiniZinc, pathJaCoP;
    private Integer solverTimeLimit;
    private boolean codeOutput, resourceShifting, postponedUrgency, normalizedImportances, limitSolverTime;
    
    Settings() {
        this.pathMiniZinc = null;
        this.pathJaCoP = null;
        this.codeOutput = true;
        this.resourceShifting = false;
        this.postponedUrgency = true;
        this.normalizedImportances = true;
        this.limitSolverTime = false;
        this.solverTimeLimit = null;
    }
    
    public void setMiniZincPath(String path) throws Exception {
        if (path == null || ResourceManager.isOfType(path, ResourceManager.Type.DIRECTORY)) {
            this.pathMiniZinc = path;
        } else {
            throw new Exception("Not a directory!");
        }
    }

    public String getMiniZincPath() {
        return this.pathMiniZinc;
    }
    
    public void setJaCoPPath(String path) throws Exception {
        if (path == null || ResourceManager.isOfType(path, ResourceManager.Type.DIRECTORY)) {
            this.pathJaCoP = path;
        } else {
            throw new Exception("Not a directory!");
        }
    }

    public String getJaCoPPath() {
        return this.pathJaCoP;
    }
    
    public void setCodeOutput(boolean codeOutput){
        this.codeOutput = codeOutput;
    }
    
    public boolean getCodeOutput() {
        return this.codeOutput;
    }
    
    public void setResourceShifting(boolean resourceShifting) {
        this.resourceShifting = resourceShifting;
    }
    
    public boolean getResourceShifting() {
        return this.resourceShifting;
    }
    
    @Deprecated
    public void setPostponedUrgency(boolean postponedUrgency) {
        this.postponedUrgency = postponedUrgency;
    }
    
    @Deprecated
    public boolean getPostponedUrgency() {
        return this.postponedUrgency;
    }
    
    public void setNormalizedImportances(boolean normImp) {
        this.normalizedImportances = normImp;
    }
    
    public boolean getNormalizedImportances() {
        return this.normalizedImportances;
    }
    
    public void setLimitSolverTime(boolean limit) {
        this.limitSolverTime = limit;
    }
    
    public boolean getLimitSolverTime() {
        return this.limitSolverTime;
    }
    
    public void setSolverTimeLimit(Integer time) {
        this.solverTimeLimit = time;
    }
    
    public Integer getSolverTimeLimit() {
        return this.solverTimeLimit;
    }
}
