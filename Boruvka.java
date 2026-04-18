import java.util.Arrays;

public class Boruvka {
    private int countIteration;      // счетчик итераций
    private int vertNum;             // кол-во вершин
    private int edgeNum;             // кол-во рёбер
    private  Edge[] edges;           // массив рёбер графа
    private Subset[] subsets;        // массив компонент связности
    private int[] lowPriceNotes;     // массив для хранения индексов самых дешевых рёбер для каждой компоненты
    private long lastRunTimeMs;      // время последнего запуска алгоритма
    private int lastMSTWeight;       // вес MST для последнего запуска

    public void start(Graph graph){

        vertNum = graph.getVertNum();
        edgeNum = graph.getEdgeNum();
        edges = graph.getEdges();
    
        subsets = new Subset[vertNum]; // На первом шаге кол-во компонент = кол-ву вершин
        lowPriceNotes = new int[vertNum];

        
        for (int i = 0; i < vertNum; i++) {  // На старте каждая вершина находится в своей собственной компоненте (отдельном дереве)
            subsets[i] = new Subset();
            subsets[i].setParent(i);         // Родителем компоненты является сама вершина
            subsets[i].setRank(0);     // Дерево из одной вершины имеет нулевую высоту
            lowPriceNotes[i] = -1;          // Для каждой компоненты пока не найдено минимальное ребро
        }

        boruvkaAlg();
    }

    public int getCountIteration() {
        return countIteration;
    }

    public long getLastRunTimeMs() {
        return lastRunTimeMs;
    }

    public int getLastMSTWeight() {
        return lastMSTWeight;
    }

    public void boruvkaAlg() {
        int numTree = vertNum;      // Число деревьев равно числу вершин
        int MSTweight = 0;
        countIteration = 0;

        long before = System.currentTimeMillis();

        while (numTree > 1) { // Алгоритм работает, пока не останется одна компонента связности
            
            countIteration++;
            Arrays.fill(lowPriceNotes, -1); // Обнуляем “лучшие ребра” для всех компонент

            for (int i = 0; i < edgeNum; i++) { // Перебираем все ребра, чтобы найти самое минимальное
                countIteration++;

                // Проверяем, принадлежат ли вершины ребра разным компонентам,
                // то есть соединяет ли данное ребро две компоненты связности
                int set1 = find(subsets, edges[i].getSrc()); // Находим корень компоненты для src и dest
                int set2 = find(subsets, edges[i].getDest());

                // Проверяем, что ребро не внутреннее для одной компоненты
                if (set1 != set2) {

                    // Если для компоненты еще нет кандидата или текущий кандидат тяжелее, чем текущее ребро
                    if (lowPriceNotes[set1] == -1 || edges[lowPriceNotes[set1]].getWeight() > edges[i].getWeight()) {
                        lowPriceNotes[set1] = i;
                    }

                    if (lowPriceNotes[set2] == -1 || edges[lowPriceNotes[set2]].getWeight() > edges[i].getWeight()) {
                        lowPriceNotes[set2] = i;
                    }
                }
            }

            // Добавляем наименьшие ребра, полученные выше, в мин остов дерево
            for (int j = 0; j < vertNum; j++) {
                countIteration++;

                // Проверяем, существует ли минимальное исходящее ребро для компоненты
                if (lowPriceNotes[j] != -1) {

                    int set1 = find(subsets, edges[lowPriceNotes[j]].getSrc());
                    int set2 = find(subsets, edges[lowPriceNotes[j]].getDest());

                    if (set1 != set2) {
                        MSTweight += edges[lowPriceNotes[j]].getWeight();
                        uniteSubsets(subsets, set1, set2); // Объединяем две компоненты в одну
                        numTree--; 
                    }
                }
            }
        }

        long after = System.currentTimeMillis();

        lastRunTimeMs = after - before;
        lastMSTWeight = MSTweight;

        System.out.println("Final weight of MST :" + MSTweight);
    }

    // Когда нашли корень => переписываем parent у промежуточных вершин так, чтобы они указывали прямо на корень
    // То есть укорачиваем цепочку
    private int find(Subset[] subsets, int vert) {
        if (subsets[vert].getParent() != vert) {
            subsets[vert].setParent(find(subsets, subsets[vert].getParent())); // Идем до корня рекурсивно и при этом переписываем parent у всех вершин на корень
        }
        return subsets[vert].getParent();
    }

    // Метод объединения компонент, использует ранг для выбора родителя
    // Rank это оценка высоты дерева компоненты
    // Меньший ранк подвешиваем к большему, чтобы не увеличивать высоту дерева, потому что длинные цепочки замедляют find
    private void uniteSubsets(Subset[] subsets, int v1, int v2) {

        if (subsets[v1].getRank() < subsets[v2].getRank()) {
            subsets[v1].setParent(subsets[v2].getParent());

        } else if (subsets[v1].getRank() > subsets[v2].getRank()) {
            subsets[v2].setParent(subsets[v1].getParent());

        } else {
            subsets[v2].setParent(subsets[v1].getParent());
            subsets[v1].setRank(subsets[v1].getRank() + 1);
        }
    }
}