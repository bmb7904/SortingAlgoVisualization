package bernardi;

/**
 * A custom class to store the "moves" made in an array during a sorting algorithm.
 */
public class Moves
{
    private int i;
    private int j;
    private boolean afterPartition;
    private int lo;
    private int hi;
    private int k;

    public Moves(int i, int j)
    {
        this.i = i;
        this.j = j;
        this.afterPartition = false;
    }

    // Second constructor
    public Moves(int lo, int hi, int k, boolean isAfter)
    {
        this.i = -1;
        this.j = -1;

        this.k = k;
        this.lo = lo;
        this.hi = hi;
        this.afterPartition = isAfter;
    }
    public int getI()
    {
        return this.i;
    }
    public int getJ()
    {
        return this.j;
    }
    public void setI(int i)
    {
        this.i = i;
    }
    public void setJ(int j)
    {
        this.j = j;
    }
    public void setAfterPartition(boolean b)
    {
        this.afterPartition = b;
    }
    public boolean getAfterPartition()
    {
        return this.afterPartition;
    }
    public void setLo(int lo)
    {
        this.lo = lo;
    }
    public int getLo()
    {
        return this.lo;
    }
    public void setHi(int hi)
    {
        this.hi = hi;
    }
    public int getHi()
    {
        return this.hi;
    }

    public void setK(int k)
    {
        this.k = k;
    }
    public int getK()
    {
        return this.k;
    }

    @Override
    public String toString()
    {
        String str = "i = " + i + " : j = " + j + " ";
        return str;
    }
}
