package courier.model;

import courier.exceptions.ValidationException;

public class FragileCourier extends Courier{
    private static final double Base_rate=8.00;
    private static final double Rate_per_kg=3.00;
    private static final double Fragile_surcharge=8.00;
    private String Fragilecategory;

    public FragileCourier(String Courierid,Customer Sender,Customer Receiver,
                          String Destinationaddress,String Destinationcity,double Weight,
                          String Parceltype,String Bookingdate,String Fragilecategory) throws ValidationException{
        super(Courierid,Sender,Receiver,Destinationaddress,Destinationcity,Weight,Parceltype,Bookingdate);
        if(Fragilecategory!=null&&!Fragilecategory.trim().isEmpty()){
            this.Fragilecategory=Fragilecategory.trim();
        }else{
            this.Fragilecategory="General Fragile";
        }
        Setdeliverycharge(Calculatedeliverycharge());
    }
    public String Getfragilecategory(){
        return Fragilecategory;
    }
    @Override
    public double Calculatedeliverycharge(){
        return Base_rate+(Getweight()*Rate_per_kg)+Fragile_surcharge;
    }
    @Override
    public String Getprioritylevel(){
        return "Special Care ("+Fragilecategory+")";
    }
    @Override
    public String Gethandlinginstructions(){
        return "FRAGILE: Padded bottom shelving. Do not stack.";
    }
    @Override
    public String Getcouriertype(){
        return "Fragile";
    }
}
