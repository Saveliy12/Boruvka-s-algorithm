import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Вершина графа
class Note {

    private int num;            // номер вершины
    private Set<Edge> edges;    // множество рёбер, инцидентных данной вершине

    public Note(int num) {
        this.num = num;
        edges = new HashSet<>();
    }

    public int getNum() {
        return num;
    }

    public boolean addEdge(Edge edge){
        return edges.add(edge);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }
}