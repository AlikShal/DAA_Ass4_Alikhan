package com.alikhan.daa.graph.dagsp;

import java.util.*;

public class DagShortestPaths {

    public int[] shortestPaths(List<List<Integer>> graph, int source) {
        int n = graph.size();
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        List<Integer> topoOrder = topologicalSort(graph);

        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (int v : graph.get(u)) {
                    dist[v] = Math.min(dist[v], dist[u] + 1);
                }
            }
        }

        return dist;
    }

    private List<Integer> topologicalSort(List<List<Integer>> graph) {
        int n = graph.size();
        int[] inDegree = new int[n];

        // Заполняем inDegree для всех вершин
        for (List<Integer> neighbors : graph) {
            for (int neighbor : neighbors) {
                inDegree[neighbor]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);

            for (int v : graph.get(u)) {
                if (--inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        return topoOrder;
    }
}
