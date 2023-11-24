import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;

public class task1 {
    public static void max_satisfied(int n, int[][] prefs) {
        Store store = new Store();

        List<IntVar> allVars = new ArrayList<IntVar>(n * 3);
    
        IntVar[] people = new IntVar[n];
        for (int i = 0; i < n; ++i) {
            IntVar tmp = new IntVar(store, 1, n);
            people[i] = tmp;

            allVars.add(tmp);
        }
        store.impose(new Alldifferent(people));
    
        IntVar[] isTaken = new IntVar[prefs.length];
        for (int i = 0; i < prefs.length; ++i) {
            IntVar tmp = new IntVar(store, 0, 1);
            store.impose(new Reified(new Or(new PrimitiveConstraint[] {
                new XplusCeqZ(people[prefs[i][0] - 1], 1, people[prefs[i][1] - 1]),
                new XplusCeqZ(people[prefs[i][0] - 1], -1, people[prefs[i][1] - 1])
            }), tmp));
            isTaken[i] = tmp;
        }
    
        int[] toMin = new int[isTaken.length];
        Arrays.fill(toMin, -1);

        IntVar obj = new IntVar(store, -isTaken.length, 0);
        store.impose(new LinearInt(isTaken, toMin, "==", obj));
    
        Search<IntVar> search = new DepthFirstSearch<IntVar>();

        IntVar[] freeVars = new IntVar[allVars.size()];
        allVars.toArray(freeVars);

        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(freeVars, new MostConstrainedStatic<IntVar>(), new IndomainMiddle<IntVar>()); 
        boolean result = search.labeling(store, select, obj);

        if (result) {
            System.out.print("Solution: ");
            for (int i = 0; i < n; ++i)
                System.out.print(people[i] + ", " );
            for (int i = 0; i < prefs.length; ++i)
                System.out.print(isTaken[i] + ", " );
            System.out.println("");
            System.out.println(obj + " * -1");
        } else {
            System.out.println("!Infeasible!");
            System.out.println(store);
        }
    }

    public static void min_distance(int n, int[][] prefs) {
        Store store = new Store();

        List<IntVar> allVars = new ArrayList<IntVar>(n * 3);
    
        IntVar[] people = new IntVar[n];
        for (int i = 0; i < n; ++i) {
            IntVar tmp = new IntVar(store, 1, n);
            people[i] = tmp;

            allVars.add(tmp);
        }
        store.impose(new Alldifferent(people));
    
        IntVar[] distance = new IntVar[prefs.length];
        for (int i = 0; i < prefs.length; ++i) {
            IntVar d = new IntVar(store, 0, n);
            store.impose(new Distance(people[prefs[i][0] - 1], people[prefs[i][1] - 1], d));
            distance[i] = d;
        }

        IntVar maxD = new IntVar(store, 0, n);
        store.impose(new Max(distance, maxD));
    
        Search<IntVar> search = new DepthFirstSearch<IntVar>();

        IntVar[] freeVars = new IntVar[allVars.size()];
        allVars.toArray(freeVars);

        SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(freeVars, new MostConstrainedStatic<IntVar>(), new IndomainMiddle<IntVar>()); 
        boolean result = search.labeling(store, select, maxD);

        if (result) {
            System.out.print("Solution: ");
            for (int i = 0; i < n; ++i)
                System.out.print(people[i] + ", " );
            System.out.println("");
            System.out.println(maxD);
        } else {
            System.out.println("!Infeasible!");
            System.out.println(store);
        }
    }

    public static void main(String[] args) {
        System.out.println("========== Data 1 ==========");
        System.out.println("---------- Satisf ----------");
        max_satisfied(9,
                new int[][]{
                    {1,3}, {1,5}, {1,8}, {2,5}, {2,9}, {3,4}, {3,5}, {4,1}, {4,5},
                    {5,6}, {5,1}, {6,1}, {6,9}, {7,3}, {7,8}, {8,9}, {8,7}
                }
        );

        System.out.println("---------- Distan ----------");
        min_distance(9,
                new int[][]{
                    {1,3}, {1,5}, {1,8}, {2,5}, {2,9}, {3,4}, {3,5}, {4,1}, {4,5},
                    {5,6}, {5,1}, {6,1}, {6,9}, {7,3}, {7,8}, {8,9}, {8,7}
                }
        );

        System.out.println("========== Data 2 ==========");
        System.out.println("---------- Satisf ----------");
        max_satisfied(15,
                new int[][]{
                    {1,3}, {1,5},  {2,5}, {2,8}, {2,9}, {3,4}, {3,5}, {4,1},  {4,15}, {4,13},
                    {5,1}, {6,10}, {6,9}, {7,3}, {7,5}, {8,9}, {8,7}, {8,14}, {9,13}, {10,11}
                }
        );

        System.out.println("---------- Distan ----------");
        min_distance(15,
                new int[][]{
                    {1,3}, {1,5},  {2,5}, {2,8}, {2,9}, {3,4}, {3,5}, {4,1},  {4,15}, {4,13},
                    {5,1}, {6,10}, {6,9}, {7,3}, {7,5}, {8,9}, {8,7}, {8,14}, {9,13}, {10,11}
                }
        );
    }
}
