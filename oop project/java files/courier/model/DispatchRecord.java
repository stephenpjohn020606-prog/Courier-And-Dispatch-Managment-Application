package courier.model;

import courier.exceptions.ValidationException;

public class DispatchRecord{
    private String Dispatchid;
    private String Courierid;
    private String Dispatchdate;
    private String Vehiclenumber;
    private String Drivername;
    private String Destination;
    private String Remarks;

    public DispatchRecord(String Dispatchid,String Courierid,String Dispatchdate,
                          String Vehiclenumber,String Drivername,String Destination,String Remarks)
            throws ValidationException{
        if(Dispatchid==null||Dispatchid.trim().isEmpty()){
            throw new ValidationException("Dispatch ID cannot be empty.");
        }
        if(Courierid==null||Courierid.trim().isEmpty()){
            throw new ValidationException("Courier ID cannot be empty.");
        }
        this.Dispatchid=Dispatchid.trim();
        this.Courierid=Courierid.trim();
        this.Dispatchdate=Dispatchdate;
        if(Vehiclenumber!=null){
            this.Vehiclenumber=Vehiclenumber.trim();
        }else{
            this.Vehiclenumber="";
        }
        if(Drivername!=null){
            this.Drivername=Drivername.trim();
        }else{
            this.Drivername="";
        }
        if(Destination!=null){
            this.Destination=Destination.trim();
        }else{
            this.Destination="";
        }
        if(Remarks!=null){
            this.Remarks=Remarks.trim();
        }else{
            this.Remarks="";
        }
    }
    public String Getdispatchid(){return Dispatchid;}
    public String Getcourierid(){return Courierid;}
    public String Getdispatchdate(){return Dispatchdate;}
    public String Getvehiclenumber(){return Vehiclenumber;}
    public String Getdrivername(){return Drivername;}
    public String Getdestination(){return Destination;}
    public String Getremarks(){return Remarks;}
    @Override
    public String toString(){
        return "["+Dispatchid+"] Dispatched via "+Vehiclenumber+" ("+Drivername+") on "+Dispatchdate;
    }
}
