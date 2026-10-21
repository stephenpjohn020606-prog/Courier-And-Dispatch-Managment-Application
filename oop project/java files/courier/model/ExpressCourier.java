package courier.model;

import courier.exceptions.ValidationException;

public class ExpressCourier extends Courier{
    private static final double Base_rate=12.00;
    private static final double Rate_per_kg=4.00;
    private static final double Express_surcharge=5.00;
    private int Guaranteedhours;

    public ExpressCourier(String Courierid,Customer Sender,Customer Receiver,
                          String Destinationaddress,String Destinationcity,double Weight,
                          String Parceltype,String Bookingdate,int Guaranteedhours) throws ValidationException{
        super(Courierid,Sender,Receiver,Destinationaddress,Destinationcity,Weight,Parceltype,Bookingdate);
        if(Guaranteedhours>0){
            this.Guaranteedhours=Guaranteedhours;
        }else{
            this.Guaranteedhours=24;
        }
        Setdeliverycharge(Calculatedeliverycharge());
    }
    public int Getguaranteedhours(){
        return Guaranteedhours;
    }
    @Override
    public double Calculatedeliverycharge(){
        return Base_rate+(Getweight()*Rate_per_kg)+Express_surcharge;
    }
    @Override
    public String Getprioritylevel(){
        return "High (Express "+Guaranteedhours+"h Delivery)";
    }
    @Override
    public String Gethandlinginstructions(){
        return "Priority queue. Store in Express Bay near dock.";
    }
    @Override
    public String Getcouriertype(){
        return "Express";
    }
}
