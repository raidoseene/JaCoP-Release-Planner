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
    private boolean codeOutput;
    private boolean resourceShifting;
    
    Settings() {
        this.pathMiniZinc = null;
        this.pathJaCoP = null;
        this.codeOutput = false;
        this.resourceShifting = false;
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
}
