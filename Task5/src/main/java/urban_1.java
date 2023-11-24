// all lots are occupied
// n*n = n_commercial + n_residential


class urban_1 {
    // row/column lenght
    public static int n = 5;
  
    // number of commercial lots in the area
    public static int n_commercial = 13;
  
    // number of residential lots in the area
    public static int n_residential = 12;
  
    // index is the number of residential lots in the row/column
    public static int[] point_distribution = {-5, -4, -3, 3, 4, 5};

    private static data instance = new data(n, n_commercial, n_residential, point_distribution);
    private urban_1() {}
    public static data getInstance() { return instance; }
}