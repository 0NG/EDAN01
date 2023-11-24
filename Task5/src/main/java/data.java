public class data {
    // row/column lenght
    int n;
  
    // number of commercial lots in the area
    int n_commercial;
  
    // number of residential lots in the area
    int n_residential;
  
    // index is the number of residential lots in the row/column
    int[] point_distribution;

    public data(int n, int n_commercial, int n_residential, int[] point_distribution) {
        this.n = n;
        this.n_commercial = n_commercial;
        this.n_residential = n_residential;
        this.point_distribution = point_distribution;
    }
}
