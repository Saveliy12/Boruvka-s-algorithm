// Компонента связности графа
public class Subset {

    private int parent;     // номер родителя компоненты связности
    private int rank;       // ранг комнпоненты связности

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Subset{" +
                "parent=" + parent +
                "\nrank=" + rank +
                '}';
    }
}
