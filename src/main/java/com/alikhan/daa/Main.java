package com.alikhan.daa;

import com.alikhan.daa.graph.scc.TarjanSCC;
import com.alikhan.daa.graph.topo.KahnTopologicalSort;
import com.alikhan.daa.graph.dagsp.DagShortestPaths;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Путь к файлу tasks.json (папка data находится на том же уровне, что и src)
        String filePath = "data/tasks.json";  // Путь относительно корня проекта

        try {
            // Загружаем граф из файла
            List<List<Integer>> graph = loadGraph(filePath);

            // 1. Поиск сильно связанных компонент
            TarjanSCC tarjanSCC = new TarjanSCC();
            List<List<Integer>> sccs = tarjanSCC.findSCCs(graph);
            System.out.println("Сильно связанные компоненты: " + sccs);

            // 2. Топологическая сортировка
            KahnTopologicalSort topoSort = new KahnTopologicalSort();
            List<Integer> topoOrder = topoSort.topoSort(graph);
            System.out.println("Топологический порядок: " + topoOrder);

            // 3. Кратчайшие пути
            DagShortestPaths dagShortestPaths = new DagShortestPaths();
            int[] shortestPaths = dagShortestPaths.shortestPaths(graph, 0);
            System.out.println("Кратчайшие пути от вершины 0: " + Arrays.toString(shortestPaths));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    // Метод для загрузки графа из JSON файла
    public static List<List<Integer>> loadGraph(String filePath) throws IOException {
        FileReader reader = new FileReader(filePath);
        StringBuilder jsonStr = new StringBuilder();
        int ch;
        while ((ch = reader.read()) != -1) {
            jsonStr.append((char) ch);
        }
        reader.close();

        JSONObject obj = new JSONObject(jsonStr.toString());
        int n = obj.getInt("n");
        List<List<Integer>> graph = new ArrayList<>();

        // Инициализация списка смежности
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // Добавление рёбер в граф
        JSONArray edges = obj.getJSONArray("edges");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            int u = edge.getInt("u");
            int v = edge.getInt("v");
            graph.get(u).add(v);
        }

        return graph;
    }
}
