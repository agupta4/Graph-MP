package edu.albany.cs.base;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class CC {

    private boolean[] marked;
    private int[][] adj;

    public ArrayList<HashSet<Integer>> cc;

    public CC(int[][] adj) {
        this.adj = adj;
        marked = new boolean[adj.length];
    }

    public HashSet<Integer> findLargestCC(int[] A) {
        int size = -1;
        HashSet<Integer> best = null;
        ArrayList<HashSet<Integer>> ccs = findCCs(A);
        for (HashSet<Integer> ss : ccs) {
            if (ss.size() > size) {
                size = ss.size();
                best = ss;
            }
        }
        return best;
    }

    public ArrayList<HashSet<Integer>> findCCs(int[] A) {
        int N = A.length;
        ArrayList<HashSet<Integer>> cc = new ArrayList<HashSet<Integer>>();
        int total = 0;
        HashSet<Integer> setA = new HashSet<Integer>();
        for (int i : A) {
            setA.add(i);
        }
        while (true) {

            if (total == N) {
                break;
            }

            HashSet<Integer> currentCC = breathFirstSearch(setA, A[0]);
            total += currentCC.size();
            cc.add(currentCC);

            HashSet<Integer> updateA = new HashSet<Integer>();
            for (int i : setA) {
                if (!currentCC.contains(i)) {
                    updateA.add(i);
                }
            }
            setA = updateA;
            A = null;
            for (int i : setA) {
                A = ArrayUtils.add(A, i);
            }


        }
        return cc;
    }

    private HashSet<Integer> breathFirstSearch(HashSet<Integer> setA, int s) {
        return bfs(adj, s, setA);
    }

    // breadth-first search from a single source
    private HashSet<Integer> bfs(int[][] adj, int s, HashSet<Integer> setA) {
        marked = new boolean[adj.length];
        HashSet<Integer> result = new HashSet<Integer>();
        PriorityQueue<Integer> q = new PriorityQueue<Integer>();
        marked[s] = true;
        result.add(s);
        q.add(s);
        while (!q.isEmpty()) {
            int v = q.remove();
            for (int w : adj[v]) {
                if (!marked[w] && setA.contains(w)) {
                    marked[w] = true;
                    result.add(s);
                    q.add(w);
                }
            }
        }
        return result;
    }

    public static void main(String args[]) {
        APDMInputFormat apdm = new APDMInputFormat(new File("./data/CrimeOfChicago/graph/APDM-2011.txt"));
        CC cc = new CC(apdm.data.graphAdj);
        int[] A = new int[40000];
        long time = System.nanoTime();
        System.out.println(cc.findLargestCC(A).size());
        System.out.println("time : " + (System.nanoTime() - time) / 1e9);
    }
}
