package com.alikhan.daa;

import com.alikhan.daa.graph.topo.KahnTopologicalSort;
import com.alikhan.daa.graph.dagsp.DagShortestPaths;
import com.alikhan.daa.graph.dagsp.DagLongestPath;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Main {


    private static final String CSV_PATH = "data/metrics.csv";
    private static final String CSV_HEADER =
            "file,vertices,edges," +
                    "Tarjan_SCC_count,Tarjan_time_ns,Tarjan_DFS_ops," +
                    "Kahn_time_ns,Kahn_queue_ops," +
                    "DAGSP_short_time_ns,DAGSP_short_relax_ops," +
                    "DAGSP_long_time_ns,DAGSP_long_relax_ops,DAGSP_long_max\n";

    public static void main(String[] args) {
        String dataDir = "data";
        String[] datasets = {
                "small_1.json","small_2.json","small_3.json",
                "medium_1.json","medium_2.json","medium_3.json",
                "large_1.json","large_2.json","large_3.json"
        };

        System.out.println("Working dir: " + Paths.get("").toAbsolutePath());
        overwriteHeader();

        for (String file : datasets) {
            Path p = Paths.get(dataDir, file);
            System.out.println("\n=== Processing: " + p + " ===");

            try {
                JSONObject obj = readJsonObject(p);
                Graph gw = buildGraph(obj);
                List<List<Integer>> g = gw.unweighted;
                int n = g.size();
                int m = edgeCount(g);

                TarjanCounters tc = new TarjanCounters();
                long t0 = System.nanoTime();
                List<List<Integer>> sccs = tarjan(g, tc);
                long t1 = System.nanoTime();
                long tarjanNs = t1 - t0;

                KahnCounters kc = new KahnCounters();
                long k0 = System.nanoTime();
                List<Integer> topo = kahn(g, kc);
                long k1 = System.nanoTime();
                long kahnNs = k1 - k0;
                boolean isDag = topo.size() == n;

                DagShortestPaths sp = new DagShortestPaths();
                SpCounters scShort = new SpCounters();
                long spShortNs = 0;
                if (isDag) {
                    long s0 = System.nanoTime();
                    dagShortestWithCounters(gw.weighted, topo, 0, sp, scShort);
                    long s1 = System.nanoTime();
                    spShortNs = s1 - s0;
                }

                DagLongestPath lp = new DagLongestPath();
                SpCounters scLong = new SpCounters();
                long spLongNs = 0;
                int longMax = 0;
                if (isDag) {
                    long l0 = System.nanoTime();
                    int[] longest = dagLongestWithCounters(gw.weighted, topo, 0, lp, scLong);
                    long l1 = System.nanoTime();
                    spLongNs = l1 - l0;
                    longMax = maxFinite(longest);
                }

                writeRow(
                        file, n, m,
                        sccs.size(), tarjanNs, tc.dfsVisits,
                        kahnNs, (kc.pushes + kc.pops),
                        (isDag ? spShortNs : 0), (isDag ? scShort.relaxOk : 0),
                        (isDag ? spLongNs  : 0), (isDag ? scLong.relaxOk  : 0),
                        (isDag ? longMax   : 0)
                );

                System.out.println("SCC: " + sccs.size() +
                        " | Topo size: " + topo.size() +
                        (isDag ? " (DAG)" : " (CYCLIC)")
                );

            } catch (Exception e) {
                System.err.println("Skip " + file + ": " + e.getMessage());
            }
        }
    }

    private static class TarjanCounters { long dfsVisits = 0, dfsEdges = 0; }
    private static class KahnCounters   { long pushes = 0, pops = 0; }
    private static class SpCounters     { long relaxOk = 0; }

    private static List<List<Integer>> tarjan(List<List<Integer>> g, TarjanCounters c) {
        int n = g.size();
        int[] idx = new int[n];
        int[] low = new int[n];
        boolean[] onStack = new boolean[n];
        Arrays.fill(idx, -1);
        Deque<Integer> st = new ArrayDeque<>();
        int[] time = {0};
        List<List<Integer>> comps = new ArrayList<>();

        for (int v = 0; v < n; v++)
            if (idx[v] == -1)
                tarjanDfs(v, g, c, idx, low, onStack, st, time, comps);

        return comps;
    }

    private static void tarjanDfs(int v, List<List<Integer>> g, TarjanCounters c,
                                  int[] idx, int[] low, boolean[] onStack,
                                  Deque<Integer> st, int[] time, List<List<Integer>> comps) {
        c.dfsVisits++;
        idx[v] = low[v] = time[0]++;
        st.push(v);
        onStack[v] = true;

        for (int to : g.get(v)) {
            c.dfsEdges++;
            if (idx[to] == -1) {
                tarjanDfs(to, g, c, idx, low, onStack, st, time, comps);
                low[v] = Math.min(low[v], low[to]);
            } else if (onStack[to]) {
                low[v] = Math.min(low[v], idx[to]);
            }
        }
        if (low[v] == idx[v]) {
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int x = st.pop();
                onStack[x] = false;
                comp.add(x);
                if (x == v) break;
            }
            comps.add(comp);
        }
    }

    private static List<Integer> kahn(List<List<Integer>> g, KahnCounters c) {
        int n = g.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++)
            for (int v : g.get(u)) indeg[v]++;

        Deque<Integer> q = new ArrayDeque<>();
        for (int v = 0; v < n; v++)
            if (indeg[v] == 0) { q.add(v); c.pushes++; }

        List<Integer> order = new ArrayList<>(n);
        while (!q.isEmpty()) {
            int u = q.remove(); c.pops++;
            order.add(u);
            for (int v : g.get(u)) {
                if (--indeg[v] == 0) { q.add(v); c.pushes++; }
            }
        }
        return order;
    }

    private static void dagShortestWithCounters(List<List<int[]>> wAdj, List<Integer> topo, int src,
                                                DagShortestPaths sp, SpCounters c) {
        final int INF = Integer.MAX_VALUE;
        int n = wAdj.size();
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] == INF) continue;
            for (int[] e : wAdj.get(u)) {
                int v = e[0], w = e[1];
                long cand = (long) dist[u] + w;
                if (cand < dist[v]) { dist[v] = (int) cand; c.relaxOk++; }
            }
        }
    }

    private static int[] dagLongestWithCounters(List<List<int[]>> wAdj, List<Integer> topo, int src,
                                                DagLongestPath lp, SpCounters c) {
        final int NEG_INF = Integer.MIN_VALUE / 4;
        int n = wAdj.size();
        int[] dist = new int[n];
        Arrays.fill(dist, NEG_INF);
        dist[src] = 0;

        for (int u : topo) {
            if (dist[u] == NEG_INF) continue;
            for (int[] e : wAdj.get(u)) {
                int v = e[0], w = e[1];
                long cand = (long) dist[u] + w;
                if (cand > dist[v]) { dist[v] = (int) cand; c.relaxOk++; }
            }
        }
        return dist;
    }

    private static final class Graph {
        final List<List<int[]>> weighted;
        final List<List<Integer>> unweighted;
        Graph(List<List<int[]>> w, List<List<Integer>> u) { weighted = w; unweighted = u; }
    }

    private static JSONObject readJsonObject(Path p) throws IOException {
        if (!Files.exists(p)) throw new IOException("file not found: " + p);
        String s = Files.readString(p, StandardCharsets.UTF_8);
        s = (!s.isEmpty() && s.charAt(0) == '\uFEFF') ? s.substring(1) : s;
        return new JSONObject(s.trim());
    }

    private static Graph buildGraph(JSONObject obj) {
        int n = obj.getInt("n");
        List<List<int[]>> gW = new ArrayList<>(n);
        List<List<Integer>> g = new ArrayList<>(n);
        for (int i = 0; i < n; i++) { gW.add(new ArrayList<>()); g.add(new ArrayList<>()); }
        JSONArray edges = obj.getJSONArray("edges");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject e = edges.getJSONObject(i);
            int u = e.getInt("u"), v = e.getInt("v");
            int w = e.has("w") ? e.getInt("w") : 1;
            gW.get(u).add(new int[]{v, w});
            g.get(u).add(v);
        }
        return new Graph(gW, g);
    }

    private static int edgeCount(List<List<Integer>> g) {
        int s = 0; for (List<Integer> nb : g) s += nb.size(); return s;
    }

    private static int maxFinite(int[] a) {
        int INF = Integer.MAX_VALUE;
        int mx = 0;
        for (int v : a) if (v != INF) mx = Math.max(mx, v);
        return mx;
    }

    private static void overwriteHeader() {
        try {
            Path p = Paths.get(CSV_PATH);
            Files.createDirectories(p.getParent());
            Files.writeString(
                    p,
                    CSV_HEADER,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            System.err.println("CSV header init failed: " + e.getMessage());
        }
    }

    private static void writeRow(String file, int n, int m,
                                 int tarjanScc, long tarjanNs, long tarjanDfsOps,
                                 long kahnNs, long kahnQueueOps,
                                 long spShortNs, long spShortRelax,
                                 long spLongNs, long spLongRelax, int spLongMax) {
        String row = String.format(Locale.US,
                "%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d%n",
                file, n, m,
                tarjanScc, tarjanNs, tarjanDfsOps,
                kahnNs, kahnQueueOps,
                spShortNs, spShortRelax,
                spLongNs, spLongRelax, spLongMax
        );
        try {
            Files.writeString(Paths.get(CSV_PATH), row, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("CSV write failed: " + e.getMessage());
        }
    }
}
