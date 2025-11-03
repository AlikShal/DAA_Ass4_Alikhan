package com.alikhan.daa.graph.dagsp;

import com.alikhan.daa.graph.topo.KahnTopologicalSort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Longest paths in a DAG from a single source.
 * Weighted adjacency format: List<List<int[]>> with each edge as {to, w}.
 * Assumes non-negative weights.
 */
public final class DagLongestPath {


    private static final int NEG_INF = Integer.MIN_VALUE / 4;

    /** Plain container (Java 8+ friendly) */
    public static final class Result {
        private final int[] dist;
        private final int[] parent;
        public Result(int[] dist, int[] parent) { this.dist = dist; this.parent = parent; }
        public int[] dist()   { return dist; }
        public int[] parent() { return parent; }
    }

    /** Compute Longest paths using internal Kahn topo. If cyclic → all NEG_INF except src=0. */
    public int[] longestPaths(List<List<int[]>> weighted, int src) {
        List<List<Integer>> unweighted = toUnweighted(weighted);
        List<Integer> topo = new KahnTopologicalSort().topoSort(unweighted);
        if (topo.size() != weighted.size()) {
            int[] dist = new int[weighted.size()];
            Arrays.fill(dist, NEG_INF);
            if (0 <= src && src < dist.length) dist[src] = 0;
            return dist;
        }
        return longestPaths(weighted, src, topo);
    }

    /** Compute Longest paths using provided topo order (size==n). */
    public int[] longestPaths(List<List<int[]>> weighted, int src, List<Integer> topo) {
        final int n = weighted.size();
        int[] dist = new int[n];
        Arrays.fill(dist, NEG_INF);
        if (0 <= src && src < n) dist[src] = 0;

        for (int u : topo) {
            if (dist[u] == NEG_INF) continue;
            for (int[] e : weighted.get(u)) {
                int v = e[0], w = e[1];
                long cand = (long) dist[u] + w;
                if (cand > dist[v]) dist[v] = (int) cand;
            }
        }
        return dist;
    }

    /** Same as above but also returns parents for path reconstruction. */
    public Result longestPathsWithParents(List<List<int[]>> weighted, int src, List<Integer> topo) {
        final int n = weighted.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, NEG_INF);
        Arrays.fill(parent, -1);
        if (0 <= src && src < n) dist[src] = 0;

        for (int u : topo) {
            if (dist[u] == NEG_INF) continue;
            for (int[] e : weighted.get(u)) {
                int v = e[0], w = e[1];
                long cand = (long) dist[u] + w;
                if (cand > dist[v]) {
                    dist[v] = (int) cand;
                    parent[v] = u;
                }
            }
        }
        return new Result(dist, parent);
    }

    /** Reconstruct path src.t using a parent array (from longestPathsWithParents). */
    public static int[] reconstructPath(int t, int[] parent) {
        List<Integer> path = new ArrayList<>();
        for (int v = t; v != -1; v = parent[v]) path.add(v);
        Collections.reverse(path);
        int[] out = new int[path.size()];
        for (int i = 0; i < path.size(); i++) out[i] = path.get(i);
        return out;
    }

    private static List<List<Integer>> toUnweighted(List<List<int[]>> wAdj) {
        int n = wAdj.size();
        List<List<Integer>> g = new ArrayList<>(n);
        for (int i = 0; i < n; i++) g.add(new ArrayList<Integer>());
        for (int u = 0; u < n; u++) {
            for (int[] e : wAdj.get(u)) g.get(u).add(e[0]);
        }
        return g;
    }
}
