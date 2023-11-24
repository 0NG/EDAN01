import java.util.ArrayList;
import java.util.List;

import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.constraints.netflow.*;
import org.jacop.constraints.netflow.simplex.*;
import org.jacop.search.*;

public class task2 {
    public static void search(int graphSize, int start, int[] dests, int[] cities1, int[] cities2, int[] costs) {
        Store store = new Store();

        int nRoads = cities1.length;
        List<IntVar> allVars = new ArrayList<IntVar>(nRoads * 3);

        NetworkBuilder net = new NetworkBuilder();
        Node[] v = new Node[graphSize]; // 6 node in total

        int flowSize = dests.length;
        v[start - 1] = net.addNode("s", flowSize);
        for (int d : dests)
            v[d - 1] = net.addNode("t" + (d - 1), -1);
        for (int i = 0; i < graphSize; ++i)
            if (v[i] == null)
                v[i] = net.addNode("v" + i, 0);

        IntVar[] cap = new IntVar[nRoads]; // flow in each edge
        IntVar[] isTaken = new IntVar[nRoads]; // indicate if there is a flow in each edge
        for (int i = 0; i < nRoads; ++i) {
            cap[i] = new IntVar(store, "cap_" + i, -flowSize, flowSize); // undirected
            allVars.add(cap[i]);

            isTaken[i] = new IntVar(store, 0, 1);
            allVars.add(isTaken[i]);

            store.impose(new Reified(new XneqC(cap[i], 0), isTaken[i])); // cap[i] != 0 <=> isTaken[i] == 1
        }

        for (int i = 0; i < nRoads; ++i)
            net.addArc(v[cities1[i] - 1], v[cities2[i] - 1], 0, cap[i]);

        int maxCost = 0;
        for (int i = 0; i < nRoads; ++i)
            if (costs[i] > maxCost)
                maxCost = costs[i];

        IntVar totalCost = new IntVar(store, "totalCost", 0, maxCost * nRoads);
        allVars.add(totalCost);

        store.impose(new LinearInt(isTaken, costs, "==", totalCost)); // sum of the cost of selected edges

        IntVar sumOfCap = new IntVar(store, 0, 0);
        net.setCostVariable(sumOfCap);

        store.impose(new NetworkFlow(net));

        Search<IntVar> search = new DepthFirstSearch<IntVar>();

        IntVar[] freeVars = new IntVar[allVars.size()];
        allVars.toArray(freeVars);

        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(freeVars, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>()); 
        boolean result = search.labeling(store, select, totalCost);

        if (result) {
            System.out.print("Solution: ");
            for (int i = 0; i < nRoads; ++i)
                System.out.print(cap[i] + ", " );
            System.out.println("");
            System.out.println(totalCost);
        } else {
            System.out.println("!Infeasible!");
            System.out.println(store);
        }
    }

    public static void main(String[] args) {
        System.out.println("========== Data 1 ==========");
        search(6,
               1,
               new int[]{6},
               new int[]{1, 1, 2,  2, 3, 4,  4},
               new int[]{2, 3, 3,  4, 5, 5,  6},
               new int[]{4, 2, 5, 10, 3, 4, 11}
        );

        System.out.println("========== Data 2 ==========");
        search(6,
               1,
               new int[]{5, 6},
               new int[]{1, 1, 2,  2, 3, 4,  4},
               new int[]{2, 3, 3,  4, 5, 5,  6},
               new int[]{4, 2, 5, 10, 3, 4, 11}
        );

        System.out.println("========== Data 3 ==========");
        search(6,
               1,
               new int[]{5, 6},
               new int[]{1, 1, 1, 2, 2, 3, 3, 3, 4},
               new int[]{2, 3, 4, 3, 5, 4, 5, 6, 6},
               new int[]{6, 1, 5, 5, 3, 5, 6, 4, 2}
        );
    }
}
