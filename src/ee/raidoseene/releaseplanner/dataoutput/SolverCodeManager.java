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

    private final Project project;
    private final PrintWriter printWriter;

    public static File saveSolverCodeFile(Project project, boolean useResourceShifting, boolean isGroups) throws Exception {
        //File inputDir = new File(project.getStorage());
        //File inputFile1 = new File(inputDir, "SolverCode_Par1.mzn");
        //File inputFile2 = new File(inputDir, "SolverCode_Par2.mzn");
        
        String path = project.getStorage();
        int index = path.lastIndexOf("\\");
        if (index >= 0) {
            path = path.substring(0, index);
        }
        
        Path Par1Path = Paths.get(path, "SolverCode_Par1_Header.par");
        Path Par2_1Path = Paths.get(path, "SolverCode_Par2.1_Declarations.par");
        Path Par2_2Path = Paths.get(path, "SolverCode_Par2.2_Declarations.par");
        Path Par3Path = Paths.get(path, "SolverCode_Par3_Variables.par");
        Path Par4Path = Paths.get(path, "SolverCode_Par4_Constraints.par");
        Path Par5Path = Paths.get(path, "SolverCode_Par5_GroupDep.par");
        Path Par6_1Path = Paths.get(path, "SolverCode_Par6.1_NonCumResConstraint.par");
        Path Par6_2Path = Paths.get(path, "SolverCode_Par6.2_CumResConstraint.par");
        Path Par7Path = Paths.get(path, "SolverCode_Par7_ObjectiveFunction.par");


        File outputDir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File outputFile = new File(outputDir, "SolverCode.mzn");

        try (PrintWriter pw = new PrintWriter(outputFile)) { // try with resource: printwriter closes automatically!
            SolverCodeManager dm = new SolverCodeManager(project, pw);

            StringBuilder sb = new StringBuilder();
            
            sb.append(readFile(Par1Path));
            sb.append(readFile(Par2_1Path));
            if(isGroups) {
                sb.append(readFile(Par2_2Path));
            }
            sb.append(readFile(Par3Path));
            //sb.append(readFile(Par4Path));
            sb.append(DependencyManager.getDependenciesData(project, DataManager.ModifyingDependencyConversion(project), true));
            if(isGroups) {
                sb.append(readFile(Par5Path));
            }
            if(!useResourceShifting) {
                sb.append(readFile(Par6_1Path));
            } else {
                sb.append(readFile(Par6_2Path));
            }
            sb.append(readFile(Par7Path));

            dm.printCode(sb);
        }
        return outputFile;
    }

    private SolverCodeManager(Project project, PrintWriter pw) {
        this.project = project;
        this.printWriter = pw;
    }

    private static String readFile(Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset());
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
