class urban_3 {
    public static int n = 7;
    public static int n_commercial = 20;
    public static int n_residential = 29;
    public static int[] point_distribution = {-7, -6, -5, -4, 4, 5, 6, 7};

    private static data instance = new data(n, n_commercial, n_residential, point_distribution);
    private urban_3() {}
    public static data getInstance() { return instance; }
}