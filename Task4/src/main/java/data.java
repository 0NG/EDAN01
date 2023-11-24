public class data {
    public int del_add;
    public int del_mul;
    
    public int number_add;
    public int number_mul;
    public int n;

    public int[] last;
    public int[] add;
    public int[] mul;
    public int[][] dependencies;

    public data(int del_add, int del_mul, int number_add, int number_mul, int n, int[] last, int[] add, int[] mul, int[][] dependencies) {
        this.del_add = del_add;
        this.del_mul = del_mul;
        this.number_add = number_add;
        this.number_mul = number_mul;
        this.n = n;
        this.last = last;
        this.add = add;
        this.mul = mul;
        this.dependencies = dependencies;
    }
}
