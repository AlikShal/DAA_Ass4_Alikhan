
import com.alikhan.daa.graph.topo.KahnTopologicalSort;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class KahnTopologicalSortTest {

    @Test
    public void testTopologicalSort() {
        // Пример графа с 4 вершинами (DAG)
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(Arrays.asList(1, 2));  // 0 -> 1, 0 -> 2
        graph.add(Arrays.asList(3));     // 1 -> 3
        graph.add(Arrays.asList(3));     // 2 -> 3
        graph.add(new ArrayList<>());    // 3

        KahnTopologicalSort topoSort = new KahnTopologicalSort();
        List<Integer> topoOrder = topoSort.topoSort(graph);

        // Проверка, что топологический порядок корректен
        assertEquals(Arrays.asList(0, 1, 2, 3), topoOrder);
    }

    @Test
    public void testCyclicGraph() {
        // Граф с циклом
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(Arrays.asList(1));  // 0 -> 1
        graph.add(Arrays.asList(2));  // 1 -> 2
        graph.add(Arrays.asList(0));  // 2 -> 0 (Цикл)

        KahnTopologicalSort topoSort = new KahnTopologicalSort();
        List<Integer> topoOrder = topoSort.topoSort(graph);

        // Проверка, что порядок пустой (граф цикличен)
        assertTrue(topoOrder.isEmpty());
    }
}
