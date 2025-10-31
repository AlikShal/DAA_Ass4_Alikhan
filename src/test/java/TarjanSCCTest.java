
import com.alikhan.daa.graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TarjanSCCTest {

    @Test
    public void testSCC() {
        // Граф с 3 вершинами, где все вершины образуют один сильно связанный компонент
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(Arrays.asList(1));  // 0 -> 1
        graph.add(Arrays.asList(2));  // 1 -> 2
        graph.add(Arrays.asList(0));  // 2 -> 0

        TarjanSCC tarjanSCC = new TarjanSCC();
        List<List<Integer>> sccs = tarjanSCC.findSCCs(graph);

        // Проверка, что есть один SCC, который включает все вершины
        assertEquals(1, sccs.size());
        assertTrue(sccs.get(0).containsAll(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void testMultipleSCCs() {
        // Граф с двумя SCC
        List<List<Integer>> graph = new ArrayList<>();
        graph.add(Arrays.asList(1));  // 0 -> 1
        graph.add(Arrays.asList(2));  // 1 -> 2
        graph.add(Arrays.asList(0));  // 2 -> 0
        graph.add(Arrays.asList(4));  // 3 -> 4
        graph.add(Arrays.asList(3));  // 4 -> 3

        TarjanSCC tarjanSCC = new TarjanSCC();
        List<List<Integer>> sccs = tarjanSCC.findSCCs(graph);

        // Ожидаем два SCC: одно для (0, 1, 2) и другое для (3, 4)
        assertEquals(2, sccs.size());
        assertTrue(sccs.get(0).containsAll(Arrays.asList(0, 1, 2)));
        assertTrue(sccs.get(1).containsAll(Arrays.asList(3, 4)));
    }
}
