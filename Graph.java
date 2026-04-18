import java.util.List;

// Граф
public class Graph {

    private int vertNum;        // кол-во вершин
    private int edgeNum;        // кол-во рёбер
    private Edge[] edges;       // массив рёбер графа



    public Graph(int vertNum, int edgeNum){
        this.vertNum = vertNum;
        this.edgeNum = edgeNum;
    }

    @Override
    public String toString() {
        return "Graph{" +
                "vertNum=" + vertNum +
                "\nedgeNum=" + edgeNum +
                "\nedges=" + java.util.Arrays.toString(edges) +
                '}';
    }

    public Graph(){

    }

    public void setVertNum(int vertNum) {
        this.vertNum = vertNum;
    }

    public void setEdgeNum(int edgeNum) {
        this.edgeNum = edgeNum;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges.toArray(new Edge[0]);
    }

    public Edge[] getEdges() {
        return edges;
    }

    public int getVertNum() {
        return vertNum;
    }

    public int getEdgeNum() {
        return edgeNum;
    }
}