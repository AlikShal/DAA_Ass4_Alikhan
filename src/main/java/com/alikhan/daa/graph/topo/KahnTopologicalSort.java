package com.alikhan.daa.graph.topo;

import java.util.*;

public class KahnTopologicalSort {

    public List<Integer> topoSort(List<List<Integer>> graph) {
        int n = graph.size();
        int[] inDegree = new int[n];

        // Заполняем inDegree для всех вершин
        for (List<Integer> neighbors : graph) {
            for (int neighbor : neighbors) {
                inDegree[neighbor]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        // Добавляем все вершины с inDegree == 0
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

        // Если количество посещённых вершин не равно числу всех вершин, значит есть цикл
        if (visitedCount != n) {
            System.out.println("Граф содержит цикл. Топологическая сортировка невозможна.");
            return new ArrayList<>();
        }

        return topoOrder;
    }
}