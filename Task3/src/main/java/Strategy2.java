import org.jacop.constraints.Not;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.constraints.*;
import org.jacop.core.FailException;
import org.jacop.core.IntDomain;
import org.jacop.core.IntVar;
import org.jacop.core.Store;

public class Strategy2 {

    boolean trace = false;

    // Store used in search
    Store store;

    // Defines varibales to be printed when solution is found
    IntVar[] variablesToReport;

    // It represents current depth of store used in search.
    int depth = 0;

    // It represents the cost value of currently best solution for FloatVar cost.
    public int costValue = IntDomain.MaxInt;

    // It represents the cost variable.
    public IntVar costVariable = null;

    // Number of visited nodes
    public long N = 0;

    // Number of failed nodes excluding leave nodes
    public long failedNodes = 0;

    public Strategy2(Store s) {
        store = s;
    }

    public boolean label(IntVar[] vars) {
        N++;
    
        if (trace) {
            for (int i = 0; i < vars.length; i++) 
                System.out.print(vars[i] + " ");
            System.out.println();
        }

        ChoicePoint choice = null;
        boolean consistent;

        // Instead of imposing constraint just restrict bounds-1 since costValue is the cost of last solution
        if (costVariable != null) {
            try {
                if (costVariable.min() <= costValue - 1)
                    costVariable.domain.in(store.level, costVariable, costVariable.min(), costValue - 1);
                else
                    return false;
            } catch (FailException f) {
                return false;
            }
        }

        consistent = store.consistency();
        if (!consistent) {
            // Failed leaf of the search tree
            return false;
        } else { // consistent
            if (vars.length == 0) {
                // solution found; no more variables to label
                // update cost if minimization
                if (costVariable != null) {
                    costValue = costVariable.min();
                }

                reportSolution();

                return costVariable == null; // true is satisfiability search and false if minimization
            }

            choice = new ChoicePoint(vars);
            levelUp();

            // choice point imposed.
            store.impose(choice.getConstraint());
            consistent = label(choice.getSearchVariables());

            if (consistent) {
                levelDown();
                return true;
            } else {
                failedNodes++;
                restoreLevel();
                store.impose(new Not(choice.getConstraint()));

                // negated choice point imposed.
                consistent = label(vars);
                levelDown();
                if (consistent) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    void levelDown() {
        store.removeLevel(depth);
        store.setLevel(--depth);
    }

    void levelUp() {
        store.setLevel(++depth);
    }

    void restoreLevel() {
        store.removeLevel(depth);
        store.setLevel(store.level);
    }

    public void reportSolution() {
        System.out.println("Nodes visited: " + N);

        if (costVariable != null)
            System.out.println ("Cost is " + costVariable);

        for (int i = 0; i < variablesToReport.length; i++) 
            System.out.print (variablesToReport[i] + " ");
        System.out.println ("\n---------------");
    }

    public void setVariablesToReport(IntVar[] v) {
        variablesToReport = v;
    }

    public void setCostVariable(IntVar v) {
        costVariable = v;
    }

    public class ChoicePoint {

        IntVar var;
        IntVar[] searchVariables;
    
        public ChoicePoint(IntVar[] v) {
            var = selectVariable(v);
        }
    
        public IntVar[] getSearchVariables() {
            return searchVariables;
        }
    
        /**
         * example variable selection; input order
         */ 
        IntVar selectVariable(IntVar[] v) {
            if (v.length != 0) {
                int isFixed = v[0].min() == v[0].max() ? 1 : 0;
                searchVariables = new IntVar[v.length - isFixed];
                for (int i = 0; i < v.length - isFixed; ++i)
                    searchVariables[i] = v[i + isFixed]; 
                return v[0];
            } else {
                System.err.println("Zero length list of variables for labeling");
                return new IntVar(store);
            }
        }
    
        /**
         * example constraint assigning a selected value
         */
        public PrimitiveConstraint getConstraint() {
            int middle = (var.min() + var.max() + 1) / 2;
            return new XgteqC(var, middle);
        }
    }
}
