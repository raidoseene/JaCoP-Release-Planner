/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author risto
 */
public final class ResourceManager {
    
    private static final String RESOURCES_PATH = "resources";
    
    public static File getResourceFile(String name) throws Exception {
        return new File(new File(getDirectory(), RESOURCES_PATH), name);
    }
    
    public static File getDirectory() throws Exception {
        File jar = new File(ResourceManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        File dir = jar.getParentFile();
        
        if (dir == null) {
            throw new NullPointerException("Unable to reach parent directory!");
        } else if (dir.isDirectory()) {
            return dir;
        }
        
        throw new Exception("Parent file is not a directory!");
    }
    
    public static boolean isOfType(String path, Type type) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file.getPath() + " does not exist!");
        }
        
        if (type == Type.DIRECTORY) {
            return file.isDirectory();
        } else if (type == Type.FILE) {
            return file.isFile();
        }
        
        return false;
    }
    
    public enum Type {
        DIRECTORY, FILE;
    }
    
}
