// Ребро графа
public class Edge {

    private int src;        // номер вершины, от которой исходит ребро
    private int dest;       // номер вершины, к которой идет ребро
    private int weight;     // вес ребра


    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "src=" + src +
                "\ndest=" + dest +
                "\nweight=" + weight +
                '}';
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}