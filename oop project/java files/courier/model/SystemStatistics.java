package courier.model;

public class SystemStatistics{
    private final int Totalcouriers;
    private final int Storedcount;
    private final int Readycount;
    private final int Dispatchedcount;
    private final int Deliveredcount;
    private final int Totalstorageslots;
    private final int Occupiedstorageslots;
    private final int Freestorageslots;
    private final double Totalrevenue;

    public SystemStatistics(int Totalcouriers,int Storedcount,
                            int Readycount,int Dispatchedcount,int Deliveredcount,
                            int Totalstorageslots,int Occupiedstorageslots,int Freestorageslots,
                            double Totalrevenue){
        this.Totalcouriers=Totalcouriers;
        this.Storedcount=Storedcount;
        this.Readycount=Readycount;
        this.Dispatchedcount=Dispatchedcount;
        this.Deliveredcount=Deliveredcount;
        this.Totalstorageslots=Totalstorageslots;
        this.Occupiedstorageslots=Occupiedstorageslots;
        this.Freestorageslots=Freestorageslots;
        this.Totalrevenue=Totalrevenue;
    }
    public int Gettotalcouriers(){return Totalcouriers;}
    public int Getstoredcount(){return Storedcount;}
    public int Getreadycount(){return Readycount;}
    public int Getdispatchedcount(){return Dispatchedcount;}
    public int Getdeliveredcount(){return Deliveredcount;}
    public int Gettotalstorageslots(){return Totalstorageslots;}
    public int Getoccupiedstorageslots(){return Occupiedstorageslots;}
    public int Getfreestorageslots(){return Freestorageslots;}
    public double Gettotalrevenue(){return Totalrevenue;}
}
