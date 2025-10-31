
import com.alikhan.daa.graph.dagsp.DagShortestPaths;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class DagShortestPathsTest {

    @Test
    public void testShortestPaths() {
        // Пример графа с 4 вершинами
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(Arrays.asList(1, 2));  // 0 -> 1, 0 -> 2
        graph.add(Arrays.asList(3));     // 1 -> 3
        graph.add(Arrays.asList(3));     // 2 -> 3
        graph.add(new ArrayList<>());    // 3

        DagShortestPaths dagShortestPaths = new DagShortestPaths();
        int[] shortestPaths = dagShortestPaths.shortestPaths(graph, 0);

        // Проверка кратчайших путей от вершины 0
        assertArrayEquals(new int[]{0, 1, 1, 2}, shortestPaths);
    }

    @Test
    public void testDisconnectedGraph() {
        // Пример графа с раздельными компонентами
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(Arrays.asList(1)); // 0 -> 1
        graph.add(new ArrayList<>()); // 1
        graph.add(new ArrayList<>()); // 2
        graph.add(new ArrayList<>()); // 3

        DagShortestPaths dagShortestPaths = new DagShortestPaths();
        int[] shortestPaths = dagShortestPaths.shortestPaths(graph, 0);

        // Для вершины 2 и 3 путь будет равен Integer.MAX_VALUE, так как они не достижимы
        assertArrayEquals(new int[]{0, 1, Integer.MAX_VALUE, Integer.MAX_VALUE}, shortestPaths);
    }
}
