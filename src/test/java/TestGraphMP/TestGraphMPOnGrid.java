package TestGraphMP;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import edu.albany.cs.base.APDMInputFormat;
import edu.albany.cs.base.Utils;
import edu.albany.cs.graphMP.GraphMP;
import edu.albany.cs.scoreFuncs.ProtestScanStat;

public class TestGraphMPOnGrid {

    public double q = 0.0D;
    public String inputFileName = null;
    public double[] Lambda = null;
    public int[][] X = null;
    public HashSet<Integer> R;

    public void generateData(double q) {
        String inputFile = "./data/SimulationData/Protest/APDM-GridData-100-precen_0.1-noise_0-numCC_1_0.txt";
        APDMInputFormat apdmInputFormat = new APDMInputFormat(inputFile);
        int n = 100;
        int t = 10;
        double[] Lambda = new double[n];
        for (int i = 0; i < apdmInputFormat.data.numNodes; i++) {
            Lambda[i] = new Random().nextDouble() + 10.0D;// normal distribution
        }
        int[][] X = new int[n][t];
        for (int i = 0; i < apdmInputFormat.data.numNodes; i++) {
            if (ArrayUtils.contains(apdmInputFormat.data.trueSubGraphNodes, i)) {
                PoissonDistribution pos = new PoissonDistribution(q * Lambda[i]);
                for (int j = 0; j < t; j++) {
                    X[i][j] = pos.sample();
                }
            } else {
                PoissonDistribution pos = new PoissonDistribution(Lambda[i]);
                for (int j = 0; j < t; j++) {
                    X[i][j] = pos.sample();
                }
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(new File("./data/SimulationData/Protest/simulation_100_q_" + q + ".txt"));
            fileWriter.write("file : " + inputFile + "\n");
            fileWriter.write("trueSubGraph : " + Arrays.toString(apdmInputFormat.data.trueSubGraphNodes) + "\n");
            fileWriter.write("q : " + q + "\n");
            for (int i = 0; i < apdmInputFormat.data.numNodes; i++) {
                fileWriter.write("lambda" + i + " : " + Lambda[i] + "\n");
            }
            for (int i = 0; i < apdmInputFormat.data.numNodes; i++) {
                fileWriter.write("X[" + i + "] : ");
                for (int j = 0; j < t; j++) {
                    fileWriter.write(X[i][j] + " ");
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // different true subgraph size
    }

    public void getProtestData(String fileName) {

        try {
            for (String eachLine : Files.readAllLines(Paths.get(fileName))) {
                String[] items = eachLine.split(" : ");
                if (items[0].startsWith("q")) {
                    q = Double.parseDouble(items[1].trim());
                }
                if (items[0].startsWith("file")) {
                    inputFileName = items[1].trim();
                }
                if (items[0].startsWith("lambda")) {
                    Lambda = ArrayUtils.add(Lambda, Double.parseDouble(items[1].trim()));
                }
                if (items[0].startsWith("X")) {
                    int[] Xi = null;
                    String[] subItems = items[1].split(" ");
                    for (int j = 0; j < subItems.length; j++) {
                        Xi = ArrayUtils.add(Xi, Integer.parseInt(subItems[j]));
                    }
                    X = ArrayUtils.add(X, Xi);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        R = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            R.add(i);
        }
        System.out.println("inputFile : " + inputFileName + " \nq: " + q + " ;");
    }

    //@Test
    public void testGraphMPOnGrid(String file) {
        getProtestData(file);
        APDMInputFormat apdm = new APDMInputFormat(inputFileName);
        ArrayList<Integer[]> edges = apdm.data.intEdges;
        ArrayList<Double> edgeCosts = apdm.data.identityEdgeCosts;
        ProtestScanStat func = new ProtestScanStat(Lambda, X, R);
        int[] candidateS = new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        double optimalVal = -Double.MAX_VALUE;
        double bestPre = 0.0D;
        double bestRec = 0.0D;
        GraphMP bestGraphMP = null;
        for (int s : candidateS) {
            double B = s - 1 + 0.0D;
            int t = 5;
            /** for protest stat, we initialize X0 */
            double[] x0 = func.getX0();
            GraphMP graphMP = new GraphMP(edges, edgeCosts, x0, s, 1, B, t, true/** maximumCC */
                    , null/** TrueSubGraph */
                    , func, null);
            double[] yx = graphMP.x;
            if (func.getFuncValue(yx) > optimalVal) {
                optimalVal = func.getFuncValue(yx);
                int len = Utils.intersect(apdm.data.trueSubGraphNodes, graphMP.resultNodes_supportX).length;
                bestRec = len * 1.0D / apdm.data.trueSubGraphNodes.length * 1.0D;
                bestPre = len * 1.0D / graphMP.resultNodes_supportX.length * 1.0D;
                bestGraphMP = graphMP;
            }
        }
        System.out.println("precision : " + bestPre + " ; recall : " + bestRec);
        System.out.println("the subgraph is: "+ Arrays.toString(bestGraphMP.resultNodes_Tail));
    }

    @Test
    public void testCases() {
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.05.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.1.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.2.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.3.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.4.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.5.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.6.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.7.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.8.txt");
        new TestGraphMPOnGrid().testGraphMPOnGrid("./data/SimulationData/Protest/simulation_100_q_0.9.txt");
    }
}
