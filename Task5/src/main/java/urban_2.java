class urban_2{
    public static int n = 5;
    public static int n_commercial = 7;
    public static int n_residential = 18;
    public static int[] point_distribution = {-5, -4, -3, 3, 4, 5};

    private static data instance = new data(n, n_commercial, n_residential, point_distribution);
    private urban_2() {}
    public static data getInstance() { return instance; }
}