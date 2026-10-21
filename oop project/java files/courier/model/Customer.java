package courier.model;

import courier.exceptions.ValidationException;

public class Customer{
    private int Customerid;
    private String Name;
    private String Phone;
    private String Email;
    private String Address;

    public Customer(String Name,String Phone) throws ValidationException{
        this(0,Name,Phone,"","");
    }
    public Customer(String Name,String Phone,String Email,String Address) throws ValidationException{
        this(0,Name,Phone,Email,Address);
    }
    public Customer(int Customerid,String Name,String Phone,String Email,String Address) throws ValidationException{
        this.Customerid=Customerid;
        Setname(Name);
        Setphone(Phone);
        Setemail(Email);
        Setaddress(Address);
    }
    public int Getcustomerid(){
        return Customerid;
    }
    public void Setcustomerid(int Customerid){
        this.Customerid=Customerid;
    }
    public String Getname(){
        return Name;
    }
    public void Setname(String Name) throws ValidationException{
        if(Name==null||Name.trim().isEmpty()){
            throw new ValidationException("Customer name cannot be empty.");
        }
        this.Name=Name.trim();
    }
    public String Getphone(){
        return Phone;
    }
    public void Setphone(String Phone) throws ValidationException{
        if(Phone==null||Phone.trim().isEmpty()){
            throw new ValidationException("Customer phone cannot be empty.");
        }
        this.Phone=Phone.trim();
    }
    public String Getemail(){
        return Email;
    }
    public void Setemail(String Email){
        if(Email!=null){
            this.Email=Email.trim();
        }else{
            this.Email="";
        }
    }
    public String Getaddress(){
        return Address;
    }
    public void Setaddress(String Address){
        if(Address!=null){
            this.Address=Address.trim();
        }else{
            this.Address="";
        }
    }
    @Override
    public String toString(){
        return Name+" ("+Phone+")";
    }
}
