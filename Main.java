import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    private static final String TESTS_FILE = "tests.txt";       // Где лежат заранее сгенерированные тесты
    private static final String METRICS_FILE = "metrics.csv";   // Куда пишем таблицу для графиков
    private static final int TEST_COUNT = 80;
    private static final int MIN_VERTICES = 100;
    private static final int MAX_VERTICES = 10_000;
    private static final long RANDOM_SEED = 42L;

    public static void main(String[] args) throws IOException {
        File testsFile = new File(TESTS_FILE);
        ensureTestsFileExists(testsFile);

        List<Graph> graphs = readTests(testsFile);
        writeBenchmarkMetrics(new File(METRICS_FILE), graphs);

        System.out.println("Tests loaded: " + graphs.size());
        System.out.println("Metrics exported to: " + METRICS_FILE);
    }

    private static void ensureTestsFileExists(File testsFile) throws IOException {
        if (!testsFile.exists()) {
            generateTestsToFile(testsFile, TEST_COUNT, MIN_VERTICES, MAX_VERTICES, RANDOM_SEED);
        }
    }

    private static void writeBenchmarkMetrics(File metricsFile, List<Graph> graphs) throws IOException {
        Boruvka boruvka = new Boruvka();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(metricsFile), StandardCharsets.UTF_8))) {
            writer.write("test_id,vertices,edges,iterations,time_ms,mst_weight");
            writer.newLine();

            int testId = 1;
            for (Graph graph : graphs) {
                boruvka.start(graph);

                writer.write(
                        testId + "," +
                        graph.getVertNum() + "," +
                        graph.getEdgeNum() + "," +
                        boruvka.getCountIteration() + "," +
                        boruvka.getLastRunTimeMs() + "," +
                        boruvka.getLastMSTWeight()
                );
                writer.newLine();
                testId++;
            }
        }
    }

    private static Graph generateGraph(Random random, int countVert) {
        List<Edge> edges = new ArrayList<>();

        // Сначала строим случайное остовное дерево
        for (int v = 1; v < countVert; v++) {
            int parent = random.nextInt(v);
            int weight = random.nextInt(1, 101);
            edges.add(new Edge(v, parent, weight));
        }

        // Добавляем дополнительные случайные ребра до целевого размера
        int extraEdges = countVert * random.nextInt(2, 7); // от 2 до 6 дополнительных рёбер на вершину
        int targetEdgeCount = Math.min((countVert * (countVert - 1)) / 2, (countVert - 1) + extraEdges); // n-1 ребро уже есть в остовном дереве, максимум - n(n-1)/2 для неор графа

        Set<String> usedUndirectedEdges = new HashSet<>();
        for (Edge edge : edges) {
            usedUndirectedEdges.add(edgeKey(edge.getSrc(), edge.getDest()));
        }

        while (edges.size() < targetEdgeCount) {
            int src = random.nextInt(countVert);
            int dest = random.nextInt(countVert);
            if (src == dest) {
                continue;
            }

            String key = edgeKey(src, dest);
            if (usedUndirectedEdges.contains(key)) {
                continue;
            }

            usedUndirectedEdges.add(key);
            int weight = random.nextInt(1, 101);
            edges.add(new Edge(src, dest, weight));
        }

        Graph graph = new Graph(countVert, edges.size());
        graph.setEdges(edges);
        return graph;
    }

    private static void generateTestsToFile(File file, int testCount, int minVertices, int maxVertices, long seed) throws IOException {
        Random random = new Random(seed);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (int i = 1; i <= testCount; i++) {
                int vertices = random.nextInt(minVertices, maxVertices + 1);
                Graph graph = generateGraph(random, vertices);

                writer.write("#" + i + ";" + graph.getVertNum() + ";" + graph.getEdgeNum());
                writer.newLine();

                for (Edge edge : graph.getEdges()) {
                    writer.write(edge.getSrc() + "-" + edge.getDest() + "-" + edge.getWeight());
                    writer.newLine();
                }
            }
        }
    }

    private static List<Graph> readTests(File file) throws IOException {
        List<Graph> graphs = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Graph currentGraph = null;

        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                String value = scanner.nextLine();
                if (value.isBlank()) {
                    continue;
                }

                if (value.startsWith("#")) {
                    if (currentGraph != null) {
                        currentGraph.setEdges(edges);
                        graphs.add(currentGraph);
                    }

                    String[] data = value.split(";");
                    if (data.length != 3) {
                        throw new IllegalArgumentException("Неверный заголовок теста: " + value);
                    }

                    currentGraph = new Graph();
                    edges = new ArrayList<>();
                    currentGraph.setVertNum(Integer.parseInt(data[1]));
                    currentGraph.setEdgeNum(Integer.parseInt(data[2]));
                } else {
                    String[] data = value.split("-");
                    if (data.length != 3) {
                        throw new IllegalArgumentException("Неверная строка ребра: " + value);
                    }
                    edges.add(new Edge(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2])));
                }
            }
        }

        if (currentGraph != null) {
            currentGraph.setEdges(edges);
            graphs.add(currentGraph);
        }

        return graphs;
    }

    private static String edgeKey(int a, int b) {
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        return min + ":" + max;
    }
}
