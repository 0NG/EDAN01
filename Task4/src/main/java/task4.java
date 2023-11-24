import java.util.ArrayList;
import java.util.List;

import org.jacop.core.*;
import org.jacop.constraints.*;
import org.jacop.constraints.diffn.*;
import org.jacop.search.*;

public class task4 {
    private static void one2zero(data para) {
        for (int i = 0; i < para.last.length; ++i) --para.last[i];
        for (int i = 0; i < para.add.length; ++i)  --para.add[i];
        for (int i = 0; i < para.mul.length; ++i)  --para.mul[i];

        for (int i = 0; i < para.dependencies.length; ++i)
            for (int j = 0; j < para.dependencies[i].length; ++j)
                --para.dependencies[i][j];
    }

    public static void search(data para, int sid) {
        one2zero(para);

        Store store = new Store();
        List<IntVar> allVars = new ArrayList<IntVar>(4 * para.n);

        boolean[] aOrm = new boolean[para.n];
        IntVar[][] operations = new IntVar[para.n][];
        int intervalMax = para.del_add * para.add.length + para.del_mul * para.mul.length;

        for (int addi : para.add) {
            IntVar[] op = new IntVar[4];

            op[0] = new IntVar(store, "node_" + addi + "_o0", 0, intervalMax);
            op[1] = new IntVar(store, "node_" + addi + "_o1", 0, para.number_add - 1);
            op[2] = new IntVar(store, "node_" + addi + "_l0", para.del_add, para.del_add);
            op[3] = new IntVar(store, "node_" + addi + "_l1", 1, 1);

            for (int i = 0; i < 4; ++i) allVars.add(op[i]);

            operations[addi] = op;
            aOrm[addi] = true;
        };

        for (int muli : para.mul) {
            IntVar[] op = new IntVar[4];

            op[0] = new IntVar(store, "node_" + muli + "_o0", 0, intervalMax);
            op[1] = new IntVar(store, "node_" + muli + "_o1", para.number_add, para.number_add + para.number_mul - 1);
            op[2] = new IntVar(store, "node_" + muli + "_l0", para.del_mul, para.del_mul);
            op[3] = new IntVar(store, "node_" + muli + "_l1", 1, 1);

            for (int i = 0; i < 4; ++i) allVars.add(op[i]);

            operations[muli] = op;
            aOrm[muli] = false;
        };

        store.impose(new Diffn(operations));

        for (int i = 0; i < para.dependencies.length; ++i)
            for (int son : para.dependencies[i]) {
                //store.impose(new XgtY(operations[son][0], operations[i][0]));
                store.impose(new XplusClteqZ(operations[i][0], aOrm[i] ? para.del_add : para.del_mul, operations[son][0]));
            }
        
        IntVar cost = new IntVar(store,
                                 "cost",
                                 Math.max(para.del_add * para.add.length / para.number_add, para.del_mul * para.mul.length / para.number_mul),
                                 intervalMax
                                );
        allVars.add(cost);

        IntVar[] last = new IntVar[para.last.length];
        for (int i = 0; i < para.last.length; ++i) 
            last[i] = operations[para.last[i]][0];

        store.impose(new Max(last, cost));

        //System.out.println(store);

        IntVar[] freeVars = new IntVar[allVars.size()];
        allVars.toArray(freeVars);

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = 
            sid == 0 ?
                new SimpleSelect<IntVar>(freeVars, new SmallestDomain<IntVar>(), new IndomainMin<IntVar>()) :
                new SimpleSelect<IntVar>(freeVars, new MostConstrainedStatic<IntVar>(), new IndomainMin<IntVar>());
        boolean result = search.labeling(store, select, cost);

        if (result) {
            System.out.println("Solution: ");
            for (int i = 0; i < operations.length; ++i)
                System.out.println("(" + operations[i][0] + ", " + operations[i][1] + ", " + operations[i][2] + ", " + operations[i][3] + ")");
            System.out.println(cost + " + 1");
        } else {
            System.out.println("!Infeasible!");
            System.out.println(store);
        }
    }

    public static void main(String[] args) {
        System.out.println("========== Data 1_1 ==========");
        search(assignment4_1_1.getInstance(), 0);

        System.out.println("========== Data 1_2 ==========");
        search(assignment4_1_2.getInstance(), 1);

        System.out.println("========== Data 1_3 ==========");
        search(assignment4_1_3.getInstance(), 1);

        System.out.println("========== Data 2_2 ==========");
        search(assignment4_2_2.getInstance(), 1);

        System.out.println("========== Data 2_3 ==========");
        search(assignment4_2_3.getInstance(), 1);

        System.out.println("========== Data 2_4 ==========");
        search(assignment4_2_4.getInstance(), 1);
    }
}
