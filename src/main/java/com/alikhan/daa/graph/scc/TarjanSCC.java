package com.alikhan.daa.graph.scc;

import java.util.*;

public class TarjanSCC {
    private int time = 0;
    private List<List<Integer>> sccs;

    public List<List<Integer>> findSCCs(List<List<Integer>> graph) {
        int n = graph.size();
        int[] low = new int[n];
        int[] disc = new int[n];
        boolean[] inStack = new boolean[n];
        Stack<Integer> stack = new Stack<>();
        sccs = new ArrayList<>();

        Arrays.fill(disc, -1);

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, graph, low, disc, inStack, stack);
            }
        }
        return sccs;
    }

    private void dfs(int u, List<List<Integer>> graph, int[] low, int[] disc, boolean[] inStack, Stack<Integer> stack) {
        disc[u] = low[u] = ++time;
        stack.push(u);
        inStack[u] = true;

        for (int v : graph.get(u)) {
            if (disc[v] == -1) {
                dfs(v, graph, low, disc, inStack, stack);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            while (stack.peek() != u) {
                scc.add(stack.pop());
                inStack[stack.peek()] = false;
            }
            scc.add(stack.pop());
            inStack[u] = false;
            sccs.add(scc);
        }
    }
}
