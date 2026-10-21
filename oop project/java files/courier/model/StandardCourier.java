package courier.model;

import courier.exceptions.ValidationException;

public class StandardCourier extends Courier{
    private static final double Base_rate=5.00;
    private static final double Rate_per_kg=2.50;

    public StandardCourier(String Courierid,Customer Sender,Customer Receiver,
                           String Destinationaddress,String Destinationcity,double Weight,
                           String Parceltype,String Bookingdate) throws ValidationException{
        super(Courierid,Sender,Receiver,Destinationaddress,Destinationcity,Weight,Parceltype,Bookingdate);
        Setdeliverycharge(Calculatedeliverycharge());
    }
    @Override
    public double Calculatedeliverycharge(){
        return Base_rate+(Getweight()*Rate_per_kg);
    }
    @Override
    public String Getprioritylevel(){
        return "Normal (Standard)";
    }
    @Override
    public String Gethandlinginstructions(){
        return "Standard shelving on warehouse racks.";
    }
    @Override
    public String Getcouriertype(){
        return "Standard";
    }
}
