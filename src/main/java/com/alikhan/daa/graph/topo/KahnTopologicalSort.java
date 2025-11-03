package com.alikhan.daa.graph.topo;

import java.util.*;

public class KahnTopologicalSort {

    public List<Integer> topoSort(List<List<Integer>> graph) {
        int n = graph.size();
        int[] inDegree = new int[n];

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
        int visitedCount = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);
            visitedCount++;

            for (int v : graph.get(u)) {
                if (--inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        if (visitedCount != n) {
            System.out.println("The graph contains a cycle. Topological sorting is not possible.");
            return new ArrayList<>();
        }

        return topoOrder;
    }
}