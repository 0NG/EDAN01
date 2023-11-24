import java.util.ArrayList;
import java.util.List;

import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.search.*;

public class task5 {

    public static void search(data para) {
        // negate the points to turn the problem into a minimization problem
        for (int i = 0; i < para.point_distribution.length; ++i) para.point_distribution[i] *= -1;

        Store store = new Store();
        List<IntVar> allVars = new ArrayList<IntVar>(para.n * para.n + 2 * para.n + 1);

        // 0-1 area where 1 for residential area and 0 for commercial area
        IntVar[] area = new IntVar[para.n * para.n];

        for (int i = 0; i < para.n; ++i)
            for (int j = 0; j < para.n; ++j) {
                area[para.n * i + j] = new IntVar(store, ("A" + i) + j, 0, 1);
                allVars.add(area[para.n * i + j]);
            }

        // constraint for the number of residential
        IntVar nResi = new IntVar(store, "nResi", para.n_residential, para.n_residential);
        store.impose(new SumInt(area, "==", nResi));

        // min/max point of the poins list
        int minPoints =  0xfffffff;
        int maxPoints = -0xfffffff;
        for (int p : para.point_distribution) {
            minPoints = minPoints < p ? minPoints : p;
            maxPoints = maxPoints > p ? maxPoints : p;
        }

        IntVar[] colPoints = new IntVar[para.n];
        IntVar[] colSum = new IntVar[para.n];
        for (int col = 0; col < para.n; ++col) {
            colPoints[col] = new IntVar(store, "colPoints" + col, minPoints, maxPoints);
            colSum[col] = new IntVar(store, "colSum" + col, 0, para.n);
            allVars.add(colSum[col]);

            // constraint for number of residential in each column
            IntVar[] tmp = new IntVar[para.n];
            for (int row = 0; row < para.n; ++row)
                tmp[row] = area[para.n * row + col];
            store.impose(new SumInt(tmp, "==", colSum[col]));

            // constraint for making distributions of residentials in columns in lex order
            if (col != 0) {
                IntVar[] tmpCol0 = new IntVar[para.n];
                IntVar[] tmpCol1 = new IntVar[para.n];
                for (int row = 0; row < para.n; ++row) {
                    tmpCol0[row] = area[para.n * row + (col - 1)];
                    tmpCol1[row] = area[para.n * row + (col    )];
                }
                store.impose(new LexOrder(tmpCol0, tmpCol1, false));
            }

            // constraint for converting numbers of residentials to points of column
            store.impose(new ElementInteger(colSum[col], para.point_distribution, colPoints[col], -1));
        }

        IntVar[] rowPoints = new IntVar[para.n];
        IntVar[] rowSum = new IntVar[para.n];
        for (int row = 0; row < para.n; ++row) {
            rowPoints[row] = new IntVar(store, "rowPoints" + row, minPoints, maxPoints);
            rowSum[row] = new IntVar(store, "rowSum" + row, 0, para.n);
            allVars.add(rowSum[row]);

            // constraint for number of residential in each row
            IntVar[] tmp = new IntVar[para.n];
            for (int col = 0; col < para.n; ++col)
                tmp[col] = area[para.n * row + col];
            store.impose(new SumInt(tmp, "==", rowSum[row]));

            // constraint for making distributions of residentials in rows in lex order
            if (row != 0) store.impose(new XlteqY(rowSum[row - 1], rowSum[row]));

            // constraint for converting numbers of residentials to points of row
            store.impose(new ElementInteger(rowSum[row], para.point_distribution, rowPoints[row], -1));
        }        

        IntVar[] allPoints = new IntVar[2 * para.n];
        for (int i = 0; i < para.n; ++i) allPoints[         i] = colPoints[i];
        for (int i = 0; i < para.n; ++i) allPoints[para.n + i] = rowPoints[i];

        // constraint for collecting points
        IntVar cost = new IntVar(store, "quality", minPoints * 2 * para.n, maxPoints * 2 * para.n);
        store.impose(new SumInt(allPoints, "==", cost));

        IntVar[] freeVars = new IntVar[allVars.size()];
        allVars.toArray(freeVars);

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select =
                //new SimpleSelect<IntVar>(freeVars, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>());
                new SimpleSelect<IntVar>(freeVars, new MostConstrainedStatic<IntVar>(), new IndomainMin<IntVar>());
                //new SimpleSelect<IntVar>(freeVars, new MostConstrainedDynamic<IntVar>(), new IndomainMin<IntVar>());
                //new SimpleSelect<IntVar>(freeVars, new SmallestDomain<IntVar>(), new IndomainMiddle<IntVar>());
        boolean result = search.labeling(store, select, cost);

        if (result) {
            System.out.println("Solution: ");
            for (int i = 0; i < para.n; ++i) {
                for (int j = 0; j < para.n; ++j)
                    System.out.print(area[para.n * i + j] + " ");
                System.out.println("");
            }
            System.out.println(cost + " * -1");
        } else {
            System.out.println("!Infeasible!");
            System.out.println(store);
        }
    }

    public static void main(String[] args) {
        System.out.println("========== Data 1 ==========");
        search(urban_1.getInstance());

        System.out.println("========== Data 2 ==========");
        search(urban_2.getInstance());

        System.out.println("========== Data 3 ==========");
        search(urban_3.getInstance());

        System.out.println("========== Data 4 ==========");
        search(urban_4.getInstance());

        System.out.println("========== Data 5 ==========");
        search(urban_5.getInstance());

        System.out.println("========== Data 6 ==========");
        search(urban_6.getInstance());

        System.out.println("========== Data 7 ==========");
        search(urban_7.getInstance());
    }
}
