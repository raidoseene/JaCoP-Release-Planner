/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import java.io.File;

/**
 *
 * @author risto
 */
public final class ResourceManager {
    
    private static final String RESOURCES_PATH = "resources";
    
    public static File getResourceFile(String name) throws Exception {
        return new File(new File(getDirectory(), RESOURCES_PATH), name);
    }
    
    private static File getDirectory() throws Exception {
        File jar = new File(ResourceManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        File dir = jar.getParentFile();
        
        if (dir == null) {
            throw new NullPointerException("Unable to reach parent directory!");
        } else if (dir.isDirectory()) {
            return dir;
        }
        
        throw new Exception("Parent file is not a directory!");
    }
    
}
