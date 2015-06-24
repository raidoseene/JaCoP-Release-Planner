/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Raido Seene
 */
public class SolverCodeManager {

    private final PrintWriter printWriter;

    public static File saveSolverCodeFile(Project project, DependencyManager depMan, boolean useResourceShifting, boolean isGroups) throws Exception {
        //File inputDir = new File(project.getStorage());
        //File inputFile1 = new File(inputDir, "SolverCode_Par1.mzn");
        //File inputFile2 = new File(inputDir, "SolverCode_Par2.mzn");
        
        String path = project.getStorage();
        File dir = new File(path).getParentFile();
        /*int index = path.lastIndexOf(File.separator);
        if (index >= 0) {
            path = path.substring(0, index);
        }*/
        
        File Par1File = new File(dir, "SolverCode_Par1_Header.par");
        File Par2_1File = new File(dir, "SolverCode_Par2.1_Declarations.par");
        File Par2_2File = new File(dir, "SolverCode_Par2.2_Declarations.par");
        File Par3File = new File(dir, "SolverCode_Par3_Variables.par");
        //Path Par4Path = Paths.get(path, "SolverCode_Par4_Constraints.par");
        File Par5File = new File(dir, "SolverCode_Par5_GroupDep.par");
        File Par6_1File = new File(dir, "SolverCode_Par6.1_NonCumResConstraint.par");
        File Par6_2File = new File(dir, "SolverCode_Par6.2_CumResConstraint.par");
        File Par7File = new File(dir, "SolverCode_Par7_ObjectiveFunction.par");


        File outputDir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File outputFile = new File(outputDir, "SolverCode.mzn");

        try (PrintWriter pw = new PrintWriter(outputFile)) { // try with resource: printwriter closes automatically!
            SolverCodeManager dm = new SolverCodeManager(pw);

            StringBuilder sb = new StringBuilder();
            
            sb.append(readFile(Par1File));
            sb.append(readFile(Par2_1File));
            if(isGroups) {
                sb.append(readFile(Par2_2File));
            }
            sb.append(readFile(Par3File));
            //sb.append(readFile(Par4Path));
            sb.append(depMan.getDependenciesData(true));
            if(isGroups) {
                sb.append(readFile(Par5File));
            }
            if(!useResourceShifting) {
                sb.append(readFile(Par6_1File));
            } else {
                sb.append(readFile(Par6_2File));
            }
            sb.append(readFile(Par7File));

            dm.printCode(sb);
        }
        return outputFile;
    }

    private SolverCodeManager(PrintWriter pw) {
        this.printWriter = pw;
    }

    private static String readFile(File file) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.defaultCharset());
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private void printCode(StringBuilder sb) {
        printWriter.print(sb.toString());
    }
}
