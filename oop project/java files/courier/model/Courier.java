package courier.model;

import courier.exceptions.InvalidStatusTransitionException;
import courier.exceptions.InvalidStorageLocationException;
import courier.exceptions.ValidationException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Courier implements Storable,Dispatchable{
    private String Courierid;
    private Customer Sender;
    private Customer Receiver;
    private String Destinationaddress;
    private String Destinationcity;
    private double Weight;
    private String Parceltype;
    private String Bookingdate;
    private CourierStatus Status;
    private StorageLocation Storagelocation;
    private DispatchRecord Dispatchrecord;
    private double Deliverycharge;

    public Courier(String Courierid,Customer Sender,Customer Receiver,
                   String Destinationaddress,String Destinationcity,double Weight,
                   String Parceltype,String Bookingdate) throws ValidationException{
        Setcourierid(Courierid);
        Setsender(Sender);
        Setreceiver(Receiver);
        Setdestinationaddress(Destinationaddress);
        Setdestinationcity(Destinationcity);
        Setweight(Weight);
        Setparceltype(Parceltype);
        if(Bookingdate!=null&&!Bookingdate.trim().isEmpty()){
            this.Bookingdate=Bookingdate;
        }else{
            this.Bookingdate=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        }
        this.Status=CourierStatus.STORED;
    }

    public abstract double Calculatedeliverycharge();
    public abstract String Getprioritylevel();
    public abstract String Gethandlinginstructions();
    public abstract String Getcouriertype();

    @Override
    public boolean Hasassignedstorage(){
        return Storagelocation!=null;
    }
    @Override
    public void Assignstoragelocation(StorageLocation Location) throws InvalidStorageLocationException{
        if(Location==null){
            throw new InvalidStorageLocationException("Storage location cannot be null.");
        }
        this.Storagelocation=Location;
        Location.Setoccupied(true);
        Location.Setcurrentcourierid(this.Courierid);
    }
    @Override
    public void Clearstoragelocation(){
        if(this.Storagelocation!=null){
            this.Storagelocation.Setoccupied(false);
            this.Storagelocation.Setcurrentcourierid(null);
            this.Storagelocation=null;
        }
    }
    @Override
    public StorageLocation Getstoragelocation(){
        return Storagelocation;
    }
    @Override
    public boolean Canbedispatched(){
        return this.Status==CourierStatus.READY_FOR_DISPATCH||this.Status==CourierStatus.STORED;
    }
    @Override
    public void Recorddispatch(DispatchRecord Record) throws InvalidStatusTransitionException{
        if(Record==null){
            throw new InvalidStatusTransitionException("Dispatch record cannot be null.");
        }
        this.Dispatchrecord=Record;
        this.Status=CourierStatus.DISPATCHED;
        Clearstoragelocation();
    }
    @Override
    public DispatchRecord Getdispatchrecord(){
        return Dispatchrecord;
    }
    public void Updatestatus(CourierStatus Newstatus) throws InvalidStatusTransitionException{
        if(Newstatus==null){
            throw new InvalidStatusTransitionException("Status cannot be null.");
        }
        if(!this.Status.Cantransitionto(Newstatus)){
            throw new InvalidStatusTransitionException(
                "Cannot transition courier from '"+this.Status.Getdisplayname()+"' to '"+Newstatus.Getdisplayname()+
                "': Invalid workflow progression. Progression must follow Stored -> Ready for Dispatch -> Dispatched -> Delivered."
            );
        }
        this.Status=Newstatus;
    }
    public String Getcourierid(){
        return Courierid;
    }
    public void Setcourierid(String Id) throws ValidationException{
        if(Id==null||Id.trim().isEmpty()){
            throw new ValidationException("Courier ID cannot be empty.");
        }
        this.Courierid=Id.trim();
    }
    public Customer Getsender(){
        return Sender;
    }
    public void Setsender(Customer Sender) throws ValidationException{
        if(Sender==null){
            throw new ValidationException("Sender cannot be null.");
        }
        this.Sender=Sender;
    }
    public Customer Getreceiver(){
        return Receiver;
    }
    public void Setreceiver(Customer Receiver) throws ValidationException{
        if(Receiver==null){
            throw new ValidationException("Receiver cannot be null.");
        }
        this.Receiver=Receiver;
    }
    public String Getdestinationaddress(){
        return Destinationaddress;
    }
    public void Setdestinationaddress(String Addr) throws ValidationException{
        if(Addr==null||Addr.trim().isEmpty()){
            throw new ValidationException("Address cannot be empty.");
        }
        this.Destinationaddress=Addr.trim();
    }
    public String Getdestinationcity(){
        return Destinationcity;
    }
    public void Setdestinationcity(String City) throws ValidationException{
        if(City==null||City.trim().isEmpty()){
            throw new ValidationException("City cannot be empty.");
        }
        this.Destinationcity=City.trim();
    }
    public double Getweight(){
        return Weight;
    }
    public void Setweight(double Weight) throws ValidationException{
        if(Weight<=0.0){
            throw new ValidationException("Weight must be greater than 0 kg.");
        }
        this.Weight=Weight;
    }
    public String Getparceltype(){
        return Parceltype;
    }
    public void Setparceltype(String Type) throws ValidationException{
        if(Type==null||Type.trim().isEmpty()){
            throw new ValidationException("Parcel type cannot be empty.");
        }
        this.Parceltype=Type.trim();
    }
    public String Getbookingdate(){
        return Bookingdate;
    }
    public void Setbookingdate(String Date){
        this.Bookingdate=Date;
    }
    public CourierStatus Getstatus(){
        return Status;
    }
    public void Setstatus(CourierStatus Status){
        this.Status=Status;
    }
    public void Setstoragelocationdirectly(StorageLocation Loc){
        this.Storagelocation=Loc;
    }
    public void Setdispatchrecorddirectly(DispatchRecord Rec){
        this.Dispatchrecord=Rec;
    }
    public double Getdeliverycharge(){
        return Deliverycharge;
    }
    public void Setdeliverycharge(double Charge){
        this.Deliverycharge=Charge;
    }
    @Override
    public String toString(){
        return "["+Courierid+"] "+Getcouriertype()+" | To: "+Receiver.Getname()+" | Status: "+Status;
    }
}
