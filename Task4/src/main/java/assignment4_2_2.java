public class assignment4_2_2 {
    public static int del_add = 1;
    public static int del_mul = 2;
    
    public static int number_add = 2;
    public static int number_mul = 2;
    public static int n = 28;
    
    public static int[] last = {27,28};
    
    public static int[] add = {9,10,11,12,13,14,19,20,25,26,27,28};
    
    public static int[] mul = {1,2,3,4,5,6,7,8,15,16,17,18,21,22,23,24};
    
    public static int[][] dependencies = {
        {9},
        {9},
        {10},
        {10},
        {11},
        {11},
        {12},
        {12},
        {27},
        {28},
        {13},
        {14},
        {16,17},
        {15,18},
        {19},
        {19},
        {20},
        {20},
        {22,23},
        {21,24},
        {25},
        {25},
        {26},
        {26},
        {27},
        {28},
        {},
        {},
    };

    private static data instance = new data(del_add, del_mul, number_add, number_mul, n, last, add, mul, dependencies);
    private assignment4_2_2() {}
    public static data getInstance() { return instance; }
}
